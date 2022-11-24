package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.homepageactivity.domain.Meal;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

public class MealEditActivity extends AppCompatActivity {
    private final List<String> cuisineOptions = Arrays.asList("Cuisine", "American", "Mexican", "Chinese", "Other");      //Also hardcoded in AddMealActivity
    private final List<String> mealTypeOptions = Arrays.asList("Meal Type", "Appetizer", "Entree", "Dessert", "Other");      //Also hardcoded in AddMealActivity
    private String chosenCuisine;
    private String chosenMealType;
    private DocumentReference docRef;
    private DocumentSnapshot document;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);

        setupCuisineOrTypeSpinner((Spinner) findViewById(R.id.cuisineType), cuisineOptions);
        setupCuisineOrTypeSpinner((Spinner) findViewById(R.id.cuisineType), mealTypeOptions);
        getMealInfo();

    }

    public void setupCuisineOrTypeSpinner(Spinner spinner, final List<String> options){
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
                switch (options.get(0)){
                    case "Cuisine":
                        chosenCuisine = options.get(i);
                        break;
                    case "Meal Type":
                        chosenMealType = options.get(i);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void getMealInfo(){
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            throwToast("Could not load meal");
        }

        Bundle extras=getIntent().getExtras();

        docRef = firestoreDB.collection("meals").document(extras.getString("mealID"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        SetupMealInfo();
                    } else {
                        throwToast("Meal doesn't exist");
                        return;
                    }
                } else {
                    throwToast("Meal failed to load");
                    return;
                }
            }
        });
    }

    private void throwToast(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }

    private void SetupMealInfo(){
        ((EditText) findViewById(R.id.mealName)).setText(document.getString("mealName"));
        ((EditText) findViewById(R.id.description)).setText(document.getString("description"));
        ((EditText) findViewById(R.id.price)).setText(document.getDouble("price").toString());
        ((EditText) findViewById(R.id.listOfIngredients)).setText(document.getString("ingredients"));
        ((EditText) findViewById(R.id.allergens)).setText(document.getString("allergens"));
        ((CheckBox)findViewById(R.id.offerMealCheckbox)).setChecked(document.getBoolean("offered"));

        //Finds and display current cuisine type
        Spinner spinny = (Spinner) findViewById(R.id.cuisineType);
        ArrayAdapter myAdap = (ArrayAdapter) spinny.getAdapter();
        spinny.setSelection(myAdap.getPosition(document.getString("cuisineType")));

        //Finds and display current meal type
        spinny = (Spinner) findViewById(R.id.cuisineType);
        myAdap = (ArrayAdapter) spinny.getAdapter();
        spinny.setSelection(myAdap.getPosition(document.getString("mealType")));
    }

    public void onClickApplyChangesButton(View view) {

        //INSERT VALIDATION same as AddMealActivity
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String mealName = (((EditText) findViewById(R.id.mealName)).getText()).toString();
        String description = (((EditText) findViewById(R.id.description)).getText()).toString();
        String priceStr = ((EditText) findViewById(R.id.price)).getText().toString();
        String ingredients = (((EditText) findViewById(R.id.listOfIngredients)).getText()).toString();
        String allergens = (((EditText) findViewById(R.id.allergens)).getText()).toString();
        boolean offered = ((CheckBox)findViewById(R.id.offerMealCheckbox)).isChecked();

        if(!validateMeal(mealName, description, chosenCuisine, chosenMealType,
                ingredients, allergens, priceStr)){return;}

        double price = Double.parseDouble(priceStr);



        Meal updatedMeal = new Meal(mealName, description, chosenCuisine, chosenMealType,
                ingredients, allergens, price, currentUser.getUid(), offered);
        docRef.set(updatedMeal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        throwToast("Meal Updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        throwToast("Could not Update Meal");
                    }
                });
    }

    public void onClickDeleteMealButton(View view){
        if(((CheckBox)findViewById(R.id.offerMealCheckbox)).isChecked()){
            throwToast("Cannot Delete Offered Meal\n" +
                    "To Delete the Meal, You must First\n" +
                    "Remove it from the Offered Meals List");
            return;
        }
        docRef.delete();
        finish();
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
        if (!val.isPhrase(mealName)){
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