package com.example.homepageactivity.domain;

public class Message {
    private String senderUID;
    private String senderEmail;
    //This will be the admin in the case of ComplaintMessage. It will initially be null
    //then it will be non-null once admin selects it.
    private String recipientUID;
    private String subject;
    private String bodyText;

    //a message is considered archived once some sort of action has been taken on it
    //It is still stored in the database for future reference but it will no longer
    //be displayed in the list of message.
    private boolean archived;

    public Message() {}

    public Message(String senderUID, String senderEmail, String recipientUID, String subject, String bodyText){
        this.senderUID = senderUID;
        this.senderEmail = senderEmail;
        this.recipientUID = recipientUID;
        this.subject = subject;
        this.bodyText = bodyText;
        archived = false;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getSenderEmail() {
        return senderEmail;
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

    public boolean isArchived() {
        return archived;
    }
}
