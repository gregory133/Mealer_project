package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity {

    private TextView mealName;
    private TextView mealType;
    private TextView cuisineType;
    private TextView mealPrice;

    private TextView cardholderName;
    private TextView cardNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        mealName=findViewById(R.id.mealName);
        mealType=findViewById(R.id.mealType);
        cuisineType=findViewById(R.id.cuisineType);
        mealPrice=findViewById(R.id.mealPrice);
        cardholderName=findViewById(R.id.cardholderName);
        cardNumber=findViewById(R.id.cardNumber);

        loadText();
    }

    private void loadText(){
        mealName.setText(mealName.getText()+getIntent().getStringExtra("mealName"));
        mealType.setText(mealType.getText()+getIntent().getStringExtra("mealType"));
        cuisineType.setText(cuisineType.getText()+getIntent().getStringExtra("cuisineType"));
        mealPrice.setText(mealPrice.getText()+getIntent().getStringExtra("mealPrice"));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(userId).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                HashMap<String, String> dict1=(HashMap<String, String>)documentSnapshot.get("payment");
                HashMap<String, Number> dict2=(HashMap<String, Number>)documentSnapshot.get("payment");

                String cardholderNameString=dict1.get("cardHolderName");
                Number cardNumberString=dict2.get("cardNumber");

                cardholderName.setText(cardholderName.getText()+cardholderNameString);

                String cardNum=String.valueOf(cardNumberString);
                String lastDigits=cardNum.substring(cardNum.length() - 4);
                String muffledCardNumber="**** **** **** "+lastDigits;
                cardNumber.setText(cardNumber.getText()+muffledCardNumber);

            }
        });




    }


}