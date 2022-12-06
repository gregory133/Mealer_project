package com.example.homepageactivity;

import static android.content.ContentValues.TAG;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.MealOrder;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceOrderActivity extends AppCompatActivity {
    private Client orderingClient;
    private DocumentSnapshot cookDoc;
    private String pickupTime;
    private static final String TAG = "PlaceOrderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        loadText();
        setupOnClickConfirmOrderButton();
    }

    private void loadText(){
        String userId;
        try{
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Could now Retrieve User Data\nTry Again Layer", Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(userId).get();

        task.addOnSuccessListener(documentSnapshot -> {
            try{
                orderingClient = documentSnapshot.toObject(Client.class);
                setupPlaceOrderActivityUI();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Could not load User Information", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupPlaceOrderActivityUI(){
        String cardNum = String.valueOf(orderingClient.getPayment().getCardNumber());
        String lastDigits = cardNum.substring(cardNum.length() - 4);

        //Done in 2 steps as per Android Studio guidelines
        String mealName = "Meal: "+getIntent().getStringExtra("mealName");
        String mealPrice = "Price: $"+getIntent().getStringExtra("mealPrice");
        String cardholderName = "Card Holder: "+orderingClient.getPayment().getCardHolderName();
        String cardNumber = "Card Number:\n\t **** **** **** "+lastDigits;

        ((TextView) findViewById(R.id.mealName)).setText(mealName);
        ((TextView) findViewById(R.id.price)).setText(mealPrice);
        ((TextView)findViewById(R.id.cardholderName)).setText(cardholderName);
        ((TextView)findViewById(R.id.cardNumber)).setText(cardNumber);
    }

    private void setupOnClickConfirmOrderButton(){
        Button confirmOrderButton = (Button) findViewById(R.id.confirmOrderButton);
        confirmOrderButton.setOnClickListener(this::onClickConfirmOrderButton);
    }

    public void onClickConfirmOrderButton(View view){
        pickupTime = (((EditText) findViewById(R.id.pickupTimeEdit)).getText()).toString();
        if (!validatePlaceOrder(pickupTime)) return;

        String cookUID = getIntent().getStringExtra("cookUID");
        getFirebaseObjectByUID(cookUID);
    }

    private boolean validatePlaceOrder(String pickupTime) {
        Validator val = new Validator();

        if (!val.isAlphanumericPhrase(pickupTime)) {
            Toast.makeText(this, "Pickup Time Field Invalid", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void getFirebaseObjectByUID(String UID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(UID).get();

        task.addOnSuccessListener(documentSnapshot -> {
            try{
                this.cookDoc = documentSnapshot;
                placeOrder();
            }catch (Exception e){
                Log.e(TAG, "getFirebaseObjectByUID: "+e);
            }
        });
    }

    private void placeOrder(){
        String cookUID = cookDoc.getId();
        String cookEmail = cookDoc.getString("emailAddress");

        String clientUID;
        try{
            clientUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Could now Retrieve User Data\nTry Again Layer", Toast.LENGTH_LONG).show();
            return;
        }
        String clientEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = getIntent();
        String mealUID = intent.getStringExtra("mealUID");
        String mealName = intent.getStringExtra("mealName");

        MealOrder mealOrder = new MealOrder(cookUID, cookEmail, clientUID, clientEmail, mealUID, mealName, pickupTime);

        firestoreDB.collection("orders").add(mealOrder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Order Confirmed", Toast.LENGTH_LONG).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Problem Sending Order Request", Toast.LENGTH_LONG).show());
    }
}