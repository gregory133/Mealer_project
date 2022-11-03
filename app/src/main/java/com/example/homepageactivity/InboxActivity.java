package com.example.homepageactivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    String userRole; //client, admin or cook
    TextView titleText;
    Spinner spinner;
    ListView listView;
    FirebaseFirestore db;
    private static final String TAG = "InboxActivity";

    ArrayList<Message> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        userRole = getIntent().getStringExtra("userRole");
        titleText=findViewById(R.id.title);
        spinner=findViewById(R.id.spinner);
        listView=findViewById(R.id.list);
        setTitle();
        hookDropDown();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            db = FirebaseFirestore.getInstance();
            db.collection("messages")
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
                                items.add(msg.toObject(Message.class));
                            }
                            hookList();
                        }
                    });
        } else {
            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
        }
    }

    private void hookDropDown(){
        final List<String> options = Arrays.asList("Settings", "Logout");
        ArrayAdapter adapter=new ArrayAdapter(this, R.layout.dropdown_layout, options);
        adapter.setDropDownViewResource(R.layout.dropdown_layout);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (options.get(i).equals("Logout")){
                    logout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void setTitle(){
        HashMap<String, String> titleDict=new HashMap<String, String>(){{
            put("Admin", "User Complaints");
            put("Cook", "Messages");
            put("Client", "Messages");
        }};
        titleText.setText(titleDict.get(userRole));

    }
    private void logout(){
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
                Intent intent=new Intent(getApplicationContext(), InboxDescriptionActivity.class);
                intent.putExtra("subject", items.get(i).getSubject());
                intent.putExtra("description", items.get(i).getBodyText());
                startActivity(intent);
            }
        });
    }
}