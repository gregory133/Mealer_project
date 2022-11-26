package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.MealOrder;
import com.example.homepageactivity.domain.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity {
    private Client orderingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        Toast.makeText(getApplicationContext(), "not workingA", Toast.LENGTH_LONG);
        loadText();
        Toast.makeText(getApplicationContext(), "not working", Toast.LENGTH_LONG);
        setupOnClickConfirmOrderButton();
    }

    private void loadText(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(userId).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    orderingClient = documentSnapshot.toObject(Client.class);
                    setupPlaceOrderActivityUI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not load User Information", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void setupPlaceOrderActivityUI(){
        String cardNum = String.valueOf(orderingClient.getPayment().getCardNumber());
        String lastDigits = cardNum.substring(cardNum.length() - 4);

        ((TextView) findViewById(R.id.mealName)).setText("Meal: "+getIntent().getStringExtra("mealName"));
        ((TextView) findViewById(R.id.price)).setText("Price: $"+getIntent().getStringExtra("mealPrice"));
        ((TextView)findViewById(R.id.cardholderName)).setText("Card Holder: "+orderingClient.getPayment().getCardHolderName());
        ((TextView)findViewById(R.id.cardNumber)).setText("Card Number:\n\t **** **** **** "+lastDigits);
    }

    private void setupOnClickConfirmOrderButton(){
        Toast.makeText(getApplicationContext(), "setupOnClickConfirmOrderButton", Toast.LENGTH_LONG);
        Button confirmOrderButton = (Button) findViewById(R.id.confirmOrderButton);

        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickConfirmOrderButton(v);
            }
        });
    }

    public void onClickConfirmOrderButton(View view){
        Toast.makeText(getApplicationContext(), "Place Order", Toast.LENGTH_LONG);
        String cookUID = getIntent().getStringExtra("cookUID");
        getFirebaseObjectByUID("users", cookUID);
    }

    private void getFirebaseObjectByUID(String collection, String UID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection(collection).document(UID).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    placeOrder(documentSnapshot);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not load Information", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void placeOrder(DocumentSnapshot cookDoc){
        String cookUID = cookDoc.getId();
        String cookEmail = cookDoc.getString("");
        String clientUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String clientEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Intent intent = getIntent();
        String mealUID = intent.getStringExtra("mealUID");
        String mealName = intent.getStringExtra("mealName");

        MealOrder mealOrder = new MealOrder(cookUID, cookEmail, clientUID, clientEmail, mealUID, mealName);

        firestoreDB.collection("orders").add(mealOrder)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Order Request Sent", Toast.LENGTH_SHORT);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Problem Sending Order Request", Toast.LENGTH_LONG);
                    }
                });
    }
}