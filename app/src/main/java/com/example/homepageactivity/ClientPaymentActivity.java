package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ClientPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_payment);
    }

    public void onClickRegisterButton(View view){
        String cardholderName = (((EditText) findViewById(R.id.cardholderName)).getText()).toString();
        String cardNumber = (((EditText) findViewById(R.id.cardNumber)).getText()).toString();
        String expiryMonth = (((EditText) findViewById(R.id.expiryMonth)).getText()).toString();
        String expiryYear = (((EditText) findViewById(R.id.expiryYear)).getText()).toString();
        String cvvString = (((EditText) findViewById(R.id.CVV)).getText()).toString();


        if (validate(cardholderName, cardNumber, expiryMonth, expiryYear, cvvString)){

            Bundle extras=getIntent().getExtras();

            Intent intent=new Intent(this, UserHomepageActivity.class);

            //these values have already been checked in validity to be valid ints - no possible errors converting
            //int cardNum = Integer.parseInt(cardNumber);
            //int expMonth = Integer.parseInt(cardNumber);
            //int expYear = Integer.parseInt(cardNumber);
            //int cvvNum = Integer.parseInt(cardNumber);

//            Payment payment = new Payment(cardNum, cvvNum, expMonth, expYear, cardholderName);
//            Client client = new Client(extras.getString("FirstName"),extras.getString("LastName")
//                    extras.getString("Address"), extras.getString("Email"),
//                    extras.getString("Password"), payment);

            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Invalid/Insufficient Information Given", Toast.LENGTH_LONG).show();
        }
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

