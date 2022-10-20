package com.qsilver.sarea.view;

import static com.qsilver.sarea.helper.SharedPreferencesManger.LoadData;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

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

public class StudentActivity extends AppCompatActivity {
    private List<User> mUser;
    private com.qsilver.sarea.Adapter.usersAdapter usersAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        recyclerView = findViewById(R.id.recyclerView_student);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        mUser = new ArrayList<>();
        readUsers();
    }
    private void readUsers() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("student");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    String user_getid=user.getId();
                    if (user_getid!=null&&!user_getid.equals(firebaseUser.getUid())){
                        mUser.add(user);
                    }
                }
                usersAdapter =new usersAdapter(getBaseContext(),LoadData(StudentActivity.this, "USER_TYPE"));
                usersAdapter.setItems(mUser);
                usersAdapter.notifyDataSetChanged();


                recyclerView.setAdapter(usersAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

}