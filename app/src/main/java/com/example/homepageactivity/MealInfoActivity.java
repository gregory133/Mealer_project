package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Meal;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        task.addOnSuccessListener(documentSnapshot -> {
            try{
                meal = documentSnapshot.toObject(Meal.class);
                setupUI();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not load Meal Information", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupUI(){
        String mealName ="Meal: "+meal.getMealName();
        String mealType = "type: "+meal.getMealType();
        String cuisineType = "cuisine: "+meal.getCuisineType();
        String ingredients = "ingredients: "+meal.getIngredients();
        String allergens = "allergens"+meal.getAllergens();
        String price = "price"+meal.getPrice();
        String description = "description"+meal.getDescription();

        ((TextView) findViewById(R.id.mealName)).setText(mealName);
        ((TextView) findViewById(R.id.mealType)).setText(mealType);
        ((TextView) findViewById(R.id.cuisineType)).setText(cuisineType);
        ((TextView) findViewById(R.id.ingredients)).setText(ingredients);
        ((TextView) findViewById(R.id.allergens)).setText(allergens);
        ((TextView) findViewById(R.id.price)).setText(price);
        ((TextView) findViewById(R.id.description)).setText(description);
    }

    public void onClickCookProfile(View view){

        Intent intent=new Intent(getApplicationContext(), CookProfileActivity.class);
        intent.putExtra("cookUID", meal.getCookUID());
        startActivity(intent);
    }

    public void onClickPlaceOrderButton(View view){

        Intent intent=getIntent();
        String mealUID=intent.getStringExtra("mealUID");

        intent = new Intent(this, PlaceOrderActivity.class);

        intent.putExtra("cookUID", meal.getCookUID());
        intent.putExtra("mealUID", mealUID);
        intent.putExtra("mealName", meal.getMealName());
        intent.putExtra("mealPrice", Double.toString(meal.getPrice()));

        startActivity(intent);
    }
}