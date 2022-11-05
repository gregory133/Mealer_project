package com.example.homepageactivity.domain;

public class ComplaintMessage extends Message {
    private String cookUID; //the cook accused by the complaint

    public ComplaintMessage() {}

    public ComplaintMessage(String senderUID, String subject, String body, String cookUID){
        //The recipient is the admin
        super(senderUID, "BShaMMSVKIYn8tDFy5eKokv5ubE3", subject, body);
        this.cookUID = cookUID;
    }

    public String getCookUID() {
        return cookUID;
    }

}
