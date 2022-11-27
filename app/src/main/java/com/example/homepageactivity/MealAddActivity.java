package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.Meal;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;
import java.util.List;

public class MealAddActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private String chosenCuisine;
    private String chosenMealType;
    private final List<String> cuisineOptions = Arrays.asList("Cuisine", "American", "Mexican", "Chinese", "Other");      //Also hardcoded in AddMealActivity
    private final List<String> mealTypeOptions = Arrays.asList("Meal Type", "Appetizer", "Entree", "Dessert", "Other");      //Also hardcoded in AddMealActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
            finish();
        }

        setupCuisineSpinner((Spinner) findViewById(R.id.cuisineType), cuisineOptions);
        setupMealTypeSpinner((Spinner) findViewById(R.id.mealType), mealTypeOptions);
    }

    public void onClickAddMealButton(View view) {
        String mealName = (((EditText) findViewById(R.id.mealName)).getText()).toString();
        String description = (((EditText) findViewById(R.id.description)).getText()).toString();
        String priceStr = ((EditText) findViewById(R.id.price)).getText().toString();
        String ingredients = (((EditText) findViewById(R.id.listOfIngredients)).getText()).toString();
        String allergens = (((EditText) findViewById(R.id.allergens)).getText()).toString();
        boolean offered = ((CheckBox)findViewById(R.id.offerMealCheckbox)).isChecked();

        if(!validateMeal(mealName, description, chosenCuisine, chosenMealType,
                ingredients, allergens, priceStr)){return;}

        double price = Double.parseDouble(priceStr);

        //INSERT VALIDATION same as EditMealActivity

        Meal newMeal = new Meal(mealName, description, chosenCuisine, chosenMealType,
                ingredients, allergens, price, currentUser.getUid(), offered);
        firestoreDB.collection("meals").add(newMeal)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        onMealCreationSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onMealAdditionFailure();                                        }
                });
    }

    public void setupCuisineSpinner(Spinner spinner, final List<String> options){
        chosenCuisine = "";
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_layout,
                options);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenCuisine = options.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public void setupMealTypeSpinner(Spinner spinner, final List<String> options){
        chosenMealType = "";
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_layout,
                options);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chosenMealType = options.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void onMealCreationSuccess() {
        Toast.makeText(this, "Meal successfully added", Toast.LENGTH_LONG).show();
        finish();
    }

    private void onMealAdditionFailure() {
        Toast.makeText(this, "ERROR: Failed to add meal", Toast.LENGTH_LONG).show();
    }


    private boolean validateMeal(String mealName, String description, String chosenCuisine, String chosenMealType,
                             String ingredients, String allergens, String price) {
        Validator val = new Validator();
        double mealPrice;

        try{
            mealPrice = Double.parseDouble(price);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Meal Price Invalid (Not a Number)", Toast.LENGTH_LONG).show();
            return false;
        }

        if (mealName.equals("")){
            Toast.makeText(this, "Your Meal Must Have A Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!val.isAlphanumericPhrase(mealName)){
            Toast.makeText(this, "Meal Names May Only Contain Alphanumeric Characters, Apostrophes, and Spaces", Toast.LENGTH_LONG).show();
            return false;
        }
        if (val.getStringLength(description) > 300) {
            Toast.makeText(this, "Your Meal Description is "+(val.getStringLength(description)-300)+" Characters Too Long", Toast.LENGTH_LONG).show();
            return false;
        }
        if(val.getStringIndex(cuisineOptions, chosenCuisine) < 1){
            Toast.makeText(this, "Please Select a Cuisine For This Meal", Toast.LENGTH_LONG).show();
            return false;
        }
        if(val.getStringIndex(mealTypeOptions, chosenMealType) < 1){
            Toast.makeText(this, "Please Select a Meal Type For This Meal", Toast.LENGTH_LONG).show();
            return false;
        }
        if (ingredients.equals("")){
            Toast.makeText(this, "Please Enter Your Meal's Ingredients", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!val.isAlphanumericPhrase(ingredients)){
            Toast.makeText(this, "Ingredients May Only Contain Alphanumeric Characters", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!val.isAlphanumericPhrase(allergens)){
            Toast.makeText(this, "Allergens May Only Contain Alphanumeric Characters", Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d("TAG", "success");
        return true;
    }

}