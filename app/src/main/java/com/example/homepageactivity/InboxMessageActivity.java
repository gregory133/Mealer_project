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
        setContentView(R.layout.inbox_description);

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
}