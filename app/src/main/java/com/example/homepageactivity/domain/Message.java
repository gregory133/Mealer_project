package com.example.homepageactivity.domain;

public abstract class Message {
    String senderEmail;
    String recipientEmail;
    String subject;
    String bodyText;

    public Message(String sender, String recipient, String subject, String body){
        senderEmail = sender;
        recipientEmail = recipient;
        this.subject = subject;
        bodyText = body;
    }
}
