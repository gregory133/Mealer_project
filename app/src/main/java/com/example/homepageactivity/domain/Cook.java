package com.example.homepageactivity.domain;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Cook extends User{

    private Image voidChequePicture;
    private String shortDescription;
    private int bannedUntil;
    private boolean banned;
    private ArrayList<Meal> menu;

    public Cook() {}

    public Cook(String firstName, String lastName, String address, Image voidChequePicture, String shortDescription) {
        super(firstName, lastName, address);
        this.voidChequePicture = voidChequePicture;
        this.shortDescription = shortDescription;
        bannedUntil = 0;
        banned = false;
        menu = new ArrayList<Meal>();
    }

    public Image getVoidChequePicture() {
        return voidChequePicture;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public int getBannedUntil() {
        return bannedUntil;
    }

    public boolean isBanned() {
        return banned;
    }

    public ArrayList<Meal> getMenu() {
        return menu;
    }
}
