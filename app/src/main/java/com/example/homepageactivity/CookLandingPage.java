package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CookLandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_landing_page);
    }

    public void onClickAddMeal(View view) {
        Intent intent = new Intent(getApplicationContext(), MealAddActivity.class);
        startActivity(intent);
    }
}