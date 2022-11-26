package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        loadText();
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
                    //setupUI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not load User Information", Toast.LENGTH_LONG);
                }
            }
        });
    }

    public void setupPlaceOrderActivityUI(){
        TextView mealName=findViewById(R.id.mealName);
        TextView  mealType=findViewById(R.id.mealType);
        TextView  cuisineType=findViewById(R.id.cuisineType);
        TextView mealPrice=findViewById(R.id.mealPrice);
        TextView cardholderName=findViewById(R.id.cardholderName);
        TextView cardNumber=findViewById(R.id.cardNumber);

        String cardholderNameString = orderingClient.getPayment().getCardHolderName();
        Long cardNumberString = orderingClient.getPayment().getCardNumber();

        cardholderName.setText(cardholderName.getText()+cardholderNameString);

        String cardNum = String.valueOf(cardNumberString);
        String lastDigits = cardNum.substring(cardNum.length() - 4);
        String muffledCardNumber = "**** **** **** "+lastDigits;
        cardNumber.setText(cardNumber.getText()+muffledCardNumber);

        mealName.setText(mealName.getText()+getIntent().getStringExtra("mealName"));
        mealType.setText(mealType.getText()+getIntent().getStringExtra("mealType"));
        cuisineType.setText(cuisineType.getText()+getIntent().getStringExtra("cuisineType"));
        mealPrice.setText(mealPrice.getText()+getIntent().getStringExtra("mealPrice"));
    }

    public void onClickPlaceOrderButton(View view){
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
        Cook cook;
        try {
            cook = cookDoc.toObject(Cook.class);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Failed to Place Order", Toast.LENGTH_LONG);
            return;
        }
        String cookUID = cookDoc.getId();
        String cookEmail = cook.getEmailAddress();
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
                        //onMessageCreationSuccess();
                        finish();
                        Toast.makeText(getApplicationContext(), "Order Request Sent", Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Problem Sending Order Request", Toast.LENGTH_LONG);
                    }
                });
    }
}