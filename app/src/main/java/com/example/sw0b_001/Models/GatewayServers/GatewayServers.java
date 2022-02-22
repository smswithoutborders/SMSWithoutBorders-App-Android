package com.example.sw0b_001.Models.GatewayServers;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value={"url"}, unique = true)})
public class GatewayServers {


    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String publicKey;

    @NonNull
    private String url;

    @NonNull
    private String protocol;

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public String getUrl() {
        return this.url;
    }

    public String getProtocol() {
        return this.protocol;
    }
    public long getId() {
        return this.id;
    }

    public GatewayServers(){}
}
