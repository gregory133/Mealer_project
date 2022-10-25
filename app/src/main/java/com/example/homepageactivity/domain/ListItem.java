package com.example.homepageactivity.domain;

public class ListItem {

    private String subject;
    private String description;

    public ListItem(String subject, String description){
        this.subject=subject;
        this.description=description;
    }

    public String getSubject(){
        return subject;
    }
    public String getDescription(){
        return description;
    }

}
