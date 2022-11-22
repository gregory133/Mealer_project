package com.example.homepageactivity;

import static com.example.homepageactivity.MainActivity.currentAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.homepageactivity.domain.Admin;
import com.example.homepageactivity.domain.Cook;
import com.example.homepageactivity.domain.StyleApplyer;

public class InboxMessageActivity extends AppCompatActivity {

    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_message);

        background=findViewById(R.id.background);
        setThemeColors();
        collapseAdminButtons();
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