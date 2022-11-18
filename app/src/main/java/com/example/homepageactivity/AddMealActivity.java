package com.example.homepageactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class AddMealActivity extends AppCompatActivity {

    private FirebaseFirestore firestoreDB;
    private FirebaseUser currentUser;

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
    }

    public void onClickAddMealButton(View view) {
        String mealName = (((EditText) findViewById(R.id.mealNameEdit)).getText()).toString();
        String description = (((EditText) findViewById(R.id.descriptionEdit)).getText()).toString();
        double price = Double.parseDouble(((EditText) findViewById(R.id.priceEdit)).getText().toString());

        //INSERT VALIDATION

        Meal newMeal = new Meal(mealName, description, null, null,
                null, null, price, currentUser.getUid(), false);
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

    private void onMealCreationSuccess() {
        Toast.makeText(this, "Meal successfully added", Toast.LENGTH_LONG).show();
        finish();
    }

    private void onMealAdditionFailure() {
        Toast.makeText(this, "ERROR: Failed to add meal", Toast.LENGTH_LONG).show();
    }
}