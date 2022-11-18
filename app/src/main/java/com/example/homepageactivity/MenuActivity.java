package com.example.homepageactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.View;
import android.widget.Toast;

import com.example.homepageactivity.domain.ItemListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MenuActivity extends AppCompatActivity {
    GridView mealsGrid;
    int meals[];
    FirebaseFirestore db;
    private static final String TAG = "MenuActivity";
    private ArrayList<QueryDocumentSnapshot> items;
    private QueryDocumentSnapshot docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        meals = new int[0];

        getMealsForMealGrid();
    }

    private void getMealsForMealGrid(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            loginAttemptFailure("Could not load menu");
        }
        db = FirebaseFirestore.getInstance();
        db.collection("messages")
                .whereEqualTo("recipientUID", currentUser.getUid()).whereEqualTo("archived", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        items = new ArrayList<>();
                        for (QueryDocumentSnapshot msg : value) {
                            items.add(msg);
                        }
                        setUpMealsGrid();
                    }
                });
    }

    private void setUpMealsGrid(){
        mealsGrid = (GridView) findViewById(R.id.mealsGrid);
        MealsGridAdapter customAdapter = new MealsGridAdapter(getApplicationContext(), items);
        mealsGrid.setAdapter(customAdapter);
        // implement setOnItemClickListener event on GridView
        mealsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Log.d(TAG, "onItemSelected:");
                // set an Intent to Another Activity
                Toast.makeText(getApplicationContext(), "Error, no user signed in", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(MenuActivity.this, MealActivity.class);
                //intent.putExtra("image", items.get(i).getId()); // put image data in Intent
                //startActivity(intent); // start Intent
                //docRef = items.get(i);
            }
        });
    }

    private void loginAttemptFailure(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }


//    public onClickLogin(){
//        //Intent intent =
//    }
}