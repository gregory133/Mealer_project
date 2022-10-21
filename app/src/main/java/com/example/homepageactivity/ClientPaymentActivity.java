package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
        String firstName = extras.getString("FirstName");
        String lastName = extras.getString("LastName");
        String email = extras.getString("Email");
        String address = extras.getString("Address");
        String password = extras.getString("Password");

        mAuth.createUserWithEmailAndPassword("clnt"+email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful()) {
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
                        "Problem Making Account"
                                + " Please try again later",
                        Toast.LENGTH_LONG)
                .show();
    }

    private boolean validate(String cardholderName, String cardNumber, String expiryMonth, String expiryYear, String cvvString) {
        //Double isNum;
        if (cvvString.equals("")) {
            Toast.makeText(this, "CVV Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{ //check if CVV is actually a number
            Double.parseDouble(cvvString);
        }catch(NumberFormatException e){
            Toast.makeText(this, "CVV Field Invalid (Not Number)", Toast.LENGTH_LONG).show();
            return false;
        }

        if (cardholderName.equals("")){
            Toast.makeText(this, "Cardholder Name Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }

        if (cardNumber.equals("")){
            Toast.makeText(this, "Cardholder Number Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{ //check if cardNumber is actually a number
            Double.parseDouble(cardNumber);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Cardholder Number Field Invalid (Not Number)", Toast.LENGTH_LONG).show();
            return false;
        }

        if (expiryMonth.equals("")){
            Toast.makeText(this, "Expiry Month Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{ //check if expiryMonth is actually a number
            Double.parseDouble(expiryMonth);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Expiry Month Field Invalid (Not Number)", Toast.LENGTH_LONG).show();
            return false;
        }

        if (expiryYear.equals("")){
            Toast.makeText(this, "Expiry Year Field Invalid (Empty)", Toast.LENGTH_LONG).show();
            return false;
        }
        try{ //check if cardYear is actually a number
            Double.parseDouble(expiryYear);
        }catch(NumberFormatException e){
            Toast.makeText(this, "Expiry Year Field Invalid (Not Number)", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}

