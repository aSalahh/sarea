package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DriversActivity extends AppCompatActivity {
    String currentSchoolId;
    TextView noDrivers;
    private List<User> mDriverList;
    private com.qsilver.sarea.Adapter.usersAdapter usersAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers);
        noDrivers = findViewById(R.id.no_drivers);
        recyclerView = findViewById(R.id.recyclerView_driver);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mDriverList = new ArrayList<>();
        if (LoadData(DriversActivity.this, "CurrentSchoolId") != null) {
            currentSchoolId = LoadData(DriversActivity.this, "CurrentSchoolId");
        }

        readUsers();

    }

    private void readUsers() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDriverList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String user_getid = user.getId();
                    if (user_getid != null && user.getSchoolId().equals(currentSchoolId)
                            && user.getType().equals("driver")) {
                        mDriverList.add(user);
                    }
                }
                usersAdapter = new usersAdapter(getBaseContext(),LoadData(DriversActivity.this, "USER_TYPE"));
                usersAdapter.setItems(mDriverList);
                usersAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(usersAdapter);
                if (mDriverList.size() == 0) {
                    noDrivers.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}