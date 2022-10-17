package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        System.out.println("to account");
    }

    /**
     * Logs the user out of the application and returns LOGOUT_CODE
     */
    public void onClickLogoutButton(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Logout", 1);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}