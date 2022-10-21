package com.example.homepageactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.emailTextEdit);
        passwordTextView = findViewById(R.id.editTextPassword);

        //Realtime database testing
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("messages");
        mDatabase.setValue("Hello, World");
    }

    public void onClickLoginButton(View view){
        //Check both boxes are filled
        if(!validateLoginInputs()){
            //Toast.makeText(this, "Please Enter your Email and Password", Toast.LENGTH_LONG).show();
            return;
        }
        loginUserAttempt();
    }
    /**
     * Clears email and password boxes on login
     */
    private void ClearLoginInfoBoxes(){
        emailTextView.setText("");
        passwordTextView.setText("");
    }

    private boolean validateLoginInputs(){
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        return !email.equals("") && !password.equals("");
    }

    private void loginUserAttempt()
    {
        // Take the value of two edit texts in Strings
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(
                    @NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful()){
                    loginAttemptSuccess();
                }else{
                    loginAttemptFailure();
                }
            }
        });
    }

    private void loginAttemptSuccess(){
        //Don't want to leave there info sitting around after logging in
        ClearLoginInfoBoxes();

        Intent intent = new Intent(getApplicationContext(), UserHomepageActivity.class);
        startActivity(intent);
    }

    private void loginAttemptFailure(){
        Toast.makeText(this, "Your Email or Password is Incorrect", Toast.LENGTH_LONG).show();
    }

    public void onClickRegister(View view){
        Intent intent=new Intent(this, ChooseRole.class);
        startActivity(intent);
    }
}