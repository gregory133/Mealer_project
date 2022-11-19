package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.homepageactivity.domain.StyleApplyer;

public class InboxMessageActivity extends AppCompatActivity {

    private String userRole;
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_message);

        userRole = getIntent().getStringExtra("userRole");
        background=findViewById(R.id.background);
        setThemeColors(userRole);
        collapseAdminButtons();
    }

    private void collapseAdminButtons(){

        if (!(userRole.equals("Admin"))){
            LinearLayout adminRow=findViewById(R.id.row4);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1.0f);
            adminRow.setLayoutParams(param);
        }

    }

    private void setThemeColors(String mode){
        if (mode.equals("Cook")){
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.cook_light));
        }
        else{
            ((ImageView) findViewById(R.id.midground)).setColorFilter(getResources().getColor(R.color.client_light));
        }
    }
}