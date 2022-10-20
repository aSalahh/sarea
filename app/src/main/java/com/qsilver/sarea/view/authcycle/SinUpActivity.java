package com.qsilver.sarea.view.authcycle;

import static com.qsilver.sarea.helper.HelperMethod.isNetworkAvailable;
import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qsilver.sarea.helper.HelperMethod;
import com.qsilver.sarea.view.MainActivity;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.model.UserLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinUpActivity extends AppCompatActivity {
    final String imageRandomKey = UUID.randomUUID().toString();
    public Uri imageUri;
    public UserLocation userLocation;
    public String address;
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            address = getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            userAddress.getEditText().setText(address);
            userLocation = new UserLocation(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), address);
        }
    };
    ProgressBar progressBar;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    ImageView doSignUpBtn;
    LinearLayout goSignIn;
    String myUserType = "";
    private List<User> mSchools;
    private ImageView userImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private TextInputLayout email, name, civil_number, password, userAddress, schoolIdTi;
    private String userImageId, latitude, longitude, city, country;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    public static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(99999999);
        return String.format("%06d", number);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sin_up);
        userImage = findViewById(R.id.auth_signUp_logo);
        email = findViewById(R.id.signUp_out_et_email);
        name = findViewById(R.id.signUp_out_et_name);
        schoolIdTi = findViewById(R.id.school_id_out);
        civil_number = findViewById(R.id.signUp_out_et_civil_number);
        password = findViewById(R.id.signUp_out_et_password);
        userAddress = findViewById(R.id.signUp_out_et_adress);
        progressBar = findViewById(R.id.progressbar);
        doSignUpBtn = findViewById(R.id.signUp_btn_signUp);
        goSignIn = (LinearLayout) findViewById(R.id.tv_signIn);
        mSchools = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        myUserType = LoadData(SinUpActivity.this, "USER_TYPE");

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               choosePicture();
            }
        });
        userAddress.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();

            }
        });
        doSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
        goSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SinUpActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            userImage.setImageURI(imageUri);
            uploadPicture();

        }
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/**");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }

    private void uploadPicture() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("جارى تحميل الصورة");
        progressDialog.show();
        StorageReference riversRef = storageReference.child("images/" + imageRandomKey + ".jpg");

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "تم إضافة الصورة", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "حدث خطأ ما , حاول لاحقاً", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPercentage = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage(" جارى " + (int) progressPercentage + " %");
                    }
                });
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


    private void checkValidation() {
        String getEmail = email.getEditText().getText().toString();
        String getPassword = password.getEditText().getText().toString();
        String getName = name.getEditText().getText().toString();
        String getAddress = userAddress.getEditText().getText().toString();
        String getCivilNumber = civil_number.getEditText().getText().toString();
        String schoolId = schoolIdTi.getEditText().getText().toString();

        Pattern p = Pattern.compile(HelperMethod.EMAIL_PATTERN);
        Matcher m = p.matcher(getEmail);

        if (getEmail.equals("") || getEmail.length() == 0 ||
                getPassword.equals("") || getPassword.length() == 0 ||
                getName.equals("") || getName.length() == 0 ||
                getAddress.equals("") || getAddress.length() == 0 ||
                getCivilNumber.equals("") || getCivilNumber.length() == 0) {
            Toast.makeText(SinUpActivity.this, "من فضلك إملئ جميع الحقول", Toast.LENGTH_LONG).show();
        } else if (!m.matches()) {
            Toast.makeText(SinUpActivity.this, "من فضلك أدخل بريد إلكترونى صحيح", Toast.LENGTH_LONG).show();

        } else if (!isNetworkAvailable(SinUpActivity.this)) {
            Toast.makeText(SinUpActivity.this, "من فضلك تأكد من الإتصال بالإنترنت", Toast.LENGTH_LONG).show();

        }
//        else if (myUserType != null && !myUserType.equals("school") && !schoolIdAlreadyExist(schoolId)) {
//            Toast.makeText(SinUpActivity.this, "من فضلك تأكد من الرقم التعريفى ", Toast.LENGTH_LONG).show();
//
//        }
//        else if ( schoolIdTi.getVisibility()==View.VISIBLE
//        &&schoolId.length()==0){
//            Toast.makeText(SinUpActivity.this, "من فضلك إملئ جميع الحقول", Toast.LENGTH_LONG).show();
//
//        }

        else {
            doSignUp(getEmail, getPassword, getName, getAddress, getCivilNumber, schoolId);
            Toast.makeText(SinUpActivity.this, "من فضلك إنتظر للتأكد من صحة البيانات", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);


        }

    }


    private void doSignUp(String email, String password, String name, String address,
                          String civilNumber, String schoolId) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userId = firebaseUser.getUid();
                        if (task.isSuccessful()) {
                            // save in users root
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            User user = new User(email, civilNumber, name, address, userId, myUserType,
                                    schoolId, imageRandomKey, userLocation,false);
                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Intent goToMainActivity = new Intent(SinUpActivity.this, MainActivity.class);
                                        goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(goToMainActivity);
                                        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
                                        Toast.makeText(SinUpActivity.this, "تم تسجيل الدخول", Toast.LENGTH_LONG).show();
                                    }


                                }

                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SinUpActivity.this, "حاول لاحقاً", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }


    public boolean schoolIdAlreadyExist(String schoolId) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("student");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSchools.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String school_id = user.getSchoolId();
                    if (school_id != null && !school_id.equals(schoolId)) {
                        mSchools.add(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return mSchools.size() > 0;


    }
}