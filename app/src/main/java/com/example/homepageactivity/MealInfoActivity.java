package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Meal;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MealInfoActivity extends AppCompatActivity {
    private Meal meal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_info);
        getMealFromFirebase();
    }

    private void getMealFromFirebase(){
        Intent intent=getIntent();
        String mealUID=intent.getStringExtra("mealUID");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("meals").document(mealUID).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    meal = documentSnapshot.toObject(Meal.class);
                    setupUI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not load Meal Information", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void setupUI(){
        ((TextView) findViewById(R.id.mealName)).setText("Meal: "+meal.getMealName());
        ((TextView) findViewById(R.id.mealType)).setText("type: "+meal.getMealType());
        ((TextView) findViewById(R.id.cuisineType)).setText("cuisine: "+meal.getCuisineType());
        ((TextView) findViewById(R.id.ingredients)).setText("ingredients: "+meal.getIngredients());
        ((TextView) findViewById(R.id.allergens)).setText("allergens"+meal.getAllergens());
        ((TextView) findViewById(R.id.price)).setText("price"+meal.getPrice());
        ((TextView) findViewById(R.id.description)).setText("description"+meal.getDescription());
    }

    public void onClickCookProfile(View view){

        Intent intent=new Intent(getApplicationContext(), CookProfileActivity.class);
        intent.putExtra("cookUID", meal.getCookUID());
        startActivity(intent);
    }

    public void onClickPlaceOrderButton(View view){

        Intent intent=getIntent();
        String mealUID=intent.getStringExtra("mealUID");

        intent=new Intent(getApplicationContext(), PlaceOrderActivity.class);

        intent.putExtra("cookUID", meal.getCookUID());
        intent.putExtra("mealUID", mealUID);
        intent.putExtra("mealName", meal.getMealName());

        startActivity(intent);
    }
}