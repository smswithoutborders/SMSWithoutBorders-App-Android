package com.example.sw0b_001.Providers.Emails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.sw0b_001.Helpers.CustomHelpers;

@Entity
public class EmailThreads {
    @ColumnInfo(name="subject")
    public String subject;

    @ColumnInfo(name="recipient")
    public String recipient;

    @ColumnInfo(name="mdate")
    public String mdate;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="image")
    public int image;

    @ColumnInfo(name="platform_id")
    public long platformId;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.setImage(CustomHelpers.getLetterImage(recipient.charAt(0)));
        this.recipient = recipient;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(long platformId) {
        this.platformId = platformId;
    }
}
