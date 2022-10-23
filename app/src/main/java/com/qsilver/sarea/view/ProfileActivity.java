package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.HelperMethod.isNetworkAvailable;
import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.qsilver.sarea.model.UserLocation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
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
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 40;
    User currentUser;
    public String address;
    public UserLocation userLocation;


    private FirebaseStorage storage;
    private StorageReference storageReference;
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            address = getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            userAddress.getEditText().setText(address);
            userLocation = new UserLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), address);
        }
    };
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                if (myUserType.equals("student")){
                    checkValidationForStudent();
                }
                else {
                    checkValidation();

                }


            }
        });
        profile_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });
        userAddress.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();

            }
        });
    }

    private void checkValidation() {
        String getEmail = email.getEditText().getText().toString();
        String getName = name.getEditText().getText().toString();
        String getAddress = userAddress.getEditText().getText().toString();
        String getCivilNumber = civil_number.getEditText().getText().toString();
        String getSchoolId = schoolIdTi.getEditText().getText().toString();

        Pattern p = Pattern.compile(HelperMethod.EMAIL_PATTERN);
        Matcher m = p.matcher(getEmail);

        if (getEmail.equals("") || getEmail.length() == 0 ||
                getName.equals("") || getName.length() == 0 ||
                getAddress.equals("") || getAddress.length() == 0 ||
                getCivilNumber.equals("") || getCivilNumber.length() == 0 ||
                getSchoolId.equals("") || getSchoolId.length() == 0 ) {
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
    private void checkValidationForStudent() {
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
            saveDataForStudent(currentUser);


        }

    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            address = getAddress(location.getLatitude(), location.getLongitude());
                            userAddress.getEditText().setText(address);
                            userLocation = new UserLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), address);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "من فضلك فعل الوصول لبيانات الموقع", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }



    private void saveDataForStudent(User user) {
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
    private void saveData(User user) {
        String getEmail = email.getEditText().getText().toString();
        String getName = name.getEditText().getText().toString();
        String getAddress = userAddress.getEditText().getText().toString();
        String getCivilNumber = civil_number.getEditText().getText().toString();
        String getSchoolId = schoolIdTi.getEditText().getText().toString();
        User localUser = new User(getEmail, getCivilNumber, getName, getAddress, currentUser.getId(), currentUser.getType(), getSchoolId, currentUser.getImageId(), currentUser.getUserLocation(),currentUser.isInBus());
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

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }



}