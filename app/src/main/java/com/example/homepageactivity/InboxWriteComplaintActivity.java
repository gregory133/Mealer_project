package com.example.homepageactivity;

import static android.content.ContentValues.TAG;
import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.ComplaintMessage;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.Message;
import com.example.homepageactivity.domain.Validator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InboxWriteComplaintActivity extends AppCompatActivity {
    private static final String adminUID = "QmVTPXy0g5RQNUVX5cFfHI7TJoE3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_write_complaint);

        setThemeColors();
        setupTarget();
    }

    private void setThemeColors(){
        Class hold = currentAccount.getClass();
        if (hold == Cook.class){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        } else {
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }

    private void setupTarget() {
        Bundle extras = getIntent().getExtras();
        if (extras.getString("targetEmail") != null) {
            ((TextView) findViewById(R.id.targetEmailText)).setText(extras.getString("targetEmail"));
        }
    }

    public void onClickSendComplaintButton(View view) {
        String subject = (((EditText) findViewById(R.id.subjectEdit)).getText()).toString();

        if (!validateSubject(subject)) {
            return;
        }

        String senderUID = firebaseAuth.getCurrentUser().getUid();
        String senderEmail = firebaseAuth.getCurrentUser().getEmail();
        String bodyText = ((EditText) findViewById(R.id.messageBodyEdit)).getText().toString();
        Bundle extras = getIntent().getExtras();
        String cookUID = extras.getString("targetUID");
        String cookEmail = extras.getString("targetEmail");

        ComplaintMessage newComplaint = new ComplaintMessage(senderUID, senderEmail, adminUID, subject, bodyText, cookUID, cookEmail);

        firestoreDB.collection("messages").add(newComplaint)
                .addOnSuccessListener(documentReference -> {
                    finish();
                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_SHORT);
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Problem Sending Message", Toast.LENGTH_LONG));
    }

    public void onCLickDeleteComplaint(View view) {
        finish();
    }

    private boolean validateSubject(String subject) {
        Validator val = new Validator();

        if (!val.isPhrase(subject)) {
            Toast.makeText(this, "The Subject May Only Contain Alphanumeric Characters, Apostrophes, and Spaces", Toast.LENGTH_LONG).show();
            return false;
        }

        Log.d("TAG", "success");
        return true;
    }
}