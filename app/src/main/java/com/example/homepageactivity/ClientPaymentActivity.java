package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.*;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.CreditCardInformation;
import com.example.homepageactivity.domain.StyleApplyer;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ClientPaymentActivity extends AppCompatActivity {
    private ImageView background;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_payment);
        nextButton=findViewById(R.id.completeClientRegistration);
        background=findViewById(R.id.background);
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

        CreditCardInformation newCard = new CreditCardInformation(
                Long.parseLong(extras.getString("cardNumber")),
                Integer.parseInt(extras.getString("cvvString")),
                Integer.parseInt(extras.getString("expiryMonth")),
                Integer.parseInt(extras.getString("expiryYear")),
                extras.getString("cardholderName")
        );

        Client newClient = new Client(
                extras.getString("FirstName"),
                extras.getString("LastName"),
                extras.getString("Address"),
                extras.getString("Email"),
                newCard
        );

        firebaseAuth.createUserWithEmailAndPassword(extras.getString("Email"), extras.getString("Password"))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            //adds the user's details to the Cloud Firestore
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
                            firestoreDB.collection("users").document(uid).set(newClient);
                            Map<String, Object> temp = new HashMap<>(1);
                            temp.put("role","Client");
                            firestoreDB.collection("users").document(uid).set(temp, SetOptions.merge());
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
        if (!val.checkValidExpDate(year, month)){
            Toast.makeText(this, "Card is Expired", Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d("TAG", "success");
        return true;
    }
}

