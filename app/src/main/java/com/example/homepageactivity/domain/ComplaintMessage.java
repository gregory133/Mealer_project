package com.example.homepageactivity.domain;

public class ComplaintMessage extends Message {
    private Cook cook; //the cook that received the complaint
    private Admin admin; //the admin that selected the complaint

    public ComplaintMessage(String sender, String recipient, String subject, String body, Cook cook){
        super(sender, recipient, subject, body);
        this.cook = cook;
        //admin will be set once admin selects
        this.admin = null;
    }



}
