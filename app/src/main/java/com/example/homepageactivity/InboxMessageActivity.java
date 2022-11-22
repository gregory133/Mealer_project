package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.ComplaintMessage;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InboxMessageActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DocumentSnapshot docRef;
    DatePickerDialog.OnDateSetListener calenderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_message);

        setThemeColors();
        collapseAdminButtons();
        getMessageInfo();
    }

    private void setThemeColors(){
        Class hold = currentAccount.getClass();
        if (hold == Cook.class){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        } else {
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }
    private void collapseAdminButtons(){
        if (currentAccount.getClass() != Admin.class){
            LinearLayout adminRow=findViewById(R.id.row4);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f);
            adminRow.setLayoutParams(param);
        }
    }

    private void getMessageInfo(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            throwToast("Could not load meal");
        }

        Bundle extras=getIntent().getExtras();

        DocumentReference docRef = firestoreDB.collection("messages").document(extras.getString("messageUID"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    InboxMessageActivity.this.docRef = task.getResult();
                    if (InboxMessageActivity.this.docRef.exists()) {
                        SetupMessageInfo();
                    } else {
                        throwToast("Meal doesn't exist");
                        return;
                    }
                } else {
                    throwToast("Meal failed to load");
                    return;
                }
            }
        });
    }
    private void SetupMessageInfo(){
        ((TextView) findViewById(R.id.subject)).setText(docRef.toObject(Message.class).getSubject());
        ((TextView) findViewById(R.id.senderText)).setText(getIntent().getStringExtra("senderName"));
        ((TextView) findViewById(R.id.messageText)).setText(docRef.toObject(Message.class).getBodyText());
    }

    public void onCLickReturnToInbox(View view){
        finish();
    }
    public void onCLickDeleteMessage(View view){
        archiveMessage();
    }
    public void onCLickReplyToMessage(View view){
        throwToast("Not implemented");
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

    private void archiveMessage(){
        Map<String, Boolean> change = new HashMap<>(1);
        change.put("archived", true);
        String msgID = docRef.getId();
        firestoreDB.collection("messages").document(msgID).set(change, SetOptions.merge());
        finish();
    }

    private void throwToast(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }
}