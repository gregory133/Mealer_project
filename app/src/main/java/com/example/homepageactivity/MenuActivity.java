package com.example.homepageactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.GridView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.homepageactivity.domain.MealsGridAdapter;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.PageIconsAdapter;
import com.example.homepageactivity.domain.Validator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MenuActivity extends AppCompatActivity {
    private GridView mealsGrid;
    private int meals[];
    private FirebaseFirestore db;
    private static final String TAG = "MenuActivity";
    private ArrayList<QueryDocumentSnapshot> items;
    private QueryDocumentSnapshot docRef;
    private static final String logoutText = "Logout";
    private static final ArrayList<PageIconInfo> pageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Menu", MenuActivity.class));
        add(new PageIconInfo("Inbox", InboxActivity.class));
        add(new PageIconInfo(logoutText, null));
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
        PageIconsAdapter adapter=new PageIconsAdapter(getApplicationContext(), pageIconOptions);
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
                if (this.getClass().getName().contains(pageIconOptions.get(i).getPage().getName())){
                    return;
                }    //Don't reload this page
                Intent intent=new Intent(getApplicationContext(), pageIconOptions.get(i).getPage());
                intent.putExtra("userRole",  getIntent().getStringExtra("userRole"));
                startActivity(intent);
                finish();
            }
        });
    }

    private void LogoutRequest(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void getMealsForMealGrid(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            loginAttemptFailure("Could not load menu");
        }
        db = FirebaseFirestore.getInstance();
        db.collection("meals")
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
        mealsGrid = (GridView) findViewById(R.id.mealsGrid);
        MealsGridAdapter iconsAdapter = new MealsGridAdapter(getApplicationContext(), items);
        mealsGrid.setAdapter(iconsAdapter);

        mealsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "MealIconSelected:");
                Intent intent = new Intent(getApplicationContext(), EditMealActivity.class);
                intent.putExtra("mealID", items.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void loginAttemptFailure(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }

    public void onClickAddMealButton(View view){
        Intent intent = new Intent(getApplicationContext(), AddMealActivity.class);
        startActivity(intent);
    }
}