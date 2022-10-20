package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qsilver.sarea.Adapter.usersAdapter;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.User;

import java.util.ArrayList;
import java.util.List;

public class SchoolStudentsActivity extends AppCompatActivity {
    private String currentSchoolId;
    private TextView noStudents;
    private List<User> mStudentList;
    private com.qsilver.sarea.Adapter.usersAdapter usersAdapter;
    private RecyclerView recyclerView;
    private ImageButton btnBackImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_students);
        noStudents = findViewById(R.id.no_student);
        recyclerView = findViewById(R.id.recyclerView_my_student);
        btnBackImageButton = findViewById(R.id.btn_back_my_students);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mStudentList = new ArrayList<>();
        if (LoadData(SchoolStudentsActivity.this, "CurrentSchoolId") != null) {
            currentSchoolId = LoadData(SchoolStudentsActivity.this, "CurrentSchoolId");
        }
        btnBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        readStudents();

    }

    private void readStudents() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mStudentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String user_getid = user.getId();
                    if (user_getid != null && user.getSchoolId().equals(currentSchoolId)
                            && user.getType().equals("student")) {
                        mStudentList.add(user);
                    }
                }
                usersAdapter = new usersAdapter(getBaseContext(),LoadData(SchoolStudentsActivity.this, "USER_TYPE"));
                usersAdapter.setItems(mStudentList);
                usersAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(usersAdapter);
                if (mStudentList.size() == 0) {
                    noStudents.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}