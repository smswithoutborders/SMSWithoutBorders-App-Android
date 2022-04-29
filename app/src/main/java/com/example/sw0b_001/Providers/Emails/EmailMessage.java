package com.example.sw0b_001.Providers.Emails;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EmailMessage {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name="to")
    public String to;

    @ColumnInfo(name="cc")
    public String cc;

    @ColumnInfo(name="bcc")
    public String bcc;

    @ColumnInfo(name="subject")
    public String subject;

    @ColumnInfo(name="body")
    public String body;

    @ColumnInfo(name="datetime")
    public String datetime;

    @ColumnInfo(name="thread_id")
    public long threadId;


    public void setTo(String to) {
        // this.setImage(CustomHelpers.getLetterImage(to.charAt(0)));
        this.to = to;
    }

    public void setCC(String cc) { this.cc = cc; }

    public void setBCC(String bcc) { this.bcc = bcc; }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getDatetime() {
        return datetime;
    }

    public long getThreadId() {
        return threadId;
    }

    public long getId() {
        return id;
    }
}
