package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
     private static int SPLASH_SCREEN = 2500;
    //variables
    Animation topAnim , bottomAnim;
    TextView title, slogan;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        Log.d("Scheduling Alarm","at 02:00am");
        AlarmScheduler.scheduleMidnightUpdate(SplashScreen.this);


        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);

        title = findViewById(R.id.title);
        slogan = findViewById(R.id.slogan);
        logo = findViewById(R.id.logo);

        title.setAnimation(bottomAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, UserOption.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}