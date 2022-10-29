package com.example.homepageactivity.domain;

public abstract class Message {
    private User sender;
    private String recipientEmail;
    private String subject;
    private String bodyText;

    public Message() {}

    public Message(User sender, String recipient, String subject, String body){
        this.sender = sender;
        recipientEmail = recipient;
        this.subject = subject;
        bodyText = body;
    }

    public User getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }
}
