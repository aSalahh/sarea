package com.qsilver.sarea.view.splashcycle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.qsilver.sarea.R;
import com.qsilver.sarea.view.splashcycle.SliderFragment;

public class SplashCycleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_cycle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.splash_cycle_activity_fl_frame, new SliderFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}