package com.example.homepageactivity.domain;

public class Message {
    private String senderUID;
    //This will be the admin in the case of ComplaintMessage. It will initially be null
    //then it will be non-null once admin selects it.
    private String recipientUID;
    private String subject;
    private String bodyText;

    public Message() {}

    public Message(String senderUID, String recipientUID, String subject, String bodyText){
        this.senderUID = senderUID;
        this.recipientUID = recipientUID;
        this.subject = subject;
        this.bodyText = bodyText;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public String getRecipientUID() {
        return recipientUID;
    }
}
