package com.example.homepageactivity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.homepageactivity.domain.AdapterMealOrder;
import com.example.homepageactivity.domain.AdapterMenuMeal;
import com.example.homepageactivity.domain.AdapterPageIcon;
import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MealOrdersActivity extends AppCompatActivity {

    private static final String logoutText = "Logout";
    private static final ArrayList<PageIconInfo> clientPageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.ic_message_icon));
        add(new PageIconInfo("MealSearch", MealSearchActivity.class, R.drawable.m_icon));
        add(new PageIconInfo(logoutText, null, R.drawable.ic_door_icon));
    }};
    private static final ArrayList<PageIconInfo> cookPageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.ic_message_icon));
        add(new PageIconInfo("Menu", MenuActivity.class, R.drawable.m_icon));
        add(new PageIconInfo(logoutText, null, R.drawable.ic_door_icon));
    }};
    private static final ArrayList<PageIconInfo> adminPageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.ic_message_icon));
        add(new PageIconInfo(logoutText, null, R.drawable.ic_door_icon));
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_orders);

        setupUserPages(R.id.pagesGrid);
        setThemeColors();
        getMealsForMealGrid();
    }

    private void setupUserPages(int viewID){
        GridView pagesGrid;
        pagesGrid = (GridView) findViewById(viewID);
        pagesGrid.setNumColumns(getUserPagesOptions().size());
        AdapterPageIcon adapter=new AdapterPageIcon(getApplicationContext(), getUserPagesOptions(), this.getClass());
        pagesGrid.setAdapter(adapter);

        pagesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onPageSelected:");

                if (getUserPagesOptions().get(i).getIconName() == logoutText) {        //logout MUST be last
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
    private final ArrayList<PageIconInfo> getUserPagesOptions(){
        Class<? extends User> aClass = currentAccount.getClass();
        if (Client.class.equals(aClass)) {
            return clientPageIconOptions;
        } else if (Cook.class.equals(aClass)) {
            return cookPageIconOptions;
        } else if (Admin.class.equals(aClass)) {
            return adminPageIconOptions;
        }
        return null;
    }
    private void LogoutRequest(){
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void setThemeColors(){
        Class hold = currentAccount.getClass();
        if (hold == Cook.class){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        } else {
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }

    private void getMealsForMealGrid(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Could not load Orders", Toast.LENGTH_LONG).show();
            return;
        }
        firestoreDB.collection("orders")
                .whereEqualTo("clientUID", currentUser.getUid())        //since UIDs are unique, there will not be overlap between cook and client
                .whereEqualTo("cookUID", currentUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<QueryDocumentSnapshot> orderDocs = new ArrayList<>();
                        for (QueryDocumentSnapshot msg : value) {
                            orderDocs.add(msg);
                        }
                        setUpMealsGrid(orderDocs);
                    }
                });
    }
    protected void setUpMealsGrid(ArrayList<QueryDocumentSnapshot> orderDocs) {
        GridView ordersGrid = (GridView) findViewById(R.id.ordersGrid);
        AdapterMealOrder iconsAdapter = new AdapterMealOrder(getApplicationContext(), orderDocs);
        ordersGrid.setAdapter(iconsAdapter);

        ordersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "MealOrderIconSelected:");
//                Intent intent = new Intent(getApplicationContext(), MealEditActivity.class);
//                intent.putExtra("mealID", orderDocs.get(position).getId());
//                startActivity(intent);
            }
        });
    }
}