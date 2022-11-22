package com.example.homepageactivity.domain;

public class Client extends User{

    private CreditCardInformation payment;

    public Client() { }

    public Client(String firstName, String lastName, String address, String emailAddress, CreditCardInformation payment){
        super(firstName, lastName, address, emailAddress);
        this.payment = payment;
    }

    public CreditCardInformation getPayment() {
        return payment;
    }
}
