package com.example.sw0b_001.ListPlatforms.Emails;

import java.util.ArrayList;

public class EmailThreads {

    int id;
    String subject;
    ArrayList<Message> messages;

    public EmailThreads() {
        setSubject(id);
        setMessages(id);
    }

    public EmailThreads(int id) {
        this.id = id;
        setSubject(id);
        setMessages(id);
    }


    public String getSubject() {
        return "";
    }

    public String getEmail() {
        return "";
    }

    public ArrayList<EmailThreads> getAll() {
        return new ArrayList<EmailThreads>(){};
    }

    private void setSubject(int id) {

    }

    private void setMessages(int id) {

    }

    public void setMessage(Message message) {
        this.messages.add(message);
    }


    public class Message{
        public void Message() {

        }
    }
}
