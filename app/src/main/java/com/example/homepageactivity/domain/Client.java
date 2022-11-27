package com.example.homepageactivity.domain;

public class Client extends User{

    private final static String role = "Client";
    private CreditCardInformation payment;

    public Client() { }

    public Client(String firstName, String lastName, String address, String emailAddress, CreditCardInformation payment){
        super(firstName, lastName, address, emailAddress, role);
        this.payment = payment;
    }

    public CreditCardInformation getPayment() {
        return payment;
    }
}
