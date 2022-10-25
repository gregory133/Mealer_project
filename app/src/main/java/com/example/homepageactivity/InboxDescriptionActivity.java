package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class InboxDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_description);
        loadText();
    }

    private void loadText(){
        TextView subjectText=findViewById(R.id.subject);
        TextView descText=findViewById(R.id.description);

        String subject=getIntent().getExtras().get("subject").toString();
        String description=getIntent().getExtras().get("description").toString();

        subjectText.setText(subject);
        descText.setText(description);
    }

    public void onClickActionButton(View view){
        Toast.makeText(this, "not implemented yet", Toast.LENGTH_LONG).show();
    }
}