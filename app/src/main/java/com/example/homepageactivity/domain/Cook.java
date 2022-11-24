package com.example.homepageactivity.domain;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Cook extends User{

    private Image voidChequePicture;
    private String shortDescription;
    private ArrayList<Meal> menu;

    public Cook() {}

    public Cook(String firstName, String lastName, String address, String emailAddress, Image voidChequePicture, String shortDescription) {
        super(firstName, lastName, address, emailAddress);
        this.voidChequePicture = voidChequePicture;
        this.shortDescription = shortDescription;
        menu = new ArrayList<Meal>();
    }

    public Image getVoidChequePicture() {
        return voidChequePicture;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public ArrayList<Meal> getMenu() {
        return menu;
    }

    public String getDesc(){return shortDescription;}
}
