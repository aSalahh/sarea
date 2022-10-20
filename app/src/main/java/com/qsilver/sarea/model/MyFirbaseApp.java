package com.qsilver.sarea.model;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirbaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
