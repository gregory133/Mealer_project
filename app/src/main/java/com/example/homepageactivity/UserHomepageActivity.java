package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity {

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        setupPageSelectSpinner((Spinner) findViewById(R.id.pagesSpinner));

        String userType;
        switch (getIntent().getExtras().getString("UserType")) {
            case "clnt":
                userType = "Client";
                break;
            case "cook":
                userType = "Cook";
                break;
            case "admn":
                userType = "Admin";
                break;
            default:
                userType = "";
                break;
        }
        Toast.makeText(this, "Welcome "+userType+"!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String email = currentUser.getEmail();
//            String userType = "error";
//            switch (email.substring(0,4)) {
//                case "clnt":
//                    userType = "client";
//                    break;
//                case "cook":
//                    userType = "cook";
//                    break;
//                case "admn":
//                    userType = "Admin";
//                    break;
//                default:
//                    userType = "";
//                    break;
//            }
//            Toast.makeText(this, "Welcome "+userType+"!", Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        data.getIntExtra("Logout", 0);

        switch (data.getIntExtra("Logout", 0)) {
            case 1:
                HomepageLogout();
                break;
            default:
                break;
        }
    }

    public void setupPageSelectSpinner(Spinner pagesSpinner){
        Class homepageClass = UserHomepageActivity.class;
        final List<String> pageNames = Arrays.asList("Menu","Logout");
        final List<Class> pageClasses = Arrays.asList(homepageClass, null);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_layout,
                pageNames);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        pagesSpinner.setAdapter(adapter);

        pagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(pageClasses.get(i) != null){
                    if(this.getClass().getName().contains(pageClasses.get(i).getName())) return;
                    finish();
                    startActivity(new Intent(getApplicationContext(), pageClasses.get(i)));
                }else if(pageNames.get(i) == "Logout"){
                    UserLogoutRequest();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void UserLogoutRequest(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Logout", 1);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void HomepageLogout(){
        //Logout Operations///////////////////////////////////////////////////////
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