package com.example.homepageactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private final long frameTimeMilli=20;
    private final long waitMilli=2000;
    private final int deltaAngle=10;


    private ImageView yinYangLoad;
    private boolean allowSpin=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        yinYangLoad=findViewById(R.id.yinyang);


        Timer totalTimer=new Timer();
        Timer frameTimer=new Timer();
        TimerTask totalTimerTask=new TimerTask(){
            @Override
            public void run() {
                allowSpin=false;
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        };
        TimerTask frameTimerTask=new TimerTask(){
            @Override
            public void run() {
                rotateLoadingImage();

            }
        };


        frameTimer.scheduleAtFixedRate(frameTimerTask, 0, frameTimeMilli);
        totalTimer.schedule(totalTimerTask, waitMilli);

    }

    private void rotateLoadingImage(){
        if (allowSpin){
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    yinYangLoad.setRotation(yinYangLoad.getRotation()+deltaAngle);

                }
            });
        }


    }
}