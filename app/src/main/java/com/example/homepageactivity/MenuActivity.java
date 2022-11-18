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

import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.PageIconsAdapter;
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
    ArrayList<PageIconInfo> pageIconOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        meals = new int[0];
        pageIconOptions = new ArrayList<>();

        //setupUserPages(R.id.pagesList);
        getMealsForMealGrid();
    }

    //DOES NOT WORK. FINAL ARRAYLIST MUST BE INITIALIZED
    private void setupUserPages(int viewID){
        pageIconOptions.add(new PageIconInfo("menu", MenuActivity.class));
        pageIconOptions.add(new PageIconInfo("inbox", InboxActivity.class));
        pageIconOptions.add(new PageIconInfo("logout", null));        //logout MUST be last
        final ArrayList<PageIconInfo> finalPageIconOptions = new ArrayList<>();
        //finalPageIconOptions = pageIconOptions;

        PageIconsAdapter adapter=new PageIconsAdapter(getApplicationContext(), finalPageIconOptions);
        ListView listView=findViewById(viewID);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onPageSelected:");

                if (i == pageIconOptions.size()-1) {        //logout MUST be last
                    LogoutRequest();
                    return;
                }
                if (this.getClass().getName().contains(pageIconOptions.get(i).getClass().getName())) return;    //Don't reload this page

                Intent intent=new Intent(getApplicationContext(), pageIconOptions.get(i).getClass());
                //intent.putExtra("userRole", userRole);
                startActivity(intent);
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
        PageIconsAdapter iconsAdapter = new PageIconsAdapter(getApplicationContext(), pageIconOptions);
        mealsGrid.setAdapter(iconsAdapter);

        mealsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "MealIconSelected:");
                Intent intent = new Intent(getApplicationContext(), EditMealActivity.class);
                intent.putExtra("mealID", items.get(position).getId());
                startActivity(intent);
                finish();
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