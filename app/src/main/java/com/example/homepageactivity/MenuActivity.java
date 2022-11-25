package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.View;
import android.widget.Toast;

import com.example.homepageactivity.domain.AdapterMenuMeal;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.AdapterPageIcon;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MenuActivity extends AppCompatActivity {
    private GridView mealsGrid;
    private int meals[];
    private static final String TAG = "MenuActivity";
    private ArrayList<QueryDocumentSnapshot> items;
    private QueryDocumentSnapshot docRef;
    private static final String logoutText = "Logout";
    private static final ArrayList<PageIconInfo> pageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.ic_message_icon));
        add(new PageIconInfo("Menu", MenuActivity.class, R.drawable.m_icon));
        add(new PageIconInfo(logoutText, null, R.drawable.ic_door_icon));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        meals = new int[0];

        setupUserPages(R.id.pagesGrid);
        getMealsForMealGrid();
    }

    private void setupUserPages(int viewID){

        GridView pagesGrid = (GridView) findViewById(viewID);
        pagesGrid.setNumColumns(pageIconOptions.size());
        AdapterPageIcon adapter=new AdapterPageIcon(getApplicationContext(), pageIconOptions, this.getClass());
        pagesGrid.setAdapter(adapter);

        pagesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onPageSelected:");

                if (pageIconOptions.get(i).getIconName() == logoutText) {        //logout MUST be last
                    LogoutRequest();
                    Toast.makeText(getApplicationContext(), "logout at "+i+"", Toast.LENGTH_LONG).show();
                    return;
                }
                if (this.getClass().getName().contains(pageIconOptions.get(i).getPageClass().getName())){
                    return;
                }    //Don't reload this page
                Intent intent=new Intent(getApplicationContext(), pageIconOptions.get(i).getPageClass());
                startActivity(intent);
                finish();
            }
        });
    }

    private void LogoutRequest(){
        firebaseAuth.signOut();
        finish();
    }

    private void getMealsForMealGrid(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            loginAttemptFailure("Could not load menu");
            return;
        }
        firestoreDB.collection("meals")
                .whereEqualTo("cookUID", currentUser.getUid())
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

    protected void setUpMealsGrid() {
        mealsGrid = (GridView) findViewById(R.id.ordersGrid);
        AdapterMenuMeal iconsAdapter = new AdapterMenuMeal(getApplicationContext(), items);
        mealsGrid.setAdapter(iconsAdapter);

        mealsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "MealIconSelected:");
                Intent intent = new Intent(getApplicationContext(), MealEditActivity.class);
                intent.putExtra("mealID", items.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void loginAttemptFailure(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }

    public void onClickAddMealButton(View view){
        Intent intent = new Intent(getApplicationContext(), MealAddActivity.class);
        startActivity(intent);
    }
}