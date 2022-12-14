package com.example.homepageactivity.domain;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

public class Cook extends User{
    private final static String role = "Cook";
    private String voidChequePicture;
    private String shortDescription;
    private int ratingTotal;
    private int numRatings;
    private int mealsSold;

    public Cook() {}

    public Cook(String firstName, String lastName, String address, String emailAddress, String voidChequePicture, String shortDescription) {
        super(firstName, lastName, address, emailAddress, role);
        this.voidChequePicture = voidChequePicture;
        this.shortDescription = shortDescription;
        this.ratingTotal = 0;
        this.numRatings = 0;
        this. mealsSold = 0;
    }

    public String getVoidChequePicture() {
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
    public int getMealsSold() {
        return mealsSold;
    }

    public void setRatingTotal(int ratingTotal) {
        this.ratingTotal = ratingTotal;
    }
    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public String getDesc(){return shortDescription;}
}
