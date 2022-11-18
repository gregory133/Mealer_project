package com.example.homepageactivity.domain;

import android.media.Image;

public class Meal {

    private String name;
    private String description;
    private String cuisineType;
    private String mealType;
    private String[] ingredients;
    private String[] allergens;
    private double price;
    private boolean offered;
    private String cookUID;

    public Meal() {}

    public Meal(String name, String description, String cuisineType, String mealType, String[] ingredients,
                String[] allergens, double price, String cookUID, boolean offered){
        this.name = name;
        this.description = description;
        this.cuisineType = cuisineType;
        this.mealType = mealType;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.price = price;
        this.cookUID = cookUID;
        this.offered = offered;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public String getMealType() {
        return mealType;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getAllergens() {
        return allergens;
    }

    public double getPrice() {
        return price;
    }

    public String getCookUID() {
        return cookUID;
    }

    public boolean isOffered() {
        return offered;
    }
}
