package com.minhdtb.cmc;

import com.google.gson.Gson;
import com.minhdtb.cmc.models.CmcBlackList;
import com.minhdtb.cmc.models.CmcRawData;
import com.rabbitmq.client.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class Main {
    private static final String QUEUE_NAME = "message";
    private static final String MQTT_TOPIC_MESSAGE = "message";
    private static final String MQTT_TOPIC_BLACK_LIST = "blacklist";
    private static final String MQTT_HOST = "wss://mqtt.esminer.com:8083";
    private static final String MQTT_CLIENT_ID = "consumer";

    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream input = null;
        final Gson gson = new Gson();
        final Logger logger = LoggerFactory.getLogger(Main.class);

        try {
            logger.info("Starting...");

            /* load config */
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            input = loader.getResourceAsStream("application.properties");
            properties.load(input);

            /* init db */
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("consumer");
            final EntityManager entityManager = entityManagerFactory.createEntityManager();

            /* init mqtt */
            MemoryPersistence persistence = new MemoryPersistence();
            final MqttClient mqttClient = new MqttClient(MQTT_HOST, MQTT_CLIENT_ID, persistence);
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setUserName("minhdtb");
            mqttConnectOptions.setPassword("123456".toCharArray());
            logger.info("Connecting to broker: " + MQTT_HOST);
            mqttClient.connect(mqttConnectOptions);
            logger.info("Connected.");

            /* init rabbitmq */
            String host = properties.getProperty("host");
            int messageTtl = Integer.parseInt(properties.getProperty("x-message-ttl"));
            int maxLength = Integer.parseInt(properties.getProperty("x-max-length"));

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("x-message-ttl", messageTtl);
            params.put("x-max-length", maxLength);

            channel.queueDeclare(QUEUE_NAME, true, false, false, params);

            Consumer consumer = new DefaultConsumer(channel) {

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    CmcRawData rawData = gson.fromJson(message, CmcRawData.class);

                    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                    mqttMessage.setQos(2);

                    try {
                        logger.info("Sending message...");
                        mqttClient.publish(MQTT_TOPIC_MESSAGE, mqttMessage);
                        logger.info("Sent.");

                        TypedQuery<CmcBlackList> query = entityManager.createNamedQuery("CmcBlackList.findByRemoteHost", CmcBlackList.class);
                        query.setParameter("remoteHost", rawData.getRemoteHost());
                        CmcBlackList cmcBlackList = query.getSingleResult();
                        if (cmcBlackList != null) {
                            logger.info("Sending black list...");
                            mqttClient.publish(MQTT_TOPIC_BLACK_LIST, mqttMessage);
                            logger.info("Sent.");
                        }
                    } catch (javax.persistence.NoResultException ignored) {

                    } catch (Exception e) {
                        logger.error("error", e);
                    }

                    rawData.setCreatedDate(new Date());
                    logger.info("Saving data...");
                    entityManager.getTransaction().begin();
                    entityManager.persist(rawData);
                    entityManager.getTransaction().commit();
                    logger.info("Saved.");
                }
            };

            channel.basicConsume(QUEUE_NAME, true, consumer);
            logger.info("Started.");
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("error", e);
                }
            }
        }
    }
}
