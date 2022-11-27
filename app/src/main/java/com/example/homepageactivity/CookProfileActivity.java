package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.Meal;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CookProfileActivity extends AppCompatActivity {
    private Cook cook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_profile);

        getCookFromFirebase();
    }



    private void getCookFromFirebase(){
        Intent intent=getIntent();
        String cookUID = intent.getStringExtra("cookUID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(cookUID).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    cook = documentSnapshot.toObject(Cook.class);
                    setupUI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not load Meal Information", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void setupUI(){
        ((TextView)findViewById(R.id.firstName)).setText(cook.getFirstName());
        ((TextView)findViewById(R.id.lastName)).setText(cook.getLastName());
        ((TextView)findViewById(R.id.email)).setText(cook.getEmailAddress());
        ((TextView)findViewById(R.id.description)).setText(cook.getDesc());
    }
}