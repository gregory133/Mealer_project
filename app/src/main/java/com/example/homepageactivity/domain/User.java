package com.example.homepageactivity.domain;

import java.util.Date;

public abstract class User {

    private String firstName;
    private String lastName;
    private String address;
    private String emailAddress;
    private String role;
    private boolean banned; //True if banned permanently. This is false if the user is not banned or is banned temporarily

    //Initially, this variable will be the date (to the millisecond) the account was created.
    //As the checker only checks to see if this is AFTER the current date the user signs in, there
    //will be no issue unless the user is actually banned temporarily.
    private Date bannedUntil;

    public User() {}

    public User(String firstName, String lastName, String address, String emailAddress, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.emailAddress = emailAddress;
        this.role = role;
        bannedUntil = new Date();
        banned = false;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public Date getBannedUntil() {
        return bannedUntil;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isBanned() {
        return banned;
    }
}
