package com.example.sw0b_001.ListPlatforms.Emails;

import java.util.ArrayList;

public class EmailCustomMessage {
    private String recipient;
    private String subject;
    private String body;
    private String datetime;
    private String status;
    private int id;
    private int image;

    public EmailCustomMessage setImage(int image) {
        this.image = image;
        return this;
    }

    public int getImage() {
        return this.image;
    }

    public String getRecipient() {
        return recipient;
    }

    public EmailCustomMessage setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailCustomMessage setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public EmailCustomMessage setBody(String body) {
        this.body = body;
        return this;
    }

    public String getDatetime() {
        return datetime;
    }

    public EmailCustomMessage setDatetime(String datetime) {
        this.datetime = datetime;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public EmailCustomMessage setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getId() {
        return id;
    }

    public EmailCustomMessage setId(int id) {
        this.id = id;
        return this;
    }

    public int getThreadId() {
        return threadId;
    }

    public EmailCustomMessage setThreadId(int threadId) {
        this.threadId = threadId;
        return this;
    }

    private int threadId;

    static public ArrayList<String> getAll() {
        return new ArrayList<String>(){};
    }
}
