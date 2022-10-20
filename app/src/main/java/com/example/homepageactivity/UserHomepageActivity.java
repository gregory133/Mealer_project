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

import java.util.Arrays;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        setupPageSelectSpinner((Spinner) findViewById(R.id.pagesSpinner));


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
        //final Spinner spinner = findViewById(R.id.pagesSpinner);
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_spinner_item,
                pageNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pagesSpinner.setAdapter(adapter);

        pagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String pageSelectedName = pagesSpinner.getSelectedItem().toString();
                if(pageClasses.get(i) != null){
                    if(this.getClass().getName().contains(pageClasses.get(i).getName())) return;    //this.getClass() instanceof pageClasses.get(i)
                    startActivityForResult(new Intent(getApplicationContext(), pageClasses.get(i)),0);
                }else if(pageNames.get(i) == "Logout"){
                    UserLogoutRequest();
                }//1234@56.78
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

    /**Opens UserAccountActivity*/
    public void onClickUserAccountButton(View view){
        Intent intent = new Intent(getApplicationContext(), UserAccountActivity.class);
        startActivityForResult(intent,0);
    }

//    /**Opens UserInboxActivity*/
//    public void onClickNotificationIconButton(View view){
//        Intent intent = new Intent(getApplicationContext(), UserInboxActivity.class);
//        startActivityForResult(intent,0);
//    }
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