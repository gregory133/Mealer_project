package com.example.homepageactivity.domain;

import android.media.Image;

import com.google.firebase.Timestamp;

import java.util.Calendar;

public class Meal {

    //Image picture;
    private String mealName;
    private String description;
    private String cuisineType;
    private String mealType;
    private String ingredients;
    private String allergens;
    private double price;
    private int ratingTotal;
    private int numRatings;
    private boolean offered;
    private String cookUID;
    private Timestamp bannedUntil;

    public Meal() {}

    public Meal(String mealName, String description, String cuisineType, String mealType, String ingredients,
                String allergens, double price, String cookUID, boolean offered){
        this.mealName = mealName;
        this.description = description;
        this.cuisineType = cuisineType;
        this.mealType = mealType;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.price = price;
        this.cookUID = cookUID;
        this.offered = offered;
        bannedUntil = new Timestamp(Calendar.getInstance().getTime());
        this.ratingTotal = 0;
        this.numRatings = 0;
    }

    public String getMealName(){ return mealName; }
    public String getDescription() { return description; }
    public String getCuisineType() { return cuisineType; }
    public String getMealType(){ return mealType; }
    public String getIngredients() { return ingredients; }
    public String getAllergens() {return allergens; }
    public double getPrice() { return price; }
    public String getCookUID() { return cookUID; }
    public boolean getOffered() {return offered; }
    public Timestamp getBannedUntil() {return bannedUntil; }
    public void setOffered(boolean offered){ this.offered = offered; }
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

    public String toString(){
        return mealName+", "+description+", "+cuisineType+", "+mealName+", "+ingredients+", "+allergens+", "+price+", "+cookUID+", "+offered;
    }
}
