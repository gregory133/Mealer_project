package com.example.homepageactivity.domain;

public class CreditCardInformation {

    private long cardNumber;
    private int cvv;
    private int expMonth;
    private int expYear;
    private String cardHolderName;

    public CreditCardInformation(long cN, int cvv, int eDM, int eDY, String cHN){
        cardNumber = cN;
        this.cvv = cvv;
        expMonth = eDM;
        expYear = eDY;
        cardHolderName = cHN;
    }

    public CreditCardInformation(){
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public int getCvv() {
        return cvv;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }
}
