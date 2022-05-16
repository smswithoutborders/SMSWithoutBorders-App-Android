package com.example.sw0b_001.Models.EncryptedContent;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EncryptedContent {

    String encryptedContent;

    @ColumnInfo(name="platform_id")
    long platformId;

    @PrimaryKey(autoGenerate = true)
    long id;

    public String getGatewayClientMSISDN() {
        return gatewayClientMSISDN;
    }
    public void setGatewayClientMSISDN(String gatewayClientMSISDN) {
        this.gatewayClientMSISDN = gatewayClientMSISDN;
    }

    @ColumnInfo(name="gateway_client_MSISDN")
    String gatewayClientMSISDN;

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(long platformId) {
        this.platformId = platformId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    String platformName;

    String type;

    long date;

    public EncryptedContent() {
    }
}
