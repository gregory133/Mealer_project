package com.example.homepageactivity.domain;

import java.util.Date;

public class MealOrder {
    int approved;   //0 = default, -1 = rejected, 1 = approved
    int delivered;  //0 = default, -1 = canceled, 1 = delivered //can only set delivered if approved == 1
    int received;    //0 = default, -1 = lost, 1 = received //can only set received if delivered == 1
    String cookUID;
    String cookEmail;
    String clientUID;
    String clientEmail;
    String mealUID;
    String mealName;
    int rating;
    Date pickupTime;

    public MealOrder(){}
    public MealOrder(String cookUID, String cookEmail, String clientUID, String clientEmail, String mealUID, String mealName, Date pickupTime){
        this.cookUID=cookUID;
        this.cookEmail=cookEmail;
        this.clientUID=clientUID;
        this.clientEmail=clientEmail;
        this.mealUID=mealUID;
        this.mealName=mealName;
        this.pickupTime=pickupTime;
        this.approved=0;
        this.delivered=0;
        this.received=0;
        this.rating=0;
    }

    public int getApproved() {
        return approved;
    }

    public int getDelivered() {
        return delivered;
    }

    public int getReceived() {
        return received;
    }
    public String getCookUID() {
        return cookUID;
    }
    public String getCookEmail() {
        return cookEmail;
    }

    public String getClientUID() {
        return clientUID;
    }
    public String getClientEmail() {
        return clientEmail;
    }

    public String getMealUID() {
        return mealUID;
    }
    public String getMealName() {
        return mealName;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public int getRating() {
        return rating;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }
    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }
    public void setReceived(int received) {
        this.received = received;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
