package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;
import static com.qsilver.sarea.helper.SharedPreferencesManger.SaveData;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.view.authcycle.SinUpActivity;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class UserDataActivity extends AppCompatActivity {

    ImageView userImage;
    ImageView btn_back;
    ImageView add_driver;
    TextView email, name, fatherNumber, matherNumber, address, schoolId, civilNumber, level;
    User clickedUser;
    double lat,lon;
    String myUserType;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        userImage = findViewById(R.id.info_user_data_logo);
        btn_back = findViewById(R.id.user_data_back_btn);
        add_driver = findViewById(R.id.user_data_add_driver);
        email = findViewById(R.id.user_data_email);
        name = findViewById(R.id.user_data_name);
        fatherNumber = findViewById(R.id.user_data_father_phone);
        matherNumber = findViewById(R.id.user_data_mather_phone);
        address = findViewById(R.id.user_data_address);
        schoolId = findViewById(R.id.user_data_school_id);
        civilNumber = findViewById(R.id.user_data_civil_number);
        level = findViewById(R.id.user_data_level);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // To retrieve object in second Activity
        clickedUser = (User) getIntent().getSerializableExtra("CLICK_USER");
        if (clickedUser != null && clickedUser.getType().equals("student")) {
            fatherNumber.setVisibility(View.VISIBLE);
            matherNumber.setVisibility(View.VISIBLE);
            level.setVisibility(View.VISIBLE);
            add_driver.setVisibility(View.GONE);

        }
        myUserType = LoadData(UserDataActivity.this, "USER_TYPE");

        if (myUserType != null && !myUserType.equals("school")) {
            add_driver.setVisibility(View.GONE);

        }

        if (clickedUser != null) {
            getUserImage();
            email.setText(clickedUser.getEmail());
            name.setText(clickedUser.getName());
            address.setText(clickedUser.getAddress());
            schoolId.setText(clickedUser.getSchoolId());
            civilNumber.setText(clickedUser.getCivilNumber());
            fatherNumber.setText(clickedUser.getFatherPhone());
            matherNumber.setText(clickedUser.getMatherPhone());
            level.setText(clickedUser.getLevel());
            lat=Double.parseDouble(clickedUser.getUserLocation().getLatitude());
            lon=Double.parseDouble(clickedUser.getUserLocation().getLongitude());
        }
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, lon);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        add_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData(UserDataActivity.this,"CHOSEN_DRIVER",clickedUser);
                Intent i = new Intent(UserDataActivity.this, TravelSettingActivity.class);
                i.putExtra("CHOSEN_DRIVER_INTENT", clickedUser);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                Toast.makeText(UserDataActivity.this, " تم إختيار السائق  ", Toast.LENGTH_LONG).show();

            }
        });


    }


    private void getUserImage() {
        StorageReference riversRef = storageReference.child("images/" + clickedUser.getImageId() + ".jpg");

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            userImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}