package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class UserHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        System.out.println("at homepage");
        //SetNotificationIconStatus(false);
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

    private void HomepageLogout(){
        //Logout Operations///////////////////////////////////////////////////////
        finish();
    }

    /**Opens UserAccountActivity*/
    public void onClickUserAccountButton(View view){
        Intent intent = new Intent(getApplicationContext(), UserAccountActivity.class);
        startActivityForResult(intent,0);
        System.out.println("to account");
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