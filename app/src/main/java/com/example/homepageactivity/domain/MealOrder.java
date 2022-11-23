package com.example.homepageactivity.domain;

public class MealOrder {
    int approved;   //0 = default, -1 = rejected, 1 = approved
    int delivered;  //0 = default, -1 = canceled, 1 = delivered //can only set delivered if approved == 1
    int received;    //0 = default, -1 = lost, 1 = received //can only set received if delivered == 1
    String cookUID;
    String cookEmail;
    String clientUID;
    String clientEmail;
    String mealUID;

    public MealOrder(){}
    public MealOrder(String cookUID, String cookEmail, String clientUID, String clientEmail, String mealUID){
        this.cookUID=cookUID;
        this.cookEmail=cookEmail;
        this.clientUID=clientUID;
        this.clientEmail=clientEmail;
        this.mealUID=mealUID;
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
}
