package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        titleText=findViewById(R.id.title);

        Bundle extras=getIntent().getExtras();
        String mode=extras.getString("TYPE");

        String titleString=(String)titleText.getText();
        titleText.setText(titleString+" "+mode);

    }

    public void onClickRegisterButton(View view){
        String firstName = (((EditText) findViewById(R.id.firstNameEdit)).getText()).toString();
        String lastName = (((EditText) findViewById(R.id.lastNameEdit)).getText()).toString();
        String email = (((EditText) findViewById(R.id.emailEdit)).getText()).toString();
        String address = (((EditText) findViewById(R.id.addressEdit)).getText()).toString();
        String password = (((EditText) findViewById(R.id.passwordEdit)).getText()).toString();
        String confirmPassword = (((EditText) findViewById(R.id.confirmPasswordEdit)).getText()).toString();

        if (validate(firstName, lastName, email, address, password, confirmPassword)){

            Bundle extras=getIntent().getExtras();
            String userType=extras.getString("TYPE");

            Intent intent=new Intent(this, UserHomepageActivity.class);
            //Change this to client class

            if(userType == "Cook"){
                intent=new Intent(this, UserHomepageActivity.class);
            }

            intent.putExtra("FirstName", firstName);
            intent.putExtra("LastName", lastName);
            intent.putExtra("Email", email);
            intent.putExtra("Address", address);
            intent.putExtra("Password", password);
            intent.putExtra("ConfirmPassword", confirmPassword);

            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Invalid/Insufficient Information Given", Toast.LENGTH_LONG).show();
        }


    }

    /**
     * rules for valid data entries given
     * @param firstName
     * @param lastName
     * @param email
     * @param address
     * @param password
     * @param confirmPassword
     * @return
     */
    private boolean validate(String firstName, String lastName, String email, String address, String password, String confirmPassword){
        if (firstName.equals("") || lastName.equals("") || email.equals("") || address.equals("") || password.equals("") || confirmPassword.equals("")){
            return false;
        }
        if (!password.equals(confirmPassword)){
            return false;
        }
        return true;
    }

}