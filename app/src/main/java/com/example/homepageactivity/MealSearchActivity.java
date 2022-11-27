package com.example.homepageactivity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.AdapterMenuMeal;
import com.example.homepageactivity.domain.Meal;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.AdapterPageIcon;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MealSearchActivity extends AppCompatActivity {
    private ArrayList<QueryDocumentSnapshot> allValidMeals; //backup so we don't have to get a new list every time
    private ArrayList<QueryDocumentSnapshot> relevantMeals;
    private final List<String> cuisineOptions = Arrays.asList("Cuisine", "American", "Mexican", "Chinese", "Other");      //Also hardcoded in AddMealActivity
    private final List<String> mealTypeOptions = Arrays.asList("Meal Type", "Appetizer", "Entree", "Dessert", "Other");      //Also hardcoded in AddMealActivity
    private static final String logoutText = "Logout";
    private static final ArrayList<PageIconInfo> pageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.ic_message_icon));
        add(new PageIconInfo("MealSearch", MealSearchActivity.class, R.drawable.m_icon));
        add(new PageIconInfo(logoutText, null, R.drawable.ic_door_icon));
    }};
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_search);

        startTime = System.currentTimeMillis();

        setupUserPages(R.id.pagesGrid);
        setupCuisineOrTypeSpinner((Spinner) findViewById(R.id.cuisineType), cuisineOptions);
        setupCuisineOrTypeSpinner((Spinner) findViewById(R.id.mealType), mealTypeOptions);
        onSearchByNameFieldChange();
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

                if (pageIconOptions.get(i).getIconName().equals(logoutText)) {        //logout MUST be last
                    LogoutRequest();
                    Toast.makeText(getApplicationContext(), "logout at "+i+"", Toast.LENGTH_LONG).show();
                    return;
                }
                if (this.getClass().getName().contains(pageIconOptions.get(i).getPageClass().getName())){
                    return;
                }    //Don't reload this page
                Intent intent=new Intent(getApplicationContext(), pageIconOptions.get(i).getPageClass());
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
        Timestamp currentDate = new Timestamp(Calendar.getInstance().getTime());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("meals")
                .whereEqualTo("offered", true)
                .whereLessThan("bannedUntil", currentDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        allValidMeals = new ArrayList<>();
                        relevantMeals = new ArrayList<>();
                        for (QueryDocumentSnapshot msg : value) {
                            allValidMeals.add(msg);
                            relevantMeals.add(msg);
                        }
                        setUpMealsGrid();
                    }
                });
    }

    protected void setUpMealsGrid() {
        GridView mealsGrid = (GridView) findViewById(R.id.mealsGrid);
        AdapterMenuMeal iconsAdapter = new AdapterMenuMeal(getApplicationContext(), relevantMeals);
        mealsGrid.setAdapter(iconsAdapter);

        mealsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.d(TAG, "MealIconSelected:");

                Intent intent = new Intent(getApplicationContext(), MealInfoActivity.class);
                Meal meal = relevantMeals.get(position).toObject(Meal.class);

                intent.putExtra("mealType", meal.getMealType());
                intent.putExtra("mealName", meal.getMealName());
                intent.putExtra("description", meal.getDescription());
                intent.putExtra("cuisineType", meal.getCuisineType());
                intent.putExtra("ingredients", meal.getIngredients());
                intent.putExtra("allergens", meal.getAllergens());
                intent.putExtra("price", Double.toString(meal.getPrice()));

                String cookUID=meal.getCookUID();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Task<DocumentSnapshot> task=db.collection("users").document(cookUID).get();

                task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Log.d("TAG", documentSnapshot.get("firstName").toString()+documentSnapshot.get("lastName").toString()+documentSnapshot.get("shortDescription").toString());
                        intent.putExtra("cookFirstName", documentSnapshot.get("firstName").toString());
                        intent.putExtra("cookLastName", documentSnapshot.get("lastName").toString());
                        intent.putExtra("cookDesc", documentSnapshot.get("shortDescription").toString());
                        startActivity(intent);
                    }
                });



            }
        });
    }

    public void setupCuisineOrTypeSpinner(Spinner spinner, final List<String> options){
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_layout,
                options);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(System.currentTimeMillis() - startTime < 500){loginAttemptFailure((startTime-System.currentTimeMillis())+""); return;}     //BUG FIX: ignore for half a second so the meals are not overwritten
                if(i==0){
                    updateMeals("mealName", "");
                    return;
                }
                switch (options.get(0)){
                    case "Cuisine":
                        updateMeals("cuisineType", options.get(i));
                        break;
                    case "Meal Type":
                        updateMeals("mealType", options.get(i));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void loginAttemptFailure(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }

    private void onSearchByNameFieldChange(){
        TextView searchByNameField = (TextView) findViewById(R.id.searchByNameField);
        searchByNameField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateMeals("mealName", s.toString());
            }
        });
    }

    private void updateMeals(String field, String subString){
        relevantMeals.clear();
        for(QueryDocumentSnapshot meal : allValidMeals){
            if(meal.getString(field).toLowerCase(Locale.ROOT).contains(subString.toLowerCase(Locale.ROOT))) {
                relevantMeals.add(meal);
            }
        }
        setUpMealsGrid();
    }
}