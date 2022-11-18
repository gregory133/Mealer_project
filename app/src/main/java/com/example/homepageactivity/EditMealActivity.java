package com.example.homepageactivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class EditMealActivity extends AppCompatActivity {
    final List<String> cuisineOptions = Arrays.asList("Chinese", "Other");      //Also hardcoded in AddMealActivity
    final List<String> mealTypeOptions = Arrays.asList("Appetizer", "Entree", "Dessert", "Other");      //Also hardcoded in AddMealActivity
    String chosenCuisine;
    String chosenMealType;
    DocumentReference docRef;
    DocumentSnapshot document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);

        setupCuisineSpinner((Spinner) findViewById(R.id.cuisineTypeSpinner), cuisineOptions);
        setupMealTypeSpinner((Spinner) findViewById(R.id.mealTypeSpinner), mealTypeOptions);
        getMealInfo();

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

    private void getMealInfo(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            throwToast("Could not load meal");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle extras=getIntent().getExtras();

        docRef = db.collection("meals").document(extras.getString("mealID"));
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
        ((EditText) findViewById(R.id.mealNameEdit)).setText(document.getString("mealName"));
        ((EditText) findViewById(R.id.descriptionEdit)).setText(document.getString("description"));
        ((EditText) findViewById(R.id.priceEdit)).setText(document.getDouble("price").toString());
        ((EditText) findViewById(R.id.listOfIngredientsEdit)).setText(document.getString("ingredients"));
        ((EditText) findViewById(R.id.allergensEdit)).setText(document.getString("allergens"));
        ((CheckBox)findViewById(R.id.offerMealCheckbox)).setChecked(document.getBoolean("offered"));
        ((Spinner) findViewById(R.id.cuisineTypeSpinner)).setPrompt(document.getString("cuisineType"));
        ((Spinner) findViewById(R.id.mealTypeSpinner)).setPrompt(document.getString("mealType"));
    }

    public void onClickApplyChangesButton(View view) {

        //INSERT VALIDATION same as AddMealActivity
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String mealName = (((EditText) findViewById(R.id.mealNameEdit)).getText()).toString();
        String description = (((EditText) findViewById(R.id.descriptionEdit)).getText()).toString();
        String priceStr = ((EditText) findViewById(R.id.priceEdit)).getText().toString();
        String ingredients = (((EditText) findViewById(R.id.listOfIngredientsEdit)).getText()).toString();
        String allergens = (((EditText) findViewById(R.id.allergensEdit)).getText()).toString();
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
        if(val.getStringIndex(cuisineOptions, chosenCuisine) < 0){
            Toast.makeText(this, "Please Select a Cuisine For This Meal", Toast.LENGTH_LONG).show();
            return false;
        }
        if(val.getStringIndex(mealTypeOptions, chosenMealType) < 0){
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