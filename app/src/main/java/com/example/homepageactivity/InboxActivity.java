package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Client;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.AdapterInboxMessage;
import com.example.homepageactivity.domain.Message;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.AdapterPageIcon;
import com.example.homepageactivity.domain.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {

    private static final String TAG = "InboxActivity";

    private ArrayList<QueryDocumentSnapshot> items;

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
        setContentView(R.layout.activity_inbox);

        collapseAdminButtons();
        setThemeColors();
        setupUserPages(R.id.pagesGrid);
        getMessages();
    }

    private void getMessages(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestoreDB.collection("messages")
                    .whereEqualTo("recipientUID", currentUser.getUid()).whereEqualTo("archived", false)
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
                            populateInbox();
                        }
                    });
        } else {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
        }
    }

    private void populateInbox(){
        AdapterInboxMessage adapter=new AdapterInboxMessage(this, R.layout.activity_inbox_list_item, items);
        ListView listView = (ListView) findViewById(R.id.messagesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected:");
                OpenMessage(i);
            }
        });
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

    private void collapseAdminButtons(){
        if (!(currentAccount.getClass() == Admin.class)){
            LinearLayout adminRow=findViewById(R.id.row4);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f
            );
            adminRow.setLayoutParams(param);
        }
    }

    private void setThemeColors(){
        Class hold = currentAccount.getClass();
        if (hold == Cook.class){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        } else {
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }
    public void onCLickNewMessage(View view){
        startActivity(new Intent(this, InboxWriteMessageActivity.class));
    }

    private void OpenMessage(int i) {
        Message selectedMessage = items.get(i).toObject(Message.class);

        Intent intent=new Intent(this, InboxMessageActivity.class);
        intent.putExtra("messageUID", items.get(i).getId());
        intent.putExtra("senderName", ((TextView)((ListView)findViewById(R.id.messagesList)).getChildAt(i).findViewById(R.id.cuisineType)).getText());

        int requestCode=1;
        startActivityForResult(intent, requestCode);
    }
}