package com.example.homepageactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private TextView titleText;

    private Button Btn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        titleText=findViewById(R.id.title);

        Bundle extras=getIntent().getExtras();
        String mode=extras.getString("TYPE");

        String titleString=(String)titleText.getText();
        titleText.setText(titleString+" "+mode);

        //firebase integration

        mAuth = FirebaseAuth.getInstance();
        Btn = findViewById(R.id.Next);

        // Set on Click Listener on Registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
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

            Intent intent=new Intent(this, ClientPaymentActivity.class);

            if(userType == "Cook"){
                intent=new Intent(this, CookDescriptionActivity.class);
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

    private void registerNewUser() {

        String firstName = (((EditText) findViewById(R.id.firstNameEdit)).getText()).toString();
        String lastName = (((EditText) findViewById(R.id.lastNameEdit)).getText()).toString();
        String email = (((EditText) findViewById(R.id.emailEdit)).getText()).toString();
        String address = (((EditText) findViewById(R.id.addressEdit)).getText()).toString();
        String password = (((EditText) findViewById(R.id.passwordEdit)).getText()).toString();
        String confirmPassword = (((EditText) findViewById(R.id.confirmPasswordEdit)).getText()).toString();


        // Validations for input email and password
        if (!validate(firstName,lastName,email,address,password,confirmPassword)) {
            Toast.makeText(this, "Invalid/Insufficient Information Given", Toast.LENGTH_LONG).show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // if the user created intent to login activity
                            Intent intent
                                    = new Intent(RegistrationActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                        }
                        else {

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                        }
                    }
                });
    }

}