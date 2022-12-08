package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.Meal;
import com.example.homepageactivity.domain.MealOrder;
import com.example.homepageactivity.domain.Message;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MealOrderInfoActivity extends AppCompatActivity {
    int lastRating;
    int rating;
    int ratingDif;
    int recieved;
    int[] stars;
    int spinnerNum;
    int spinnerIndex;
    boolean firstRating;
    DocumentReference docRef;
    DocumentSnapshot orderDoc;
    private final List<String> approvedOptions = Arrays.asList("Pending Approval", "Request Approved", "Request Declined");
    private final List<String> deliveredOptions = Arrays.asList("Pickup Status", "Available for Pickup", "Order Canceled");
    private final List<String> receivedOptions = Arrays.asList("Received Status", "Order Received", "Order Lost");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_order_info);

        spinnerNum = 0;
        spinnerIndex = 0;

        setThemeColors();
        getOrderInfo();
        collapseComplaintButton();
    }

    private void getOrderInfo(){

        Bundle extras=getIntent().getExtras();

        docRef = firestoreDB.collection("orders").document(extras.getString("orderID"));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                orderDoc = task.getResult();
                if (orderDoc.exists()) {
                    SetupOrderInfo();
                    return;
                }
            }
            throwToast("Meal Order Failed To Load");
        });
    }

    private void SetupOrderInfo(){
        MealOrder currentOrder = orderDoc.toObject(MealOrder.class);
        rating = lastRating = currentOrder.getRating();
        recieved = currentOrder.getReceived();

        Date pickupDate = currentOrder.getPickupTime();
        String pickupText = "ERROR";
        if (pickupDate != null) {
            GregorianCalendar pickupTime = new GregorianCalendar();
            pickupTime.setTime(pickupDate);
            pickupText = PlaceOrderActivity.formatPickupTime(pickupTime);
        }

        ((TextView) findViewById(R.id.orderMealName)).setText(orderDoc.getString("mealName"));
        ((TextView) findViewById(R.id.pickupTime)).setText(pickupText);

        Class userClass = currentAccount.getClass();
        if (userClass == Cook.class){
            ((TextView) findViewById(R.id.otherParty)).setText(orderDoc.getString("clientEmail"));
        } else {
            ((TextView) findViewById(R.id.otherParty)).setText(orderDoc.getString("cookEmail"));
        }

        collapseRating();

        //setup spinners
        setupSpinner(findViewById(R.id.approvedSpinner), approvedOptions, "approved");
        setupSpinner(findViewById(R.id.deliveredSpinner), deliveredOptions, "delivered");
        setupSpinner(findViewById(R.id.receivedSpinner), receivedOptions, "received");
        setupStars();

        //initialize spinner values
        Spinner spinner = null;
        if(orderDoc.getDouble("delivered").intValue() == 1  && orderDoc.getDouble("received").intValue() == 0 && userClass == Client.class){        //only Clients can update received status and only if is has been delivered
            spinner = findViewById(R.id.receivedSpinner);
            spinnerNum = 3;
        }else if(orderDoc.getDouble("approved").intValue() == 1 && orderDoc.getDouble("delivered").intValue() == 0 && userClass == Cook.class){       //only cooks can update delivered status and only if it is approved
            spinner = findViewById(R.id.deliveredSpinner);
            spinnerNum = 2;
        }else if(orderDoc.getDouble("approved").intValue() == 0 && userClass == Cook.class){       //only cooks can update approved status and only if a status is not set
            spinner= findViewById(R.id.approvedSpinner);
            spinnerNum = 1;
        }

        if(spinner != null){
            spinner.setEnabled(true);
            spinner.setBackground(getDrawable(R.drawable.outline_off_white));
            spinner.getBackground().setColorFilter(getResources().getColor(R.color.off_white), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void setThemeColors(){
        Class hold = currentAccount.getClass();
        if (hold == Cook.class){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        } else {
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }

    private void setupSpinner(Spinner spinner, final List<String> options, String field){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_layout,
                options);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(adapter);
        spinner.setSelection(orderDoc.getDouble(field).intValue());
        if(orderDoc.getDouble(field).intValue() == 2){
            spinner.setBackgroundColor(getResources().getColor(R.color.cook_dark));
            spinner.getBackground().setColorFilter(getResources().getColor(R.color.cook_dark), PorterDuff.Mode.SRC_ATOP);
        }
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.off_grey), PorterDuff.Mode.SRC_ATOP);
        spinner.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerIndex = i;
                if(spinnerNum == 1 && spinnerIndex == 1){
                    updateCookSpinners();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void updateCookSpinners(){
        Spinner spinner = findViewById(R.id.approvedSpinner);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.off_grey), PorterDuff.Mode.SRC_ATOP);
        spinner.setEnabled(false);

        spinner = findViewById(R.id.deliveredSpinner);
        spinnerNum = 2;
        spinner.setEnabled(true);
        spinner.setBackground(getDrawable(R.drawable.outline_off_white));
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.off_white), PorterDuff.Mode.SRC_ATOP);
    }

    private void setupStars(){
        stars = new int[]{
                R.id.star1Icon,
                R.id.star2Icon,
                R.id.star3Icon,
                R.id.star4Icon,
                R.id.star5Icon};
        setRating();

        for(int i=0;i< stars.length;i++){
            int finalI = i;
            findViewById(stars[i]).setOnClickListener(
                    b -> {
                        onClickStar(finalI +1);
                    });
        }
    }

    public void onClickStar(int newRating){
        Class hold = currentAccount.getClass();
        if (hold != Client.class){
            return;
        }
        if (recieved != 1 ){
            throwToast("Order must be Received before you can rate it");
            return;
        }
        rating = newRating;
        setRating();
    }

    private void setRating(){
        for(int i=1;i< stars.length+1;i++){
            if(i<=rating){
                ((ImageView)findViewById(stars[i-1])).setColorFilter(getResources().getColor(R.color.client_light));
            }else{
                ((ImageView)findViewById(stars[i-1])).setColorFilter(getResources().getColor(R.color.off_grey));
            }
        }
    }

    public void onClickWriteComplaintButton(View view){
        Class hold = currentAccount.getClass();
        MealOrder order = orderDoc.toObject(MealOrder.class);
        if (hold != Client.class){
            return;
        }

        Intent intent = new Intent(getApplicationContext(), InboxWriteComplaintActivity.class);
        intent.putExtra("targetEmail", order.getCookEmail());
        intent.putExtra("targetUID", order.getCookUID());
        startActivity(intent);
    }

    private void collapseComplaintButton(){
        if (currentAccount.getClass() != Client.class){
            LinearLayout adminRow=findViewById(R.id.complaintLayout);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0);
            adminRow.setLayoutParams(param);
        }
    }

    private void collapseRating(){
        if (recieved != 1){
            LinearLayout row=findViewById(R.id.ratingLayout);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0);
            row.setLayoutParams(param);
        } else{
            expandRating(1);
        }
    }
    private void expandRating(int recieved){
        if(recieved != 1){
            collapseRating();
            return;
        }

        LinearLayout row=findViewById(R.id.ratingLayout);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        row.setLayoutParams(param);
    }



    public void onClickApplyChangesButton(View view){
        MealOrder originalOrder = orderDoc.toObject(MealOrder.class);
        MealOrder updatedOrder = orderDoc.toObject(MealOrder.class);


        recieved = ((Spinner) findViewById(R.id.receivedSpinner)).getSelectedItemPosition();
        updatedOrder.setApproved(((Spinner) findViewById(R.id.approvedSpinner)).getSelectedItemPosition());
        updatedOrder.setDelivered(((Spinner) findViewById(R.id.deliveredSpinner)).getSelectedItemPosition());
        updatedOrder.setReceived(recieved);
        updatedOrder.setRating(rating);

        docRef.set(updatedOrder)
                .addOnSuccessListener(aVoid -> expandRating(recieved))
                .addOnFailureListener(e -> throwToast("Could not Update Order"));

        throwToast("Changes Saved");

        if(originalOrder.getApproved() != updatedOrder.getApproved()){
            sendStatusUpdateMessage(updatedOrder.getClientUID(), 1, updatedOrder.getApproved());       //only the cook can change approved status, so this is fine
        }
        if (originalOrder.getReceived() != 1 && updatedOrder.getReceived() == 1) {
            String cookUID = orderDoc.getString("cookUID");
            DocumentReference cookRef = firestoreDB.collection("users").document(cookUID);
            cookRef.update("mealsSold", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> Log.d("updating mealsSold", "SUCCESS"))
                    .addOnFailureListener(e -> Log.d("updating mealsSold", "FAILURE"));
        }

        //rate meal and cook if the meal was recieved by the client
        Class hold = currentAccount.getClass();
        if(recieved != 1 || hold != Client.class){
            return;
        }
        ratingDif = rating-lastRating;
        firstRating = (lastRating == 0);
        lastRating = rating;
        if (originalOrder.getReceived() == 1) {
            updateRating("meals", orderDoc.getString("mealUID"));
            updateRating("users", orderDoc.getString("cookUID"));
        }

        getOrderInfo();
    }

    private void updateRating(String collection, String UID){
        DocumentReference ratingDocRef = firestoreDB.collection(collection).document(UID);
        ratingDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot rateableDoc = task.getResult();
                if (rateableDoc.exists()) {
                    rate(collection, ratingDocRef, rateableDoc);
                    return;
                }
            }
            throwToast("Meal Order Failed To Load");
        });
    }

    private void rate(String collection, DocumentReference ratingDocRef, DocumentSnapshot rateableDoc){
        switch (collection){
            case "users":
                Cook cook = rateableDoc.toObject(Cook.class);
                if(firstRating){
                    cook.setNumRatings(cook.getNumRatings()+1);
                }
                cook.setRatingTotal(cook.getRatingTotal()+ratingDif);
                cook.setRole("Cook");
                ratingDocRef.set(cook)
                        .addOnFailureListener(e -> throwToast("Could not Update Order"));
                break;
            case "meals":
                Meal meal = rateableDoc.toObject(Meal.class);
                if(firstRating){
                    meal.setNumRatings(meal.getNumRatings()+1);
                }
                meal.setRatingTotal(meal.getRatingTotal()+ratingDif);
                ratingDocRef.set(meal)
                        .addOnFailureListener(e -> throwToast("Could not Update Order"));
                break;
            default:
                return;
        }
    }

    private void sendStatusUpdateMessage(String recipientUID, int statusType, int newValue){
        String[] statusList;
        MealOrder order = orderDoc.toObject(MealOrder.class);
        int oldValue;
        switch (statusType){
            case 1:
                statusList = (String[])approvedOptions.toArray();
                oldValue = order.getApproved();
                break;
            case 2:
                statusList = (String[])deliveredOptions.toArray();
                oldValue = order.getDelivered();
                break;
            case 3:
                statusList = (String[])receivedOptions.toArray();
                oldValue = order.getReceived();
                break;
            default:
                return;
        }


        String subject = "Your order status has been updated.";
        String bodyText = "Your order of "+order.getMealName()+" has had its status updated from \""+statusList[oldValue]+"\" to \""+statusList[newValue]+"\".";
        String senderUID = firebaseAuth.getCurrentUser().getUid();
        String senderEmail = firebaseAuth.getCurrentUser().getEmail();

        Message newMessage = new Message(senderUID, senderEmail, recipientUID, subject, bodyText);

        firestoreDB.collection("messages").add(newMessage)
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Problem sending automatic status update notification\nPlease send your customer a message to let them know their order has been approved.", Toast.LENGTH_LONG));
    }

    public void onCLickReturn(View view){
        finish();
    }

    private void throwToast(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_SHORT).show();
    }
}