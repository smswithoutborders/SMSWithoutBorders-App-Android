package com.example.sw0b_001.ListPlatforms.Emails;

import com.example.sw0b_001.CustomHelpers;
import com.example.sw0b_001.R;

import java.util.ArrayList;

public class EmailThreads {
    private String subject;
    private String subjectSub;
    private String bottomRightText = "";
    private String topRightText = "";
    private int id;
    private int image = R.drawable.googleg_standard_color_18;
    private ArrayList<EmailCustomMessage> messages;

    public EmailThreads add(EmailCustomMessage message) {
        messages.add(message);
        return this;
    }

    public ArrayList<EmailCustomMessage> getMessages() {
        return messages;
    }

    public EmailThreads setTopRightText(String topRightText) {
        this.topRightText = topRightText;
        return this;
    }

    public EmailThreads setBottomRightText(String bottomRightText) {
        this.bottomRightText = bottomRightText;
        return this;
    }

    public EmailThreads setSubjectSub(String subjectSub) {
        this.subjectSub = subjectSub;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailThreads setImage(int image) {
        this.image = image;
        return this;
    }

    public String getSubjectSub() {
        return subjectSub;
    }

    public int getImage() {
        return this.image;
    }

    public String getBottomRightText() {
        return bottomRightText;
    }

    public String getTopRightText() {
        return topRightText;
    }

    public EmailThreads setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public int getId() {
        return id;
    }

    public EmailThreads setId(int id) {
        this.id = id;
        return this;
    }


    static public ArrayList<String> getAll() {
        return new ArrayList<String>(){};
    }
}
