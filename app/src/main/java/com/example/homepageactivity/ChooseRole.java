package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseRole extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
    }

    public void onClickButton(View view){
        Button button=(Button) view;
        String tag=(String)button.getTag();

        Intent intent=new Intent(this, MakeUserActivity.class);
        intent.putExtra("TYPE", tag);

        finish();
        startActivity(intent);

    }
}