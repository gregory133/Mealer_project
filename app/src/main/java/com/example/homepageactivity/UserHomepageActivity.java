package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class UserHomepageActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        setupPageSelectSpinner((Spinner) findViewById(R.id.pagesSpinner));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            //Getting user type
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(currentUser.getUid());
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
        Toast.makeText(this, "Welcome "+userRole+"!", Toast.LENGTH_LONG).show();
    }

    private void onFailedDocumentRetrival() {
        Toast.makeText(this, "Error, failed to retrieve user document", Toast.LENGTH_LONG).show();
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
                    finish();
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

    public void UserLogoutRequest(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("Logout", 1);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void HomepageLogout(){
        //Logout Operations///////////////////////////////////////////////////////
        FirebaseAuth.getInstance().signOut();
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