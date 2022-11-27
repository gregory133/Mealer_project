package com.example.homepageactivity.domain;

public class Admin extends User {
    private final static String role = "Admin";

    public Admin() {}

    public Admin(String firstName, String lastName, String address, String emailAddress) {
        super(firstName, lastName, address, emailAddress, role);
    }
}
