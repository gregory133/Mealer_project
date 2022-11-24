package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MealInfoActivity extends AppCompatActivity {

    private TextView mealNameText;
    private TextView mealTypeText;
    private TextView cuisineTypeText;
    private TextView ingredientsText;
    private TextView allergensText;
    private TextView priceText;
    private TextView descText;

    private Button orderButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_info);

        mealNameText=findViewById(R.id.mealName);
        mealTypeText=findViewById(R.id.mealType);
        cuisineTypeText=findViewById(R.id.cuisineType);
        ingredientsText=findViewById(R.id.ingredients);
        allergensText=findViewById(R.id.allergens);
        priceText=findViewById(R.id.price);
        descText=findViewById(R.id.description);

        orderButton=findViewById(R.id.orderButton);
        loadTextViews();

    }

    private void loadTextViews(){
        Intent intent=getIntent();

        String mealName=intent.getStringExtra("mealName");
        String mealType=intent.getStringExtra("mealType");
        String cuisineType=intent.getStringExtra("cuisineType");
        String ingredients=intent.getStringExtra("ingredients");
        String allergens=intent.getStringExtra("allergens");
        String price=intent.getStringExtra("price");
        String description=intent.getStringExtra("description");

//        Log.d("TAG", mealName);

        mealNameText.setText(mealNameText.getText()+mealName);
        mealTypeText.setText(mealTypeText.getText()+mealType);
        cuisineTypeText.setText(cuisineTypeText.getText()+cuisineType);
        ingredientsText.setText(ingredientsText.getText()+ingredients);
        allergensText.setText(allergensText.getText()+allergens);
        priceText.setText(priceText.getText()+price);
        descText.setText(descText.getText()+description);
    }

    public void onClickCookProfile(View view){

        Intent newIntent=new Intent(getApplicationContext(), CookProfileActivity.class);

        String firstName=getIntent().getStringExtra("cookFirstName");
        String lastName=getIntent().getStringExtra("cookLastName");
        String desc=getIntent().getStringExtra("cookDesc");


        newIntent.putExtra("firstName", firstName);
        newIntent.putExtra("lastName", lastName);
        newIntent.putExtra("desc", desc);

//        Log.d("TAG", firstName+lastName+desc);

        startActivity(newIntent);
    }

    public void onClickPlaceOrderButton(View view){

        Intent intent=new Intent(getApplicationContext(), PlaceOrderActivity.class);
        intent.putExtra("mealName", getIntent().getStringExtra("mealName"));
        intent.putExtra("mealType", getIntent().getStringExtra("mealType"));
        intent.putExtra("cuisineType", getIntent().getStringExtra("cuisineType"));
        intent.putExtra("mealPrice", getIntent().getStringExtra("price"));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Log.d("TAG", userId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(userId).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                HashMap<String, String> dict1=(HashMap<String, String>)documentSnapshot.get("payment");
                HashMap<String, Number> dict2=(HashMap<String, Number>)documentSnapshot.get("payment");

                String cardholderName=dict1.get("cardHolderName");
                Number cardNumber=dict2.get("cardNumber");

                intent.putExtra("cardholderName", cardholderName);
                intent.putExtra("cardNumber", String.valueOf(cardNumber));
//                Log.d("TAG", documentSnapshot.get("cardholderName").toString()+documentSnapshot.get("cardNumber").toString());
                startActivity(intent);
            }
        });


//


    }
}