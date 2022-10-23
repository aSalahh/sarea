package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;
import static com.qsilver.sarea.helper.SharedPreferencesManger.SaveData;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qsilver.sarea.Adapter.usersAdapter;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.Travel;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.view.authcycle.SignUpStudentActivity;
import com.qsilver.sarea.view.maps.MapsActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TravelsDetailsActivity extends AppCompatActivity {
    Travel clickedTravel;
    User currentStudent;
    ImageView driverImage;
    TextView driverName;
    TextView driverAddress;
    TextView driverPhone;
    TextView travelFrom;
    TextView travelTo;
    TextView detailsType;
    TextView detailsDays;
    TextView noStudentInThisTravel;
    TextView TravelTime;
    //////////////////////////
    TextView civilnumber;
    TextView level;
    TextView username;
    ImageView currentUserImg;
    RecyclerView travelStudentsRecycler;
    List<User> travelStudentList;
    ImageButton travelsBtnBack;
    ImageButton openTravelOnMap;
    CardView join;
    CardView desJoin;
    LinearLayout linearLayout;
    usersAdapter travelStudentsAdapter;
    //////////////////////////
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_travels);
        clickedTravel = (Travel) getIntent().getSerializableExtra("CLICK_TRAVEL");
        currentStudent = (User) getIntent().getSerializableExtra("CURRENT_STUDENT");
        driverImage = findViewById(R.id.driver_profile_img);
        driverName = findViewById(R.id.travel_driver_name);
        driverAddress = findViewById(R.id.travel_driver_address);
        openTravelOnMap = findViewById(R.id.open_travel_on_map);
        travelsBtnBack = findViewById(R.id.travels_btn_back);
        driverPhone = findViewById(R.id.travel_driver_phone);
        travelFrom = findViewById(R.id.travel_from_set);
        travelTo = findViewById(R.id.travel_to_set);
        TravelTime = findViewById(R.id.travel_time_set);
        detailsType = findViewById(R.id.travel_details_type);
        detailsDays = findViewById(R.id.travel_details_days);
        join = findViewById(R.id.join);
        desJoin = findViewById(R.id.des_join);
        linearLayout = findViewById(R.id.student_options);
        travelStudentsRecycler = findViewById(R.id.recyclerView_travel_students);
        travelStudentsRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        travelStudentsRecycler.setLayoutManager(linearLayoutManager);
        travelStudentList = new ArrayList<User>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (clickedTravel!=null){
            getUserImage(clickedTravel.getDriver().getImageId());
            driverName.setText(clickedTravel.getDriver().getName());
            driverAddress.setText(clickedTravel.getDriver().getAddress());
            detailsType.setText(clickedTravel.getType());
            detailsDays.setText(clickedTravel.getTravelDays());
            driverPhone.setText(clickedTravel.getDriver().getCivilNumber());
            travelFrom.setText(clickedTravel.getTravelStartLocation().getAddress());
            travelTo.setText(clickedTravel.getTravelEndLocation().getAddress());
            TravelTime.setText(clickedTravel.getTime());
            getAvailableStudentInThisTravel(clickedTravel);

        }else {
            Toast.makeText(TravelsDetailsActivity.this, "من فضلك تأكد من الإتصال بالإنترنت", Toast.LENGTH_LONG).show();

        }



        if (LoadData(TravelsDetailsActivity.this, "USER_TYPE") != null &&
                LoadData(TravelsDetailsActivity.this, "USER_TYPE").equals("student")) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }

        travelsBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        openTravelOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TravelsDetailsActivity.this, MapsActivity.class);
                intent.putExtra("MAP_CLICK_DRIVER", clickedTravel);
                intent.putExtra("MAP_CURRENT_STUDENT", currentStudent);
                SaveData(TravelsDetailsActivity.this, "CLICKED_TRAVEL_TYPE", clickedTravel.getType());

                startActivity(intent);

            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentStudentToTravel(currentStudent);

            }
        });
        desJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCurrentStudentFromTravel(currentStudent);

            }
        });

    }

    private void removeCurrentStudentFromTravel(User student) {

        reference = FirebaseDatabase.getInstance().getReference("TravelStudents").child(student.getId());

        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(TravelsDetailsActivity.this, "تم", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void addCurrentStudentToTravel(User student) {
        User currentStudent = new User(student.getEmail(), student.getCivilNumber(), student.getFatherPhone(),
                student.getMatherPhone(), student.getLevel(), student.getName(), student.getAddress(), student.getId(), student.getType(),
                student.getSchoolId(), student.getImageId(), clickedTravel.getTravelId(), student.getUserLocation(), false, clickedTravel.getType());
        reference = FirebaseDatabase.getInstance().getReference("TravelStudents").child(student.getId());
        reference.setValue(currentStudent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(TravelsDetailsActivity.this, "تم", Toast.LENGTH_LONG).show();

                }
            }


        });
        getAvailableStudentInThisTravel(clickedTravel);

    }

    private void getUserImage(String ImageId) {
        StorageReference riversRef = storageReference.child("images/" + ImageId + ".jpg");

        try {
            File localFile = File.createTempFile("tempfile", ".jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            driverImage.setImageBitmap(bitmap);
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


    private void getAvailableStudentInThisTravel(Travel travel) {
        //currentSchoolId=LoadData(MainActivity.this,"CurrentSchoolId");

        reference = FirebaseDatabase.getInstance().getReference("TravelStudents");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                travelStudentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User students = snapshot.getValue(User.class);
                    String StudentTravelId = students.getTravelId();
                    if (StudentTravelId != null && StudentTravelId.equals(travel.getTravelId()) &&
                            travel.getType().equals(students.getTravelType())) {
                        travelStudentList.add(students);
                    }
                }
                travelStudentsAdapter = new usersAdapter(TravelsDetailsActivity.this, LoadData(TravelsDetailsActivity.this, "USER_TYPE"));
                travelStudentsAdapter.setItems(travelStudentList);
                travelStudentsRecycler.setAdapter(travelStudentsAdapter);
                travelStudentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}