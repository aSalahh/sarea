package com.qsilver.sarea.view.splashcycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.qsilver.sarea.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler h = new Handler();

        h.postDelayed(new Runnable(){

            @Override
            public void run(){

                Splash.this.startActivity(new Intent(Splash.this, SplashCycleActivity.class));
                overridePendingTransition(R.anim.left_out, R.anim.right_enter);

                finish();

            }}, 3000);
    }
}