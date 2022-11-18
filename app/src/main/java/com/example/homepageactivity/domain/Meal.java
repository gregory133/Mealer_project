package com.example.homepageactivity.domain;

import android.media.Image;

public class Meal {

    //Image picture;
    private String mealName;
    private String mealType;
    private String cuisineType;
    private String[] ingredients;
    private String[] allergens;
    private double price;
    private String description;
    private String cookUID;

    public Meal(){};

    public Meal(String mealName, String mealType, String cuisineType, String[] ingredients,
                String[] allergens, double price, String description, String cookUID){
        this.mealName = mealName;
        this.mealType = mealType;
        this.cuisineType = cuisineType;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.price = price;
        this.description = description;
        this.cookUID = cookUID;
    }

    public String getMealName(){ return mealName; }
    public String getMealType(){ return mealType; }
    public String getCuisineType() { return cuisineType; }
    public String[] getIngredients() { return ingredients; }
    public String[] getAllergens() {return allergens; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getCookUID() { return cookUID; }

}
