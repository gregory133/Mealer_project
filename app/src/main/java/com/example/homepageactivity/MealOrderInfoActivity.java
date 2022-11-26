package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.R;
import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.Meal;
import com.example.homepageactivity.domain.MealOrder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MealOrderInfoActivity extends AppCompatActivity {
    int initialRating;
    int rating;
    int[] stars;
    int spinnerNum;
    int spinnerIndex;
    DocumentReference docRef;
    DocumentSnapshot orderDoc;
    private final List<String> approvedOptions = Arrays.asList("Pending Approval", "Request Approved", "Request Declined");
    private final List<String> deliveredOptions = Arrays.asList("Delivery Status", "Order Delivered", "Order Canceled");
    private final List<String> receivedOptions = Arrays.asList("Received Status", "Order Received", "Order Lost");

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_order_info);

        setThemeColors();
        getOrderInfo();
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
        initialRating = orderDoc.getDouble("rating").intValue();

        ((TextView) findViewById(R.id.orderMealName)).setText(orderDoc.getString("mealName"));

        Class userClass = currentAccount.getClass();
        if (userClass == Cook.class){
            ((TextView) findViewById(R.id.otherParty)).setText(orderDoc.getString("clientEmail"));
        } else {
            ((TextView) findViewById(R.id.otherParty)).setText(orderDoc.getString("cookEmail"));
        }

        //setup spinners
        setupSpinner(findViewById(R.id.approvedSpinner), approvedOptions, "approved");
        setupSpinner(findViewById(R.id.deliveredSpinner), deliveredOptions, "delivered");
        setupSpinner(findViewById(R.id.receivedSpinner), receivedOptions, "received");
        setupStars();

        //initialize spinner values
        spinnerNum = 0;
        spinnerIndex = 0;
        if(orderDoc.getDouble("delivered").intValue() == 1 && userClass == Client.class){        //only Clients can update received status and only if is has been delivered
            findViewById(R.id.receivedSpinner).setEnabled(true);
            spinnerNum = 3;
        }else if(orderDoc.getDouble("approved").intValue() == 1 && orderDoc.getDouble("delivered").intValue() == 0 && userClass == Cook.class){       //only cooks can update delivered status and only if it is approved
            findViewById(R.id.deliveredSpinner).setEnabled(true);
            spinnerNum = 2;
        }else if(orderDoc.getDouble("approved").intValue() == 0 && userClass == Cook.class){       //only cooks can update approved status and only if a status is not set
            findViewById(R.id.approvedSpinner).setEnabled(true);
            spinnerNum = 1;
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
        spinner.setEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerIndex = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setupStars(){
        rating = 0;
        stars = new int[]{
                R.id.star1Icon,
                R.id.star2Icon,
                R.id.star3Icon,
                R.id.star4Icon,
                R.id.star5Icon};
        setRating();
    }
    public void onClickStar(View view){
        Class hold = currentAccount.getClass();
        if (hold != Cook.class || orderDoc.getDouble("received").intValue() != 1 ){
            return;
        }
        
        rating = (Integer) view.getTag();
        setRating();

        //rate meal and cook
        updateRating("meals", orderDoc.getString("mealUID"));
        updateRating("users", orderDoc.getString("cookUID"));
    }
    private void setRating(){
        for(int i=1;i< stars.length+1;i++){
            if(i<=rating){
                ((ImageView)findViewById(stars[i-1])).setColorFilter(getResources().getColor(R.color.client_light));
            }else{
                ((ImageView)findViewById(stars[i-1])).setColorFilter(getResources().getColor(R.color.client_dark));
            }
        }
    }

    public void onClickWriteComplaintButton(View view){
        //ret
    }

    public void onClickApplyChangesButton(View view){
        MealOrder updatedOrder = orderDoc.toObject(MealOrder.class);


        int recieved = ((Spinner) findViewById(R.id.deliveredSpinner)).getSelectedItemPosition();
        updatedOrder.setApproved(((Spinner) findViewById(R.id.approvedSpinner)).getSelectedItemPosition());
        updatedOrder.setDelivered(recieved);
        updatedOrder.setReceived(((Spinner) findViewById(R.id.receivedSpinner)).getSelectedItemPosition());

        docRef.set(updatedOrder)
                .addOnSuccessListener(aVoid -> throwToast("Order Updated"))
                .addOnFailureListener(e -> throwToast("Could not Update Order"));

        Class hold = currentAccount.getClass();
        if(recieved != 1 || hold != Client.class){
            return;
        }
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
                if(initialRating == 0){
                    cook.setNumRatings(cook.getNumRatings()+1);
                }
                cook.setRatingTotal(cook.getRatingTotal()+rating-initialRating);
                docRef.set(cook)
                        .addOnFailureListener(e -> throwToast("Could not Update Order"));
                break;
            case "meals":
                Meal meal = rateableDoc.toObject(Meal.class);
                if(initialRating == 0){
                    meal.setNumRatings(meal.getNumRatings()+1);
                }
                meal.setRatingTotal(meal.getRatingTotal()+rating-initialRating);
                docRef.set(meal)
                        .addOnFailureListener(e -> throwToast("Could not Update Order"));
                break;
            default:
                return;
        }

    }

    public void onCLickReturn(View view){
        finish();
    }

    private void throwToast(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_SHORT).show();
    }
}