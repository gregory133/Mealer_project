package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
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
import com.example.homepageactivity.domain.ComplaintMessage;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.ItemListAdapter;
import com.example.homepageactivity.domain.Message;
import com.example.homepageactivity.domain.PageIconInfo;
import com.example.homepageactivity.domain.PageIconsAdapter;
import com.example.homepageactivity.domain.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InboxActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ListView listView;
    private static final String TAG = "InboxActivity";

    private ArrayList<QueryDocumentSnapshot> items;
    private QueryDocumentSnapshot docRef;

    private static final String logoutText = "Logout";
    private static final ArrayList<PageIconInfo> clientPageIconOptions = new ArrayList<PageIconInfo>() {{
        add(new PageIconInfo("Inbox", InboxActivity.class, R.drawable.ic_message_icon));
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
        listView=findViewById(R.id.messagesList);
        collapseAdminButtons();
        setThemeColors();
        setupUserPages(R.id.pagesGrid);

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
                            hookList();
                        }
                    });
        } else {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
        }
    }

    private void setupUserPages(int viewID){
        GridView pagesGrid;
        pagesGrid = (GridView) findViewById(viewID);
        pagesGrid.setNumColumns(getUserPagesOptions().size());
        PageIconsAdapter adapter=new PageIconsAdapter(getApplicationContext(), getUserPagesOptions(), this.getClass());
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

    private void returnHomepage(){
        finish();
    }
    private void hookList(){
        ItemListAdapter adapter=new ItemListAdapter(this, R.layout.inbox_list_item, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected:");
                docRef = items.get(i);
                showMessage();
            }
        });
    }

    public void onCLickNewMessage(View view){
        Toast.makeText(getApplicationContext(), "New Message", Toast.LENGTH_LONG).show();
    }

    private void showMessage() {
        Message selectedMessage = docRef.toObject(Message.class);

        Intent intent=new Intent(this, InboxMessageActivity.class);
        intent.putExtra("subjectText", selectedMessage.getSubject());
        intent.putExtra("senderUID", selectedMessage.getSenderUID());
        intent.putExtra("descText", selectedMessage.getBodyText());

        int requestCode=1;
        startActivityForResult(intent, requestCode);

    }

    public void onClickBanCook(View view) {
        String cookUID = docRef.toObject(ComplaintMessage.class).getCookUID();
        if (cookUID != null) {
            Map<String, Boolean> change = new HashMap<>(1);
            change.put("banned", true);
            firestoreDB.collection("users").document(cookUID).set(change, SetOptions.merge());
        } else {
            Toast.makeText(this, "Error, no cook UID found", Toast.LENGTH_LONG).show();
        }
        archiveMessage();
    }

    public void onClickSuspendCook(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DATE));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Timestamp timestamp = new Timestamp(new Date(year, month, day));

        String cookUID = docRef.toObject(ComplaintMessage.class).getCookUID();
        if (cookUID != null) {
            Map<String, Timestamp> change = new HashMap<>(1);
            change.put("bannedUntil", timestamp);
            firestoreDB.collection("users").document(cookUID).set(change, SetOptions.merge());
        } else {
            Toast.makeText(this, "Error, no cook UID found", Toast.LENGTH_LONG).show();
        }
        archiveMessage();
    }

    public void onClickDismiss(View view) {
        archiveMessage();
    }

    private void archiveMessage(){
        Map<String, Boolean> change = new HashMap<>(1);
        change.put("archived", true);
        String msgID = docRef.getId();
        firestoreDB.collection("messages").document(msgID).set(change, SetOptions.merge());
    }

    public void onClickLogout(View view){
        returnHomepage();
    }
}