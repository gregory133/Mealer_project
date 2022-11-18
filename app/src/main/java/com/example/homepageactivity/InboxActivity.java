package com.example.homepageactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.*;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InboxActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String userRole; //Client, Admin or Cook
    TextView titleText;
    ListView listView;
    FirebaseFirestore firestoreDB;
    private static final String TAG = "InboxActivity";

    private ImageView background;
    private ArrayList<QueryDocumentSnapshot> items;
    private Dialog currentMessage;
    private QueryDocumentSnapshot docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        background=findViewById(R.id.background);
        userRole = getIntent().getStringExtra("userRole");
        titleText=findViewById(R.id.title);
        listView=findViewById(R.id.list);
        setThemeColors(userRole);
//        setTitle();
//        hookDropDown();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            firestoreDB = FirebaseFirestore.getInstance();
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


//    private void hookDropDown(){
//        final List<String> options = Arrays.asList("Settings", "Return");
//        ArrayAdapter adapter=new ArrayAdapter(this, R.layout.dropdown_layout, options);
//        adapter.setDropDownViewResource(R.layout.dropdown_layout);
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (options.get(i).equals("Return")) {
//                    logout();
//                }
//            }
//        }
//    }

    private void collapseAdminButtons(){
        if (!userRole.equals("Admin")){
            LinearLayout adminRow=findViewById(R.id.row4);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(


    private void setThemeColors(String mode){
        ContextWrapper wrapper=null;
        if (mode.equals("Cook")){
            wrapper=new ContextThemeWrapper(this, R.style.cook_style);

        }
        else if (mode.equals("Client")){
            wrapper=new ContextThemeWrapper(this, R.style.client_style);
        }
        else{
            wrapper=new ContextThemeWrapper(this, R.style.client_style);
        }
        background.setImageDrawable(StyleApplyer.applyTheme(getApplicationContext(), wrapper,R.drawable.ic_wave));

    }

//    private void hookDropDown(){
//        final List<String> options = Arrays.asList("Settings", "Logout");
//        ArrayAdapter adapter=new ArrayAdapter(this, R.layout.dropdown_layout, options);
//        adapter.setDropDownViewResource(R.layout.dropdown_layout);
//        spinner.setAdapter(adapter);
//
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (options.get(i).equals("Logout")){
//                    logout();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//    }

    private void setTitle(){
        HashMap<String, String> titleDict=new HashMap<String, String>(){{
            put("Admin", "User Complaints");
            put("Cook", "Messages");
            put("Client", "Messages");
        }};
        titleText.setText(titleDict.get(userRole));

    }
    private void returnHomepage(){
//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("Logout", 1);
//        setResult(RESULT_OK, returnIntent);
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

    private void showMessage() {
//        currentMessage = new Dialog(this);
//        currentMessage.setContentView(R.layout.inbox_description);

        Message selectedMessage = docRef.toObject(Message.class);
//        TextView subjectText=currentMessage.findViewById(R.id.subject);
//        TextView descText=currentMessage.findViewById(R.id.description);
//        subjectText.setText(selectedMessage.getSubject());
//        descText.setText(selectedMessage.getBodyText());
//        currentMessage.show();

        Intent intent=new Intent(this, InboxMessageActivity.class);
        intent.putExtra("userRole", userRole);
        intent.putExtra("subjectText", selectedMessage.getSubject());
        intent.putExtra("descText", selectedMessage.getBodyText());
//        intent.putExtra("userRole", userRole);

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
        currentMessage.dismiss();
    }

    public void onClickLogout(View view){
        returnHomepage();
    }
    public void onClickMessage(View view){
        Toast.makeText(this, "not yet implemented", Toast.LENGTH_LONG).show();
    }
    public void onClickMealer(View view){
        Toast.makeText(this, "not yet implemented", Toast.LENGTH_LONG).show();
    }

}