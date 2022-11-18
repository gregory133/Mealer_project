package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.homepageactivity.domain.Meal;
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
    private final List<String> cuisineOptions = Arrays.asList("Mexican", "Chinese", "Other");      //Also hardcoded in AddMealActivity
    private final List<String> mealTypeOptions = Arrays.asList("Appetizer", "Entree", "Dessert", "Other");      //Also hardcoded in AddMealActivity
    private String chosenCuisine;
    private String chosenMealType;
    private DocumentReference docRef;
    private DocumentSnapshot document;

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

        //Finds and display current cuisine type
        Spinner spinny = (Spinner) findViewById(R.id.cuisineTypeSpinner);
        ArrayAdapter myAdap = (ArrayAdapter) spinny.getAdapter();
        spinny.setSelection(myAdap.getPosition(document.getString("cuisineType")));

        //Finds and display current meal type
        spinny = (Spinner) findViewById(R.id.mealTypeSpinner);
        myAdap = (ArrayAdapter) spinny.getAdapter();
        spinny.setSelection(myAdap.getPosition(document.getString("mealType")));
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
        double price = Double.parseDouble(((EditText) findViewById(R.id.priceEdit)).getText().toString());
        String ingredients = (((EditText) findViewById(R.id.listOfIngredientsEdit)).getText()).toString();
        String allergens = (((EditText) findViewById(R.id.allergensEdit)).getText()).toString();
        boolean offered = ((CheckBox)findViewById(R.id.offerMealCheckbox)).isChecked();

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
}