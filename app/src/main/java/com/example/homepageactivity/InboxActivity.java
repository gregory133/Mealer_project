package com.example.homepageactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homepageactivity.domain.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import android.app.Dialog;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity {

    String userRole; //client, admin or cook
    TextView titleText;
    Spinner spinner;
    ListView listView;
    FirebaseFirestore db;
    private static final String TAG = "InboxActivity";

    ArrayList<QueryDocumentSnapshot> items;
    private Dialog currentMessage;
    private QueryDocumentSnapshot docRef;

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
        populateMessages();
    }

    private void populateMessages(){
        System.out.println("1");
        CollectionReference colRef = getCollection("users");
        System.out.println("11");
        DocumentReference docRef = getDocument(colRef, "BShaMMSVKIYn8tDFy5eKokv5ubE3");
        System.out.println("12");
        Object firstName = getField(docRef, "inbox");
        System.out.println("13");

        String name = firstName.toString();
        titleText.setText(name);

//        hookDropDown();
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            db = FirebaseFirestore.getInstance();
//            db.collection("messages")
//                    .whereEqualTo("recipientUID", currentUser.getUid()).whereEqualTo("archived", false)
//                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                        @Override
//                        public void onEvent(@Nullable QuerySnapshot value,
//                                            @Nullable FirebaseFirestoreException e) {
//                            if (e != null) {
//                                Log.w(TAG, "Listen failed.", e);
//                                return;
//                            }
//
//                            items = new ArrayList<>();
//                            for (QueryDocumentSnapshot msg : value) {
//                                items.add(msg);
//                            }
//                            hookList();
//                        }
//                    });
//        } else {
//            Toast.makeText(this, "Error, no user signed in", Toast.LENGTH_LONG).show();
//        }
    }





    /**
     Refs
     Inbox activity - onClickSuspend
     */
    public Object getField(DocumentReference docRef, String fieldName){		//fieldName is a string of the field's name

        System.out.println("14");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                System.out.println("15");
                if (task.isSuccessful()) {
                    System.out.println("16");
                    DocumentSnapshot document = task.getResult();
                    System.out.println("17");
                    if (document.exists()) {
                        System.out.println("18");
                        onSuccessfulUserRetrival(document);
                    }
                    else{
                        System.out.println("19");
                        //no doc error
                    }
                } else {
                    System.out.println("20");
                    //failed error
                }
            }
        });

        System.out.println("21");
        Task<DocumentSnapshot> docSnapshot = docRef.get();
        System.out.println("22");
        //(Source.valueOf(fieldName))
        return docSnapshot;
    }

    private void onSuccessfulUserRetrival(DocumentSnapshot docSnapshot){
        System.out.println("23");
        User thisUser = getUserFromDocumentSnapshot(docSnapshot);
        if(thisUser == null){
            return;
        }
        System.out.println("24");
        titleText.setText(thisUser.getBannedUntil().toString());
        System.out.println("26");
    }

    public CollectionReference getCollection(String collectionName){
        db = FirebaseFirestore.getInstance();		//FirebaseFirestore is the Object of the database
        return db.collection(collectionName);
    }

    public DocumentReference getDocument(CollectionReference colletionRef, String docID){	//docID is the UID or message ID String mesa
        return colletionRef.document(docID);
    }

    /**
     import java.util.Map
     import java.util.HashMap
     */
    public void setField(DocumentReference docRef, String fieldName, Object newValue){
        Map<String, Object> changeField = new HashMap<>(1);
        changeField.put(fieldName, newValue);
        docRef.set(changeField, SetOptions.merge());
    }

    private User getUserFromDocumentSnapshot(DocumentSnapshot docSnapshot) {
        try{
            return docSnapshot.toObject(Client.class);
        }catch (Exception e){
            //notAClient
        }
        try{
            return docSnapshot.toObject(Cook.class);
        }catch (Exception e){
            //notACook
        }
        try{
            return docSnapshot.toObject(Admin.class);
        }catch (Exception e){
            //notAnAdmin
            return null;
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
                docRef = items.get(i);
                showMessage();
            }
        });
    }

    private void showMessage() {
        currentMessage = new Dialog(this);
        currentMessage.setContentView(R.layout.inbox_description);
        Message selectedMessage = docRef.toObject(Message.class);
        TextView subjectText=currentMessage.findViewById(R.id.subject);
        TextView descText=currentMessage.findViewById(R.id.description);
        subjectText.setText(selectedMessage.getSubject());
        descText.setText(selectedMessage.getBodyText());
        currentMessage.show();
    }

    public void onClickSuspend(View view) {
        String cookUID = docRef.toObject(ComplaintMessage.class).getCookUID();
        if (cookUID != null) {
            Map<String, Boolean> change = new HashMap<>(1);
            change.put("banned", true);
            db.collection("users").document(cookUID).set(change, SetOptions.merge());
            onClickDismiss(view);
        } else {
            Toast.makeText(this, "Error, no cook UID found", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickDismiss(View view) {
        Map<String, Boolean> change = new HashMap<>(1);
        change.put("archived", true);
        String msgID = docRef.getId();
        db.collection("messages").document(msgID).set(change, SetOptions.merge());
        currentMessage.dismiss();
    }
}