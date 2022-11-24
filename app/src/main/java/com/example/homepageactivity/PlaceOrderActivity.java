package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PlaceOrderActivity extends AppCompatActivity {

    private TextView mealName;
    private TextView mealType;
    private TextView cuisineType;
    private TextView mealPrice;

    private TextView cardholderName;
    private TextView cardNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        mealName=findViewById(R.id.mealName);
        mealType=findViewById(R.id.mealType);
        cuisineType=findViewById(R.id.cuisineType);
        mealPrice=findViewById(R.id.mealPrice);
        cardholderName=findViewById(R.id.cardholderName);
        cardNumber=findViewById(R.id.cardNumber);

        loadText();
    }

    private void loadText(){
        mealName.setText(mealName.getText()+getIntent().getStringExtra("mealName"));
        mealType.setText(mealType.getText()+getIntent().getStringExtra("mealType"));
        cuisineType.setText(cuisineType.getText()+getIntent().getStringExtra("cuisineType"));
        mealPrice.setText(mealPrice.getText()+getIntent().getStringExtra("mealPrice"));

        cardholderName.setText(cardholderName.getText()+getIntent().getStringExtra("cardholderName"));

        String cardNum=getIntent().getStringExtra("cardNumber");
        String lastDigits=cardNum.substring(cardNum.length() - 4);
        String muffledCardNumber="**** **** **** "+lastDigits;
        cardNumber.setText(cardNumber.getText()+muffledCardNumber);

    }


}