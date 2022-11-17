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

public class MainActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.emailTextEdit);
        passwordTextView = findViewById(R.id.editTextPassword);

        // Firestore Database testing
        /*
        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Bonk");
        user.put("last", "Bonkinson");
        user.put("born", 1919);

        // Add a new document with a generated ID
        firestoreDB.collection("users")
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
        mAuth = FirebaseAuth.getInstance();
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
        finish();
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