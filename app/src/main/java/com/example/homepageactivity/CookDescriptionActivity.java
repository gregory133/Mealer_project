package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CookDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_description);
    }

    public void onClickRegisterButton(View view){
        String description = (((EditText) findViewById(R.id.description)).getText()).toString();

        if (validate(description)){

            Bundle extras=getIntent().getExtras();

            Intent intent=new Intent(this, UserHomepageActivity.class);
            //CHANGE TO NEW CLASS FOR VOID CHEQUE

            //carry forward all previous User data
            intent.putExtra("FirstName", extras.getString("FirstName"));
            intent.putExtra("LastName", extras.getString("LastName"));
            intent.putExtra("Email", extras.getString("Email"));
            intent.putExtra("Address", extras.getString("Address"));
            intent.putExtra("Password", extras.getString("Password"));
            intent.putExtra("ConfirmPassword", extras.getString("ConfirmPassword"));

            //carry forward the description
            intent.putExtra("Description", description);

            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Invalid/Insufficient Information Given", Toast.LENGTH_LONG).show();
        }


    }
    private boolean validate(String description){
        if (description.equals("")){
            Toast.makeText(this, "Description Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}