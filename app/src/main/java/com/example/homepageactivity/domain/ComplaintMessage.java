package com.example.homepageactivity.domain;

public class ComplaintMessage extends Message {
    private String cookUID; //the cook accused by the complaint

    public ComplaintMessage() {}

    public ComplaintMessage(String senderUID, String subject, String body, String cookUID){
        //The recipient is the admin, and this will be non-null once admin selects it
        super(senderUID, null, subject, body);
        this.cookUID = cookUID;
    }

    public String getCookUID() {
        return cookUID;
    }
}
