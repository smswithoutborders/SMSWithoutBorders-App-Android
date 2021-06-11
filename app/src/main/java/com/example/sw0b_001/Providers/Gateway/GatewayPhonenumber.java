package com.example.sw0b_001.Providers.Gateway;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value={"number"}, unique = true)})
public class GatewayPhonenumber {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="country_code")
    public String countryCode;

    public String getCountryCode() {
        return this.countryCode;
    }

    public GatewayPhonenumber setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public long getId() {
        return id;
    }

    @ColumnInfo(name="type")
    String type;

    public String getType() {
        return type;
    }

    public GatewayPhonenumber setType(String type) {
        this.type = type;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public GatewayPhonenumber setNumber(String number) {
        this.number = number;
        return this;
    }

    public String getIsp() {
        return isp;
    }

    public GatewayPhonenumber setIsp(String isp) {
        this.isp = isp;
        return this;
    }

    @ColumnInfo(name="number")
    String number;

    @ColumnInfo(name="default")
    boolean isDefault = false;

    public boolean isDefault() {
        return this.isDefault;
    }

    public GatewayPhonenumber setDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    @ColumnInfo(name="isp")
    String isp;
    public GatewayPhonenumber(String type, String number, String isp, boolean isDefault) {
        this.type = type;
        this.number = number;
        this.isp = isp;
        this.isDefault = isDefault;
    }

    public GatewayPhonenumber() {}
}
