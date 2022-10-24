package com.example.homepageactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.emailTextEdit);
        passwordTextView = findViewById(R.id.editTextPassword);

        // Firestore Database testing
        /*
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Bonk");
        user.put("last", "Bonkinson");
        user.put("born", 1919);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("//////////////////DocumentSnapshot added with ID: " + documentReference.getId() + " //////////////////");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("//////////////////Error adding to Firestore//////////////////");
                    }
                });
        */
    }

    public void onClickLoginButton(View view){
        //Check both boxes are filled
        if(!validateLoginInputs()){
            Toast.makeText(this, "Please Enter your Email and Password", Toast.LENGTH_LONG).show();
            return;
        }
        makeLoginAttempts();
        //loginUserAttempt();
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


    private void makeLoginAttempts(){
        clntLoginAttempt();
    }//delimWorks@hotmail.com

    private void clntLoginAttempt(){
        // Take the value of two edit texts in Strings
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = ("clnt" + emailTextView.getText().toString());
        String password = passwordTextView.getText().toString();

        System.out.println("try: "+email);

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(
                    @NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful()){
                    loginAttemptSuccess2("clnt");
                }else{
                    cookLoginAttempt();
                }
            }
        });
    }
    private void cookLoginAttempt(){
        // Take the value of two edit texts in Strings
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = ("cook" + emailTextView.getText().toString());
        String password = passwordTextView.getText().toString();

        System.out.println("try: "+email);

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(
                            @NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()){
                            loginAttemptSuccess2("cook");
                        }else{
                            admnLoginAttempt();
                        }
                    }
                });
    }
    private void admnLoginAttempt(){
        // Take the value of two edit texts in Strings
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = ("admn" + emailTextView.getText().toString());
        String password = passwordTextView.getText().toString();

        System.out.println("try: "+email);

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(
                            @NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()){
                            loginAttemptSuccess2("admn");
                        }else{
                            loginAttemptFailure();
                        }
                    }
                });
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

    private void loginAttemptSuccess2(String userPrefix){
        //Don't want to leave there info sitting around after logging in
        ClearLoginInfoBoxes();

        Intent intent = new Intent(getApplicationContext(), UserHomepageActivity.class);
        intent.putExtra("UserType", userPrefix);
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