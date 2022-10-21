package com.example.homepageactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MakeUserActivity extends AppCompatActivity {

    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_user);

        titleText=findViewById(R.id.title);

        Bundle extras=getIntent().getExtras();
        String mode=extras.getString("TYPE");

        String titleString=(String)titleText.getText();
        titleText.setText(titleString+" "+mode);
    }
    
    public void onClickMakeUserNextButton(View view){
        String firstName = (((EditText) findViewById(R.id.firstNameEdit)).getText()).toString();
        String lastName = (((EditText) findViewById(R.id.lastNameEdit)).getText()).toString();
        String email = (((EditText) findViewById(R.id.emailEdit)).getText()).toString();
        String address = (((EditText) findViewById(R.id.addressEdit)).getText()).toString();
        String password = (((EditText) findViewById(R.id.passwordEdit)).getText()).toString();
        String confirmPassword = (((EditText) findViewById(R.id.confirmPasswordEdit)).getText()).toString();

        if (!validate(firstName, lastName, email, address, password, confirmPassword)) return;

        Bundle extras=getIntent().getExtras();
        String userType=extras.getString("TYPE");
        Intent intent=new Intent(this, ClientPaymentActivity.class);

        if(userType.contains("Cook")){
            intent=new Intent(this, CookDescriptionActivity.class);
        }

        intent.putExtra("FirstName", firstName);
        intent.putExtra("LastName", lastName);
        intent.putExtra("Email", email);
        intent.putExtra("Address", address);
        intent.putExtra("Password", password);

        finish();
        startActivity(intent);
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
        if (firstName.equals("")){
            Toast.makeText(this, "First Name Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (lastName.equals("")){
            Toast.makeText(this, "Last Name Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (email.equals("")){
            Toast.makeText(this, "Email Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.equals("")){
            Toast.makeText(this, "Password Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (confirmPassword.equals("")){
            Toast.makeText(this, "Confirm Password Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this, "Confirm Password does not match Password", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}