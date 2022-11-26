package com.example.homepageactivity.domain;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Cook extends User{

    private Image voidChequePicture;
    private String shortDescription;
    private int ratingTotal;
    private int numRatings;

    public Cook() {}

    public Cook(String firstName, String lastName, String address, String emailAddress, Image voidChequePicture, String shortDescription) {
        super(firstName, lastName, address, emailAddress);
        this.voidChequePicture = voidChequePicture;
        this.shortDescription = shortDescription;
        this.ratingTotal = 0;
        this.numRatings = 0;
    }

    public Image getVoidChequePicture() {
        return voidChequePicture;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    public int getNumRatings() {
        return numRatings;
    }
    public int getRatingTotal() {
        return ratingTotal;
    }

    public void setRatingTotal(int ratingTotal) {
        this.ratingTotal = ratingTotal;
    }
    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public String getDesc(){return shortDescription;}
}
