package com.example.homepageactivity.domain;

public class ComplaintMessage extends Message {
    private Cook cook; //the cook that received the complaint
    private Admin admin; //the admin that selected the complaint
    private boolean archived; //a complaint is considered archived once an admin has taken some sort of action.
                              //It is still stored in the database for future reference but it will no longer
                              //be displayed in the list of available complaints.

    public ComplaintMessage() {}

    public ComplaintMessage(User sender, String recipient, String subject, String body, Cook cook){
        super(sender, recipient, subject, body);
        this.cook = cook;
        //admin will be set once admin selects
        this.admin = null;
        archived = false;
    }

    public Cook getCook() {
        return cook;
    }

    public Admin getAdmin() {
        return admin;
    }

    public boolean isArchived() {
        return archived;
    }

}
