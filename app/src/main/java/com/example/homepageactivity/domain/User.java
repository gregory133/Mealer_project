package com.example.homepageactivity.domain;

public abstract class User {

    private String firstName;
    private String lastName;
    private String address;

    public User() {}

    public User(String firstName, String lastName, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
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
}
