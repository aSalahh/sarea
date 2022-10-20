package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.HelperMethod.isNetworkAvailable;
import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qsilver.sarea.helper.HelperMethod;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.User;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {
    final static String TAG = "myTag";
    ImageView profileImage;
    ImageView profile_back_btn;
    ImageView btnSave;
    TextInputLayout email, name, civil_number, fatherNumber, matherNumber, userAddress, level, schoolIdTi;
    String myUserType = "";
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference myRef;

    User currentUser;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        email = findViewById(R.id.profile_out_et_email);
        name = findViewById(R.id.profile_out_et_name);
        profileImage = findViewById(R.id.info_profile_logo);
        civil_number = findViewById(R.id.profile_out_et_civil_number);
        fatherNumber = findViewById(R.id.profile_out_et_father_phone);
        matherNumber = findViewById(R.id.profile_out_et_mather_phone);
        userAddress = findViewById(R.id.profile_out_et_adress);
        level = findViewById(R.id.profile_out_et_level);
        btnSave = findViewById(R.id.btn_save_profile);
        profile_back_btn = findViewById(R.id.profile_back_btn);
        schoolIdTi = findViewById(R.id.profile_school_id_out);
        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference();
        progressBar = findViewById(R.id.profile_progressbar);
        database = FirebaseDatabase.getInstance();
        myUserType = LoadData(ProfileActivity.this, "USER_TYPE");
        if (myUserType != null && !myUserType.equals("student")) {
            fatherNumber.setVisibility(View.GONE);
            matherNumber.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
        }
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        loadData();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();

            }
        });
        profile_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    private void checkValidation() {
        String getEmail = email.getEditText().getText().toString();
        String getName = name.getEditText().getText().toString();
        String getAddress = userAddress.getEditText().getText().toString();
        String getCivilNumber = civil_number.getEditText().getText().toString();
        String getMatherPhone = matherNumber.getEditText().getText().toString();
        String getFatherPhone = fatherNumber.getEditText().getText().toString();
        String getLevel = level.getEditText().getText().toString();
        String getSchoolId = schoolIdTi.getEditText().getText().toString();

        Pattern p = Pattern.compile(HelperMethod.EMAIL_PATTERN);
        Matcher m = p.matcher(getEmail);

        if (getEmail.equals("") || getEmail.length() == 0 ||
                getName.equals("") || getName.length() == 0 ||
                getAddress.equals("") || getAddress.length() == 0 ||
                getCivilNumber.equals("") || getCivilNumber.length() == 0 ||
                getMatherPhone.equals("") || getMatherPhone.length() == 0 ||
                getSchoolId.equals("") || getSchoolId.length() == 0 ||
                getFatherPhone.equals("") || getFatherPhone.length() == 0 ||
                getLevel.equals("") || getLevel.length() == 0) {
            Toast.makeText(ProfileActivity.this, "من فضلك إملئ جميع الحقول", Toast.LENGTH_LONG).show();
        } else if (!m.matches()) {
            Toast.makeText(ProfileActivity.this, "من فضلك أدخل بريد إلكترونى صحيح", Toast.LENGTH_LONG).show();

        } else if (!isNetworkAvailable(ProfileActivity.this)) {
            Toast.makeText(ProfileActivity.this, "من فضلك تأكد من الإتصال بالإنترنت", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            saveData(currentUser);


        }

    }


    private void saveData(User user) {
        String getEmail = email.getEditText().getText().toString();
        String getName = name.getEditText().getText().toString();
        String getAddress = userAddress.getEditText().getText().toString();
        String getCivilNumber = civil_number.getEditText().getText().toString();
        String getMatherPhone = matherNumber.getEditText().getText().toString();
        String getFatherPhone = fatherNumber.getEditText().getText().toString();
        String getLevel = level.getEditText().getText().toString();
        String getSchoolId = schoolIdTi.getEditText().getText().toString();

        User localUser = new User(getEmail, getCivilNumber, getFatherPhone, getMatherPhone, getLevel, getName, getAddress, currentUser.getId(), currentUser.getType(), getSchoolId, currentUser.getImageId(), currentUser.getUserLocation(),currentUser.isInBus());
        myRef = database.getReference("Users").child(firebaseUser.getUid());
        myRef.setValue(localUser);
        progressBar.setVisibility(View.GONE);
        Toast.makeText(ProfileActivity.this, "تم تعديل البيانات", Toast.LENGTH_LONG).show();

    }

    private void loadData() {
        progressBar.setVisibility(View.GONE);
        myRef = database.getReference("Users").child(firebaseUser.getUid());
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                email.getEditText().setText(currentUser.getEmail());
                name.getEditText().setText(currentUser.getName());
                civil_number.getEditText().setText(currentUser.getCivilNumber());
                fatherNumber.getEditText().setText(currentUser.getFatherPhone());
                matherNumber.getEditText().setText(currentUser.getMatherPhone());
                userAddress.getEditText().setText(currentUser.getAddress());
                level.getEditText().setText(currentUser.getLevel());
                schoolIdTi.getEditText().setText(currentUser.getSchoolId());
                getUserImage();


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void getUserImage() {
        StorageReference riversRef = storageReference.child("images/" + currentUser.getImageId() + ".jpg");

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);

                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profileImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "  من فضلك تأكد من الإتصال بالإنترنت  ", Toast.LENGTH_LONG).show();

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}