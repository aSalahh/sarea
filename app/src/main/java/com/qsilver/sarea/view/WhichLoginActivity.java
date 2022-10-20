package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.SaveData;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.qsilver.sarea.R;
import com.qsilver.sarea.view.authcycle.SignUpStudentActivity;
import com.qsilver.sarea.view.authcycle.SinUpActivity;

public class WhichLoginActivity extends AppCompatActivity {
    CardView school, driver, student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_which_login);
        school = (CardView) findViewById(R.id.school);
        driver = (CardView) findViewById(R.id.driver);
        student = (CardView) findViewById(R.id.student);


        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData(WhichLoginActivity.this,"USER_TYPE","school");
                Intent i = new Intent(WhichLoginActivity.this, SinUpActivity.class);
                i.putExtra("USER_TYPE", "school");
                startActivity(i);
            }
        });
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData(WhichLoginActivity.this,"USER_TYPE","driver");
                Intent i = new Intent(WhichLoginActivity.this, SinUpActivity.class);
                i.putExtra("USER_TYPE", "driver");
                startActivity(i);
            }
        });
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData(WhichLoginActivity.this,"USER_TYPE","student");
                Intent i = new Intent(WhichLoginActivity.this, SignUpStudentActivity.class);
                i.putExtra("USER_TYPE", "student");
                startActivity(i);
            }
        });
    }


}