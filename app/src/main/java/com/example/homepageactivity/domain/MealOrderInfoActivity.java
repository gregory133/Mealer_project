package com.example.homepageactivity.domain;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

public class MealOrderInfoActivity extends AppCompatActivity {
    int rating;
    int[] stars;
    int spinnerNum;
    int spinnerIndex;
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
        setupSpinner((Spinner) findViewById(R.id.approvedSpinner), approvedOptions, "approved");
        setupSpinner((Spinner) findViewById(R.id.deliveredSpinner), deliveredOptions, "delivered");
        setupSpinner((Spinner) findViewById(R.id.receivedSpinner), receivedOptions, "received");
        setupStars();
        getOrderInfo();
    }

    private void getOrderInfo(){

        Bundle extras=getIntent().getExtras();

        DocumentReference docRef = firestoreDB.collection("orders").document(extras.getString("orderID"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    orderDoc = task.getResult();
                    if (orderDoc.exists()) {
                        SetupOrderInfo();
                    } else {
                        throwToast("Meal doesn't exist");
                        return;
                    }
                } else {
                    throwToast("Meal failed to load");
                    return;
                }
            }
        });
    }

    private void SetupOrderInfo(){
        ((TextView) findViewById(R.id.orderMealName)).setText(orderDoc.getString("mealName"));

        Class userClass = currentAccount.getClass();
        if (userClass == Cook.class){
            ((TextView) findViewById(R.id.otherParty)).setText(orderDoc.getString("clientEmail"));
        } else {
            ((TextView) findViewById(R.id.otherParty)).setText(orderDoc.getString("cookEmail"));
        }

        spinnerNum = 0;
        spinnerIndex = 0;
        if(orderDoc.getDouble("delivered").intValue() == 1 && userClass == Client.class){        //only Clients can update received status and only if is has been delivered
            findViewById(R.id.receivedSpinner).setEnabled(true);
            spinnerNum = 3;
        }else if(orderDoc.getDouble("approved") == 1 && userClass == Cook.class){       //only cooks can update delivered status and only if it is approved
            findViewById(R.id.deliveredSpinner).setEnabled(true);
            spinnerNum = 2;
        }else if(orderDoc.getDouble("approved") == 0 && userClass == Cook.class){       //only cooks can update approved status and only if a status is not set
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
        spinner.setSelection(adapter.getPosition(orderDoc.getString(field)));
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
        rating = (Integer) view.getTag();
        setRating();
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

    public void onClickApplyChangesButton(View view){
        //rating;
        //((Spinner) findViewById(R.id.receivedSpinner)).getSelectedItemPosition();
        //((Spinner) findViewById(R.id.deliveredSpinner)).getSelectedItemPosition();
        //((Spinner) findViewById(R.id.approvedSpinner)).getSelectedItemPosition();

        //update meal rating
        //update cook rating
    }

    public void onCLickReturn(View view){
        finish();
    }

    private void throwToast(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }
}