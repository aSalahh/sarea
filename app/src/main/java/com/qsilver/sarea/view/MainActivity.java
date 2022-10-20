package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;
import static com.qsilver.sarea.helper.SharedPreferencesManger.SaveData;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.qsilver.sarea.Adapter.TravelAdapter;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.Travel;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.view.splashcycle.Splash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ImageButton logOut;
    ProgressBar home_progressbar;
    ImageView currentUserProfile;
    TextView currentUserName;
    TextView currentUserSchoolId;
    // Write a message to the database
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout linearLayout;
    FirebaseUser firebaseUser;
    String userId;
    String currentSchoolId;
    CardView btn_create_travel;
    CardView btnMyStudents;
    TextView noGoingTravels;
    TextView noComingTravels;
    BottomNavigationView bottomNavigationView;
    private List<Travel> mGoingTravelList;
    private List<Travel> mComingTravelList;
    private TravelAdapter travelGoingAdapter;
    private TravelAdapter travelComingAdapter;
    private RecyclerView goingRecyclerView;
    private RecyclerView comingRecyclerView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private User currentUser;
    private FirebaseAuth mAuth;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        logOut = findViewById(R.id.btn_logout);
        currentUserProfile = findViewById(R.id.current_user_profile_img);
        currentUserName = findViewById(R.id.current_user_name);
        currentUserSchoolId = findViewById(R.id.current_school_id);
        home_progressbar = findViewById(R.id.home_progressbar);
        firebaseUser = mAuth.getCurrentUser();
        btn_create_travel = findViewById(R.id.create_travel);
        btnMyStudents = findViewById(R.id.my_students);
        storage = FirebaseStorage.getInstance();
        currentUser = new User();
        linearLayout = findViewById(R.id.forschool);
        storageReference = storage.getReference();
        noGoingTravels = findViewById(R.id.no_going_travels);
        noComingTravels = findViewById(R.id.no_coming_travels);
        database = FirebaseDatabase.getInstance();
        goingRecyclerView = findViewById(R.id.recyclerView_avilable_going_travels);
        comingRecyclerView = findViewById(R.id.recyclerView_avilable_coming_travels);
        goingRecyclerView.setHasFixedSize(true);
        comingRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        linearLayoutManager2.setStackFromEnd(true);
        goingRecyclerView.setLayoutManager(linearLayoutManager1);
        comingRecyclerView.setLayoutManager(linearLayoutManager2);
        mComingTravelList = new ArrayList<>();
        mGoingTravelList = new ArrayList<>();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        if (LoadData(MainActivity.this, "USER_TYPE") != null &&
                LoadData(MainActivity.this, "USER_TYPE").equals("school")) {

            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(MainActivity.this, "تسجيل الخروج", "هل انت متاكد أنك تود تسجيل الخروج");
            }
        });

        if (mAuth.getCurrentUser() != null) {
            getUserDataFromDataBase();
        }

        btn_create_travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TravelSettingActivity.class);
                startActivity(i);
            }
        });
        btnMyStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SchoolStudentsActivity.class);
                startActivity(i);
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.info:
                        Intent startIntent = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(startIntent);
                        break;
                    case R.id.home:

                        break;
                    case R.id.profile:
                        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });

    }

    private void getAvailableTravels() {
        currentSchoolId = LoadData(MainActivity.this, "CurrentSchoolId");
        final DatabaseReference referenceGoing = FirebaseDatabase.getInstance().getReference("Travel");
        referenceGoing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGoingTravelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Travel travel = snapshot.getValue(Travel.class);
                    String modelSchoolId = travel.getSchoolId();
                    if (modelSchoolId != null && modelSchoolId.equals(currentSchoolId)
                            && travel.getType().equals("ذهاب")){
                        mGoingTravelList.add(travel);
                    }


                }
                travelGoingAdapter = new TravelAdapter(getBaseContext(), currentUser);
                travelGoingAdapter.setItems(mGoingTravelList);
                travelGoingAdapter.notifyDataSetChanged();
                goingRecyclerView.setAdapter(travelGoingAdapter);
                if (mGoingTravelList.size() == 0) {
                    noGoingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                } else {
                    noGoingTravels.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (mGoingTravelList.size() == 0) {
                    noGoingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
                else {
                    noGoingTravels.setVisibility(View.GONE);
                }
            }
        });

        final DatabaseReference referenceComing = FirebaseDatabase.getInstance().getReference("Travel");
        referenceComing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComingTravelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Travel travel = snapshot.getValue(Travel.class);
                    String modelSchoolId = travel.getSchoolId();
                    if (modelSchoolId != null && modelSchoolId.equals(currentSchoolId)
                            && travel.getType().equals("عودة")){
                        mComingTravelList.add(travel);
                    }



                }
                travelComingAdapter = new TravelAdapter(getBaseContext(), currentUser);
                travelComingAdapter.setItems(mComingTravelList);
                travelComingAdapter.notifyDataSetChanged();
                comingRecyclerView.setAdapter(travelComingAdapter);
                if (mGoingTravelList.size() == 0) {
                    noComingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }
                else {
                    noComingTravels.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (mGoingTravelList.size() == 0) {
                    noComingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }  else {
                    noComingTravels.setVisibility(View.GONE);
                }
            }
        });
    }


    private void getUserDataFromDataBase() {
        home_progressbar.setVisibility(View.VISIBLE);
        if (mAuth.getCurrentUser() != null) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myref = database.getReference("Users").child(Objects.requireNonNull(mAuth.getUid()));
            myref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userModel = dataSnapshot.getValue(User.class);
                    assert userModel != null;
                    currentUserName.setText(userModel.getName());
                    currentUserSchoolId.setText(userModel.getSchoolId());
                    SaveData(MainActivity.this, "CurrentSchoolId", userModel.getSchoolId());
                    SaveData(MainActivity.this, "CURRENT_USER_DATA", userModel);
                    currentUser = userModel;
                    getUserImage();
                    if (LoadData(MainActivity.this, "USER_TYPE").equals("driver")) {
                        getAvailableTravelsForDriver();

                    } else {
                        getAvailableTravels();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void getAvailableTravelsForDriver() {
        currentSchoolId = LoadData(MainActivity.this, "CurrentSchoolId");

        final DatabaseReference referenceGoing = FirebaseDatabase.getInstance().getReference("Travel");
        referenceGoing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGoingTravelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Travel travel = snapshot.getValue(Travel.class);
                    String modelSchoolId = travel.getSchoolId();
                    if (modelSchoolId != null && modelSchoolId.equals(currentSchoolId) &&
                            travel.getDriver().getId().equals(currentUser.getId())
                            && travel.getType().equals("ذهاب")){
                        mGoingTravelList.add(travel);
                    }


                }
                travelGoingAdapter = new TravelAdapter(getBaseContext(), currentUser);
                travelGoingAdapter.setItems(mGoingTravelList);
                travelGoingAdapter.notifyDataSetChanged();
                goingRecyclerView.setAdapter(travelGoingAdapter);
                if (mGoingTravelList.size() == 0) {
                    noGoingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                } else {
                    noGoingTravels.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (mGoingTravelList.size() == 0) {
                    noGoingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                } else {
                    noGoingTravels.setVisibility(View.GONE);
                }
            }
        });


        final DatabaseReference referenceComing = FirebaseDatabase.getInstance().getReference("Travel");
        referenceComing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComingTravelList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Travel travel = snapshot.getValue(Travel.class);
                    String modelSchoolId = travel.getSchoolId();
                    if (modelSchoolId != null && modelSchoolId.equals(currentSchoolId) &&
                            travel.getDriver().getId().equals(currentUser.getId())&&
                    travel.getType().equals("عودة")){
                        mComingTravelList.add(travel);
                    }


                }
                travelComingAdapter = new TravelAdapter(getBaseContext(), currentUser);
                travelComingAdapter.setItems(mComingTravelList);
                travelComingAdapter.notifyDataSetChanged();
                comingRecyclerView.setAdapter(travelComingAdapter);
                if (mComingTravelList.size() == 0) {
                    noComingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                }else {
                    noComingTravels.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (mComingTravelList.size() == 0) {
                    noComingTravels.setVisibility(View.VISIBLE);
                    // recyclerView.setVisibility(View.GONE);
                } else {
                    noComingTravels.setVisibility(View.GONE);
                }
            }
        });
    }
    //TODO only Show my travels and create page for all travels
    private void getAvailableTravelsForStudent() {
//        currentSchoolId = LoadData(MainActivity.this, "CurrentSchoolId");
//
//        final DatabaseReference referenceGoing = FirebaseDatabase.getInstance().getReference("Travel").child("going");
//        referenceGoing.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mGoingTravelList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Travel travel = snapshot.getValue(Travel.class);
//                    String modelSchoolId = travel.getSchoolId();
//
//                    final DatabaseReference referenceStudentGoing = FirebaseDatabase.getInstance().getReference("TravelStudents").child("going");
//                     referenceGoing.addValueEventListener(new ValueEventListener() {
//                         @Override
//                         public void onDataChange(@NonNull DataSnapshot snapshot) {
//                             User currentStudent = snapshot.getValue(User.class);
//                             String travelId = currentStudent.getTravelId();
//                             if (modelSchoolId != null && modelSchoolId.equals(currentSchoolId)){
//
//                             }
//                         }
//
//                         @Override
//                         public void onCancelled(@NonNull DatabaseError error) {
//
//                         }
//                     })
//                    mGoingTravelList.add(travel);
//
//                }
//                travelGoingAdapter = new TravelAdapter(getBaseContext(), currentUser);
//                travelGoingAdapter.setItems(mGoingTravelList);
//                travelGoingAdapter.notifyDataSetChanged();
//                goingRecyclerView.setAdapter(travelGoingAdapter);
//                if (mGoingTravelList.size() == 0) {
//                    noGoingTravels.setVisibility(View.VISIBLE);
//                    // recyclerView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                if (mGoingTravelList.size() == 0) {
//                    noGoingTravels.setVisibility(View.VISIBLE);
//                    // recyclerView.setVisibility(View.GONE);
//                }
//            }
//        });
//
//
//        final DatabaseReference referenceComing = FirebaseDatabase.getInstance().getReference("Travel").child("coming");
//        referenceComing.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mComingTravelList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Travel travel = snapshot.getValue(Travel.class);
//                    String modelSchoolId = travel.getSchoolId();
//                    if (modelSchoolId != null && modelSchoolId.equals(currentSchoolId) &&
//                            travel.getDriver().getId().equals(currentUser.getId()))
//                        mComingTravelList.add(travel);
//
//                }
//                travelComingAdapter = new TravelAdapter(getBaseContext(), currentUser);
//                travelComingAdapter.setItems(mComingTravelList);
//                travelComingAdapter.notifyDataSetChanged();
//                comingRecyclerView.setAdapter(travelComingAdapter);
//                if (mComingTravelList.size() == 0) {
//                    noComingTravels.setVisibility(View.VISIBLE);
//                    // recyclerView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                if (mComingTravelList.size() == 0) {
//                    noComingTravels.setVisibility(View.VISIBLE);
//                    // recyclerView.setVisibility(View.GONE);
//                }
//            }
//        });
    }

    private void getUserImage() {
        StorageReference riversRef = storageReference.child("images/" + currentUser.getImageId() + ".jpg");

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            home_progressbar.setVisibility(View.GONE);

                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            currentUserProfile.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            home_progressbar.setVisibility(View.GONE);

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sendToStart();
    }

    @Override
    public void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        super.onStart();
        if (currentUser == null) {
            sendToStart();

        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, Splash.class);
        startActivity(startIntent);
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
        finish();
    }

    private void showDialog(Activity activity, String title, CharSequence message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });

        builder.setNegativeButton("لا", null);
        builder.show();
    }

}