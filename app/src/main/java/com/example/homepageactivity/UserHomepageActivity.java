package com.example.homepageactivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        setupPageSelectSpinner((Spinner) findViewById(R.id.pagesSpinner));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRole = getIntent().getStringExtra("userRole");
            Toast.makeText(this, "Welcome Client!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void setupPageSelectSpinner(Spinner pagesSpinner){
        Class homepageClass = UserHomepageActivity.class;
        //names needs to correspond to the classes
        final List<String> pageNames = Arrays.asList("Settings", "Logout", "Inbox");
        final List<Class> pageClasses = Arrays.asList(null, null, InboxActivity.class);


        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_layout,
                pageNames);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        pagesSpinner.setAdapter(adapter);

        pagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (pageClasses.get(i) != null) {
                    if (this.getClass().getName().contains(pageClasses.get(i).getName())) return;
                    Intent intent=new Intent(getApplicationContext(), pageClasses.get(i));
                    intent.putExtra("userRole", userRole);
                    startActivity(intent);
                } else if (pageNames.get(i).equals("Logout")) {
                    UserLogoutRequest();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void UserLogoutRequest(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
//
//    /**Opens MealSearchActivity*/
//    public void onClickMealSearchButton(View view){
//        Intent intent = new Intent(getApplicationContext(), MealSearchActivity.class);
//        startActivityForResult(intent,0);
//    }
//
//    /**Opens MealInfoActivity*/
//    public void onClickMealRecomendationButton(View view){
//        Intent intent = new Intent(getApplicationContext(), MealInfoActivity.class);
//        startActivityForResult(intent,0);
//    }

    /**Sets the visibility and intractability of the NotificationIconButton*/
    private void SetNotificationIconStatus(boolean active){
        View notificationIcon = findViewById(R.id.NotificationIcon);
        notificationIcon.setClickable(active);
        if(active){
            notificationIcon.setVisibility(View.VISIBLE);
        }else{
            notificationIcon.setVisibility(View.INVISIBLE);
        }
    }
}