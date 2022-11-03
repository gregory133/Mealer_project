package com.example.homepageactivity.domain;

public class ComplaintMessage extends Message {
    private String cookUID; //the cook accused by the complaint

    //a complaint is considered archived once an admin has taken some sort of action.
    //It is still stored in the database for future reference but it will no longer
    //be displayed in the list of available complaints.
    private boolean archived;

    public ComplaintMessage() {}

    public ComplaintMessage(String senderUID, String subject, String body, String cookUID){
        //The recipient is the admin, and this will be non-null once admin selects it
        super(senderUID, null, subject, body);
        this.cookUID = cookUID;
        archived = false;
    }

    public String getCookUID() {
        return cookUID;
    }

    public boolean isArchived() {
        return archived;
    }
}
