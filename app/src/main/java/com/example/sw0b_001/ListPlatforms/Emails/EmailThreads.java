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

    private void setSubject(int id) {

    }

    private void setMessages(int id) {

    }

    public ArrayList<Message> getAll() {
        return this.messages;
    }

    public void setMessage(Message message) {
        this.messages.add(message);
    }


    public class Message{
        public void Message() {

        }
    }
}
