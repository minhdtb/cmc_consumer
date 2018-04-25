package com.minhdtb.cmc.models;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Entity
@Table(name = "cmc_raw_data")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "CmcRawData.findAll", query = "SELECT m FROM CmcRawData m"),
        @NamedQuery(name = "CmcRawData.findById", query = "SELECT m FROM CmcRawData m WHERE m.id = :id"),
        @NamedQuery(name = "CmcRawData.findByName", query = "SELECT m FROM CmcRawData m WHERE m.name = :name")
})
public class CmcRawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "domain")
    private String domain;

    @Column(name = "public_ip")
    private String publicIp;

    private String location;

    @Column(name = "remote_host")
    private String remoteHost;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "created_date")
    private Date createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
