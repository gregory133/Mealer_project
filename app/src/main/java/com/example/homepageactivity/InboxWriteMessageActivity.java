package com.example.homepageactivity;

import static android.content.ContentValues.TAG;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Message;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InboxWriteMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_write_message);

        Bundle extras=getIntent().getExtras();
        if(extras.getString("senderEmail") != null){
            ((TextView) findViewById(R.id.recipientEmailEdit)).setText(extras.getString("senderEmail"));
            ((TextView) findViewById(R.id.subjectEdit)).setText("Re: "+extras.getString("subjectReply"));
        }
    }

    public void onClickSendMessageButton(View view) {
        String recipientEmail = (((EditText) findViewById(R.id.recipientEmailEdit)).getText()).toString();
        String subject = (((EditText) findViewById(R.id.subjectEdit)).getText()).toString();

        if(!validateSubject(subject)){return;}
        validateRecipient(recipientEmail);
    }

    public void onCLickDeleteMessage(View view){
        finish();
    }

    private boolean validateSubject(String subject) {
        Validator val = new Validator();

        if (!val.isAlphanumericPhrase(subject)){
            Toast.makeText(this, "The Subject May Only Contain Alphanumeric Characters, Apostrophes, and Spaces", Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d("TAG", "success");
        return true;
    }

    private void validateRecipient(String recipientEmail){
        firestoreDB.collection("users")
                .whereEqualTo("emailAddress", recipientEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<QueryDocumentSnapshot> docRefs = new ArrayList<>();
                        for (QueryDocumentSnapshot user : value) {
                            docRefs.add(user);
                        }
                        if(docRefs.isEmpty()){
                            Toast.makeText(getApplicationContext(), "Invalid Email Address: "+recipientEmail, Toast.LENGTH_LONG).show();
                            return;
                        }
                        finishSendingMessage(docRefs.get(0));
                    }
                });
    }
    private void finishSendingMessage(QueryDocumentSnapshot docRef){
        String subject = (((EditText) findViewById(R.id.subjectEdit)).getText()).toString();
        String bodyText = ((EditText) findViewById(R.id.messageBodyEdit)).getText().toString();
        String senderUID = firebaseAuth.getCurrentUser().getUid();
        String senderEmail = firebaseAuth.getCurrentUser().getEmail();

        Message newMessage = new Message(senderUID, senderEmail, docRef.getId(), subject, bodyText);

        firestoreDB.collection("messages").add(newMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //onMessageCreationSuccess();
                        finish();
                        Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Problem Sending Message", Toast.LENGTH_LONG);
                    }
                });
    }
}