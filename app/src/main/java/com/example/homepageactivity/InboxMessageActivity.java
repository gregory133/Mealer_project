package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;
import static com.example.homepageactivity.MainActivity.firestoreDB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.StyleApplyer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class InboxMessageActivity extends AppCompatActivity {

    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_message);

        String senderUID = getIntent().getStringExtra("senderUID");
        firestoreDB.collection("users").document(senderUID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("firstName") + " " + document.getString("lastName");
                                fillInMessage(name);
                            } else {
                                senderRetrievalFailure("Sender doesn't exist");
                                return;
                            }
                        } else {
                            senderRetrievalFailure("on complete failed");
                            return;
                        }
                    }
                });

        background=findViewById(R.id.background);
        setThemeColors();
        collapseAdminButtons();
    }

    private void senderRetrievalFailure(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        fillInMessage("ERROR");
    }

    private void fillInMessage(String name) {
        TextView subjectView = findViewById(R.id.subject);

        String subjectText = getIntent().getStringExtra("subjectText");
        String subjectString = (String)subjectView.getText();
        subjectView.setText(subjectString + ": " + subjectText);

        TextView senderView = findViewById(R.id.senderText);
        String senderString = (String)senderView.getText();
        senderView.setText(senderString + ": " + name);

        String descText = getIntent().getStringExtra("descText");
        TextView descView = findViewById(R.id.messageText);
        descView.setText(descText);
    }

    private void collapseAdminButtons(){
        if (!(currentAccount.getClass() == Admin.class)){
            LinearLayout adminRow=findViewById(R.id.row4);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f);
            adminRow.setLayoutParams(param);
        }
    }

    private void setThemeColors(){
        if (currentAccount.getClass() == Cook.class){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        }
        else{
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }


}