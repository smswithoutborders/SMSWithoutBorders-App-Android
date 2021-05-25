package com.example.sw0b_001.Providers.Gateway;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GatewayPhonenumber {
    @PrimaryKey(autoGenerate = true)
    public long id;

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

    @ColumnInfo(name="isp")
    String isp;
    public GatewayPhonenumber(String type, String number, String isp) {
        this.type = type;
        this.number = number;
        this.isp = isp;
    }

    public GatewayPhonenumber() {}
}
