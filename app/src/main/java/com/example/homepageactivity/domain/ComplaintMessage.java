package com.example.homepageactivity.domain;

public class ComplaintMessage extends Message {
    private String cookUID; //the cook accused by the complaint
    private String cookEmail;

    public ComplaintMessage() {}

    public ComplaintMessage(String senderUID, String senderEmail, String adminUID, String subject, String body, String cookUID, String cookEmail){
        //The recipient is the admin
        super(senderUID, senderEmail, adminUID, subject, body);
        this.cookUID = cookUID;
        this.cookEmail = cookEmail;
    }

    public String getCookUID() {
        return cookUID;
    }
    public String getCookEmail() {
        return cookEmail;
    }
}
