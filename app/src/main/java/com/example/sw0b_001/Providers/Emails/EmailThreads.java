package com.example.sw0b_001.Providers.Emails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmailThreads {
    @ColumnInfo(name="subject")
    public String subject;

    @ColumnInfo(name="recipient")
    public String recipient;

    public String getSubject() {
        return subject;
    }

    public EmailThreads setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getRecipient() {
        return recipient;
    }

    public EmailThreads setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public String getMdate() {
        return mdate;
    }

    public EmailThreads setMdate(String mdate) {
        this.mdate = mdate;
        return this;
    }

    public long getId() {
        return id;
    }

    public EmailThreads setId(long id) {
        this.id = id;
        return this;
    }

    public int getImage() {
        return image;
    }

    public EmailThreads setImage(int image) {
        this.image = image;
        return this;
    }

    @ColumnInfo(name="mdate")
    public String mdate;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="image")
    public int image;

    public long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(long platformId) {
        this.platformId = platformId;
    }

    @ColumnInfo(name="platform_id")
    public long platformId;
}
