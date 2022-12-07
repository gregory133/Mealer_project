package com.example.homepageactivity;

import static android.content.ContentValues.TAG;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.appcompat.app.AppCompatActivity;

import java.util.GregorianCalendar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.ComplaintMessage;
import com.example.homepageactivity.domain.MealOrder;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {
    private Client orderingClient;
    private DocumentSnapshot cookDoc;
    private GregorianCalendar pickupTime;
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


        String cookUID = getIntent().getStringExtra("cookUID");
        getFirebaseObjectByUID(cookUID);
    }

    public void onClickSetPickupTime(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        pickupTime = new GregorianCalendar(year, month, day);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        pickupTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
        pickupTime.set(Calendar.MINUTE,minute);
        ((Button)findViewById(R.id.pickupTimeButton)).setText(formatPickupTime(pickupTime));
    }

    public static String formatPickupTime(GregorianCalendar pickupTime) {
        String days[] = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String meridiemArr[] = {"AM", "PM"};

        String day = days[pickupTime.get(Calendar.DAY_OF_WEEK)-1];
        String month = months[pickupTime.get(Calendar.MONTH)];

        int hour = pickupTime.get(Calendar.HOUR);
        if (hour==0) hour = 12;

        int min = pickupTime.get(Calendar.MINUTE);
        String minStr = String.valueOf(min);
        if (min<10) minStr = "0"+minStr;

        //AM = ante meridiem, PM = post meridiem
        String meridiem = meridiemArr[pickupTime.get(Calendar.AM_PM)];

        return String.format("%s, %s %d, %d at %d:%s%S",day,month,pickupTime.get(Calendar.DAY_OF_MONTH),
                pickupTime.get(Calendar.YEAR),hour,minStr,meridiem);
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

        MealOrder mealOrder = new MealOrder(cookUID, cookEmail, clientUID, clientEmail, mealUID, mealName, pickupTime.getTime());

        firestoreDB.collection("orders").add(mealOrder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Order Confirmed", Toast.LENGTH_LONG).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Problem Sending Order Request", Toast.LENGTH_LONG).show());
    }
}