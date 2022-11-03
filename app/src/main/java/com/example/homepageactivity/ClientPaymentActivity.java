package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ClientPaymentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_payment);

        //firebase integration
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickFinishClientRegButton(View view){
        String cardholderName = (((EditText) findViewById(R.id.cardholderName)).getText()).toString();
        String cardNumber = (((EditText) findViewById(R.id.cardNumber)).getText()).toString();
        String expiryMonth = (((EditText) findViewById(R.id.expiryMonth)).getText()).toString();
        String expiryYear = (((EditText) findViewById(R.id.expiryYear)).getText()).toString();
        String cvvString = (((EditText) findViewById(R.id.CVV)).getText()).toString();


        if (!validate(cardholderName, cardNumber, expiryMonth, expiryYear, cvvString)) return;

        getIntent().putExtra("cardholderName", cardholderName);
        getIntent().putExtra("cardNumber", cardNumber);
        getIntent().putExtra("expiryMonth", expiryMonth);
        getIntent().putExtra("expiryYear", expiryYear);
        getIntent().putExtra("cvvString", cvvString);

//        Bundle extras=getIntent().getExtras();
//        Intent intent = new Intent(this, UserHomepageActivity.class);
//        intent.putExtras(extras);

        createClientAccount();

        finish();
//        startActivity(intent);
        //Brent's code that he might still want 10/20/2022
            /*
            //these values have already been checked in validity to be valid ints - no possible errors converting
            int cardNum = Integer.parseInt(cardNumber);
            int expMonth = Integer.parseInt(cardNumber);
            int expYear = Integer.parseInt(cardNumber);
            int cvvNum = Integer.parseInt(cardNumber);

            Payment payment = new Payment(cardNum, cvvNum, expMonth, expYear, cardholderName);
            Client client = new Client(extras.getString("FirstName"),extras.getString("LastName")
                    extras.getString("Address"), extras.getString("Email"),
                    extras.getString("Password"), payment);
             */
    }

    private void createClientAccount(){
        Bundle extras=getIntent().getExtras();

        //Transfers the user's details to a Map to be later transferred into the Cloud Firestore
        // userDetails excludes the email and password since those are stored in Authentication
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("role", "Client");
        userDetails.put("firstName", extras.getString("FirstName"));
        userDetails.put("lastName", extras.getString("LastName"));
        userDetails.put("address", extras.getString("Address"));
        userDetails.put("cardholderName", extras.getString("cardholderName"));
        userDetails.put("cardNumber", extras.getString("cardNumber"));
        userDetails.put("expiryMonth", extras.getString("expiryMonth"));
        userDetails.put("expiryYear", extras.getString("expiryYear"));
        userDetails.put("cvvString", extras.getString("cvvString"));

        mAuth.createUserWithEmailAndPassword(extras.getString("Email"), extras.getString("Password"))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            //adds the user's details to the Cloud Firestore
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(uid).set(userDetails);
                            /* For some reason it doesn't like addOnSuccessListener(new OnSuccessListener<DocumentReference>(), which worked in the MainActivity test case
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            onAccountCreationSuccess();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            onDetailAdditionFailure();                                        }
                                    });*/
                            onAccountCreationSuccess();
                        }
                        else {
                            onAccountCreationFailure();
                        }
                    }
                });
    }

    private void onAccountCreationSuccess(){
        Toast.makeText(getApplicationContext(),
                        "Registration successful!",
                        Toast.LENGTH_LONG)
                .show();
        finish();
    }

    private void onAccountCreationFailure(){
        Toast.makeText(
                        getApplicationContext(),
                        "Problem Registering User \n Please try again later",
                        Toast.LENGTH_LONG)
                .show();
    }

    private boolean validate(String cardholderName, String cardNumber, String expiryMonth, String expiryYear, String cvvString) {
        Validator val = new Validator();
        int cvv;
        long cardNum;
        int month;
        int year;

        try{
            cvv = Integer.parseInt(cvvString);
        }catch(NumberFormatException e){
            Toast.makeText(this, "CVV Field Invalid (Not a Number)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{
            cardNum = Long.parseLong(cardNumber);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Credit Card Number Field Invalid (Not a Number)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{
            month = Integer.parseInt(expiryMonth);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Expiry Month Field Invalid (Not a Number)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{
            year = Integer.parseInt(expiryYear);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Expiry Year Field Invalid (Not a Number)", Toast.LENGTH_LONG).show();
            return false;
        }

        if (val.getIntLength(cvv) != 3) {
            Toast.makeText(this, "Invalid CVV", Toast.LENGTH_LONG).show();
            return false;
        }
        if (cardholderName.equals("")){
            Toast.makeText(this, "Cardholder Name Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        if (val.getLongLength(cardNum) != 16) {
            Toast.makeText(this, "Invalid Credit Card Number", Toast.LENGTH_LONG).show();
            return false;
        }
        if (month > 12){
            Toast.makeText(this, "Invalid Expiry Month", Toast.LENGTH_LONG).show();
            return false;
        }
        if (year < 1910 || 2030 < year){
            Toast.makeText(this, "Invalid Expiry Year", Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d("TAG", "success");
        return true;
    }
}

