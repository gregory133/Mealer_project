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
    private User currentAccount;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        setupPageSelectSpinner((Spinner) findViewById(R.id.pagesSpinner));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            //Getting user type
            FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
            DocumentReference docRef = firestoreDB.collection("users").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        onSuccessfulDocumentRetrival(document);
                    } else {
                        onFailedDocumentRetrival();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
        }

    }

    private void onSuccessfulDocumentRetrival(DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(this, "Error, user document does not exist", Toast.LENGTH_LONG).show();
            return;
        }

        userRole = document.getString("role");
        switch (userRole) {
            case "Cook":
                currentAccount = document.toObject(Cook.class);
                /////////// TEMP - FOR TESTING PURPOSES ///////////
                Intent intent = new Intent(getApplicationContext(), AddMealActivity.class);
                startActivity(intent);
                ///////////////////////////////////////////////////
                break;
            case "Client":
                currentAccount = document.toObject(Client.class);
                break;
            case "Admin":
                currentAccount = document.toObject(Admin.class);
                break;
            default:
                Toast.makeText(this, "Error, account doesn't exist/doesn't have a type", Toast.LENGTH_LONG).show();
                return;
        }
        if (currentAccount.isBanned()) {
            showSuspensionNotice(" permanently");
        } else if (currentAccount.getBannedUntil().after(new Date())) {
            String until = currentAccount.getBannedUntil().toString();
            showSuspensionNotice(" until\n"+until);
        } else {
            Toast.makeText(this, "Welcome " + userRole + "!", Toast.LENGTH_LONG).show();
        }
    }

    private void onFailedDocumentRetrival() {
        Toast.makeText(this, "Error, failed to retrieve user document", Toast.LENGTH_LONG).show();
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

    private void showSuspensionNotice(String addon) {
        Dialog notice = new Dialog(this);
        notice.setContentView(R.layout.user_suspension_notice);
        TextView noticeText = notice.findViewById(R.id.userSuspensionNoticeText);
        String noticeString = (String)noticeText.getText();
        noticeText.setText(noticeString+addon);
        notice.setCancelable(false);
        notice.setCanceledOnTouchOutside(false);
        //NOTE: technically this dialogue never gets dismissed but the activity changes so it shouldn't be a problem
        notice.show();

        //this makes it so that the dialog window fits nicely within the screen
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        WindowManager.LayoutParams params = notice.getWindow().getAttributes();
        params.width = (6 * width)/7;
        notice.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private void UserLogoutRequest(){
        FirebaseAuth.getInstance().signOut();
        currentAccount = null;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void UserLogout(View view) {
        UserLogoutRequest();
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