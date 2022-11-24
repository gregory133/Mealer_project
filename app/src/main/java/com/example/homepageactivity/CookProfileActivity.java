package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CookProfileActivity extends AppCompatActivity {

    private TextView firstNameText;
    private TextView lastNameText;
    private TextView emailText;
    private TextView descText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_profile);

        firstNameText=findViewById(R.id.firstName);
        lastNameText=findViewById(R.id.lastName);
        emailText=findViewById(R.id.email);
        descText=findViewById(R.id.description);

        loadTextViews();


    }

    private void loadTextViews(){

        Intent intent=getIntent();

        firstNameText.setText(firstNameText.getText()+intent.getStringExtra("firstName"));
        lastNameText.setText(lastNameText.getText()+intent.getStringExtra("lastName"));
        descText.setText(descText.getText()+intent.getStringExtra("desc"));
    }
}