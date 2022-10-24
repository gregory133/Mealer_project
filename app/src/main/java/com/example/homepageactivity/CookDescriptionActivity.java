package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class CookDescriptionActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_description);

        //firebase integration
        mAuth = FirebaseAuth.getInstance();
    }


    public void onClickFinishClientRegButton(View view){
        String description = (((EditText) findViewById(R.id.description)).getText()).toString();
        if(!validate(description)) return;

        getIntent().putExtra("Description", description);

        createCookAccount();

        finish();
    }

    private void createCookAccount(){
        Bundle extras = getIntent().getExtras();

        //Transfers the user's details to a Map to be later transferred into the Cloud Firestore
        // userDetails excludes the email and password since those are stored in Authentication
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("role", "Cook");
        userDetails.put("firstName",extras.getString("FirstName"));
        userDetails.put("lastName", extras.getString("LastName"));
        userDetails.put("address", extras.getString("Address"));
        userDetails.put("description", extras.getString("Description"));

        mAuth.createUserWithEmailAndPassword(extras.getString("Email"), extras.getString("Password"))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            //adds the user's details to the Cloud Firestore
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(uid).set(userDetails);
                            /* For some reason it doesn't like addOnSuccessListener(new OnSuccessListener<DocumentReference>(), which worked in the MainActivity test case
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            onAccountCreationSuccess();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            onDetailAdditionFailure();                                        }
                                    });*/
                            onAccountCreationSuccess();
                        }
                        else {
                            onAccountCreationFailure();
                        }
                    }
                });
    }

    private void onAccountCreationSuccess(){
        Toast.makeText(getApplicationContext(),
                        "Registration successful!",
                        Toast.LENGTH_LONG)
                .show();
        finish();
    }

    private void onAccountCreationFailure(){
        Toast.makeText(
                        getApplicationContext(),
                        "Problem Registering User \n Please try again later",
                        Toast.LENGTH_LONG)
                .show();
    }

    private boolean validate(String description){
        if (description.equals("")){
            Toast.makeText(this, "Description Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}