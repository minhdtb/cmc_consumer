package com.minhdtb.cmc.models;

import javax.persistence.*;

@Entity
@Table(name = "cmc_black_list")
@NamedQueries({
        @NamedQuery(name = "CmcBlackList.findAll", query = "SELECT m FROM CmcBlackList m"),
        @NamedQuery(name = "CmcBlackList.findById", query = "SELECT m FROM CmcBlackList m WHERE m.id = :id"),
        @NamedQuery(name = "CmcBlackList.findByRemoteHost", query = "SELECT m FROM CmcBlackList m WHERE m.remoteHost = :remoteHost")
})
public class CmcBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "remote_host")
    private String remoteHost;

    public String getId() {
        return id;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
}