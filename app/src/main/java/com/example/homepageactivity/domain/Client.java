package com.example.homepageactivity.domain;

public class Client extends User{

    private CreditCardInformation payment;

    public Client() { }

    public Client(String firstName, String lastName, String address, CreditCardInformation payment){
        super(firstName, lastName, address);
        this.payment = payment;
    }

    public CreditCardInformation getPayment() {
        return payment;
    }
}
