package com.example.homepageactivity.domain;

public abstract class Message {
    private String senderEmail;
    private String recipientEmail;
    private String subject;
    private String bodyText;

    public Message(String sender, String recipient, String subject, String body){
        senderEmail = sender;
        recipientEmail = recipient;
        this.subject = subject;
        bodyText = body;
    }

    public String getSenderEmail(){
        return senderEmail;
    }
    public String getRecipientEmail(){
        return recipientEmail;
    }
    public String getSubject(){
        return subject;
    }
    public String getBodyText(){
        return bodyText;
    }



}
