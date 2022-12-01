package com.example.homepageactivity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.AdapterPageIcon;
import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.Meal;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CookProfileActivity extends AppCompatActivity {
    private Cook cook;
    private static final String logoutText = "Logout";
    private static final ArrayList<PageIconInfo> cookPageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.inbox_icon_2));
        add(new PageIconInfo("Menu", MenuActivity.class, R.drawable.menu));
        add(new PageIconInfo("MealOrders", MealOrdersActivity.class, R.drawable.orders));
        add(new PageIconInfo("Profile", CookProfileActivity.class, R.drawable.profile));
        add(new PageIconInfo(logoutText, null, R.drawable.door_icon));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_profile);

        setupUserPages(R.id.pagesGridCookProfile);
        getCookFromFirebase();
    }

    private void setupUserPages(int viewID){
        Class<? extends User> aClass = currentAccount.getClass();       //only Cook gets set up here
        if (!Cook.class.equals(aClass)) {
            findViewById(viewID).setBackgroundColor(getResources().getColor(R.color.cook_light));
            return;
        }

        GridView pagesGrid;
        pagesGrid = findViewById(viewID);
        pagesGrid.setNumColumns(getUserPagesOptions().size());
        AdapterPageIcon adapter=new AdapterPageIcon(getApplicationContext(), getUserPagesOptions(), this.getClass());
        pagesGrid.setAdapter(adapter);

        pagesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onPageSelected:");

                if (getUserPagesOptions().get(i).getIconName().equals(logoutText)) {        //logout MUST be last
                    LogoutRequest();
                    Toast.makeText(getApplicationContext(), "logout at "+i+"", Toast.LENGTH_LONG).show();
                    return;
                }
                if (this.getClass().getName().contains(getUserPagesOptions().get(i).getPageClass().getName())){
                    return;
                }    //Don't reload this page
                Intent intent=new Intent(getApplicationContext(), getUserPagesOptions().get(i).getPageClass());
                intent.putExtra("userRole",  getIntent().getStringExtra("userRole"));
                startActivity(intent);
                finish();
            }
        });
    }
    private ArrayList<PageIconInfo> getUserPagesOptions(){
        Class<? extends User> aClass = currentAccount.getClass();
        if (Cook.class.equals(aClass)) {
            return cookPageIconOptions;
        }
        return null;
    }
    private void LogoutRequest(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }



    private void getCookFromFirebase(){
        String cookUID;
        Class hold = currentAccount.getClass();
        if (hold == Cook.class){
            cookUID = firebaseAuth.getUid();
        }else{
            Intent intent=getIntent();
            cookUID = intent.getStringExtra("cookUID");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> task=db.collection("users").document(cookUID).get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try{
                    cook = documentSnapshot.toObject(Cook.class);
                    setupUI();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Could not load Meal Information", Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void setupUI(){
        ((TextView)findViewById(R.id.firstName)).setText(cook.getFirstName());
        ((TextView)findViewById(R.id.lastName)).setText(cook.getLastName());
        ((TextView)findViewById(R.id.email)).setText(cook.getEmailAddress());
        ((TextView)findViewById(R.id.description)).setText(cook.getDesc());
        if(cook.getNumRatings() != 0){
            setupStars(cook.getRatingTotal()/cook.getNumRatings());
        }
    }



    private void setupStars(double rating){
        int[] stars;
        stars = new int[]{
                R.id.cookStar1Icon,
                R.id.cookStar2Icon,
                R.id.cookStar3Icon,
                R.id.cookStar4Icon,
                R.id.cookStar5Icon};

        for(int i=1;i< stars.length+1;i++){
            if(i<=rating){
                ((ImageView)findViewById(stars[i-1])).setColorFilter(getResources().getColor(R.color.client_light));
            }else{
                ((ImageView)findViewById(stars[i-1])).setColorFilter(getResources().getColor(R.color.off_grey));
            }
        }
    }
}