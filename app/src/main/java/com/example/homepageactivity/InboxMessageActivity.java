package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firebaseAuth;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
    String msgId;
    ComplaintMessage currentMessage;

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
                    0);
            adminRow.setLayoutParams(param);
        }
    }

    private void getMessageInfo(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            throwToast("No user signed in");
            return;
        }

        Bundle extras=getIntent().getExtras();

        DocumentReference docRef = firestoreDB.collection("messages").document(extras.getString("messageUID"));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    if (result.exists()) {
                        currentMessage = result.toObject(ComplaintMessage.class);
                        msgId = result.getId();
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
        ((TextView) findViewById(R.id.mealName)).setText(currentMessage.getSubject());
        ((TextView) findViewById(R.id.cuisineType)).setText(currentMessage.getSenderEmail());
        ((TextView) findViewById(R.id.messageText)).setText(currentMessage.getBodyText());
    }

    public void onCLickReturnToInbox(View view){
        finish();
    }
    public void onCLickDeleteMessage(View view){
        archiveMessage();
    }
    public void onCLickReplyToMessage(View view){
        Intent intent=new Intent(this, InboxWriteMessageActivity.class);
        intent.putExtra("senderEmail", currentMessage.getSenderEmail());

        startActivity(intent);
    }

    public void onClickBanCook(View view) {
        String cookUID = currentMessage.getCookUID();
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

        Timestamp timestamp = new Timestamp(new Date(year-1900, month, day));

        String cookUID = currentMessage.getCookUID();
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
        firestoreDB.collection("messages").document(msgId).set(change, SetOptions.merge());

        finish();
    }

    private void throwToast(String failureReason){
        Toast.makeText(this, failureReason, Toast.LENGTH_LONG).show();
    }
}