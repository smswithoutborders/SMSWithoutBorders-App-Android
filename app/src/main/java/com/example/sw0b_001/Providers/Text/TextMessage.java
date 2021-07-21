package com.example.sw0b_001.Providers.Text;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sw0b_001.Helpers.CustomHelpers;

@Entity
public class TextMessage {

    public String getBody() {
        return body;
    }

    public TextMessage setBody(String body) {
        this.body = body;
        return this;
    }

    public String getDatetime() {
        return datetime;
    }

    public TextMessage setDatetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public TextMessage setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getImage() {
        return image;
    }

    public TextMessage setImage(int image) {
        this.image = image;
        return this;
    }

    public long getId() {
        return id;
    }

    public TextMessage setId(long id) {
        this.id = id;
        return this;
    }

    public long getPlatformId() {
        return this.platformId;
    }

    public TextMessage setPlatformId(long platformId) {
        this.platformId = platformId;
        return this;
    }

    @ColumnInfo(name="datetime")
    public String datetime;

    @ColumnInfo(name="status")
    public String status;

    @ColumnInfo(name="image")
    public int image;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="body")
    public String body;

    @ColumnInfo(name="platform_id")
    public long platformId;
}
