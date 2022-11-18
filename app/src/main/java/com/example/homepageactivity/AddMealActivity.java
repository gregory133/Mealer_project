package com.example.homepageactivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AddMealActivity extends AppCompatActivity {

    private FirebaseFirestore firestoreDB;
    private FirebaseUser currentUser;
    final List<String> cuisineOptions = Arrays.asList("Chinese", "Other");      //Also hardcoded in EditMealActivity
    final List<String> mealTypeOptions = Arrays.asList("Appetizer", "Entree", "Dessert", "Other");      //Also hardcoded in EditMealActivity
    String chosenCuisine;
    String chosenMealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            firestoreDB = FirebaseFirestore.getInstance();
        } else {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
            finish();
        }

        setupCuisineSpinner((Spinner) findViewById(R.id.cuisineTypeSpinner), cuisineOptions);
        setupMealTypeSpinner((Spinner) findViewById(R.id.mealTypeSpinner), mealTypeOptions);
    }

    public void onClickAddMealButton(View view) {
        String mealName = (((EditText) findViewById(R.id.mealNameEdit)).getText()).toString();
        String description = (((EditText) findViewById(R.id.descriptionEdit)).getText()).toString();
        double price = Double.parseDouble(((EditText) findViewById(R.id.priceEdit)).getText().toString());
        String ingredients = (((EditText) findViewById(R.id.listOfIngredientsEdit)).getText()).toString();
        String allergens = (((EditText) findViewById(R.id.allergensEdit)).getText()).toString();
        boolean offered = ((CheckBox)findViewById(R.id.offerMealCheckbox)).isChecked();

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
}