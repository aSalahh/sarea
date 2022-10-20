package com.qsilver.sarea.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.qsilver.sarea.R;
import com.qsilver.sarea.model.Travel;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.view.TravelsDetailsActivity;

import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.viewHolder> {
    private final static String STUDENT_ACTIVITY = "student";
    private final static String G1 = "g1";
    private final static String G2 = "g2";
    private final static String G3 = "g3";
    private final static String ADMIN = "admin";
    private final Context mContext;
    private  List<Travel> mTravel;
    private String whichActivity;
    private FirebaseUser fuser;
    private String current_user_id;
    private DatabaseReference reference;
    private User currentUser;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    public TravelAdapter(Context mContext, User currentUser) {
        this.mTravel = mTravel;
        this.mContext = mContext;
         this.currentUser=currentUser;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Travel> mTravel) {
        this.mTravel = mTravel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.travel_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
//        fuser = FirebaseAuth.getInstance().getCurrentUser();
//        current_user_id = fuser.getUid();
        Travel travel = mTravel.get(position);

//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
        holder.time.setText(travel.getTime());
        holder.startLocation.setText(travel.getTravelStartLocation().getAddress());
        holder.endLocation.setText(travel.getTravelEndLocation().getAddress());
        holder.days.setText(travel.getTravelDays());
        holder.type.setText(travel.getType());
        if (currentUser.getType().equals("school")){
            holder.removeTravel.setVisibility(View.VISIBLE);
        }else {
            holder.removeTravel.setVisibility(View.GONE);
        }

        holder.removeTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Travel").child(travel.getTravelId());
                mReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "تم", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TravelStudents");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    User students = snapshot.getValue(User.class);
//                    String StudentTravelId=students.getTravelId();
//                    if(StudentTravelId!=null&&StudentTravelId.equals(travel.getTravelId())){
//                      holder.inThisTravel.setVisibility(View.VISIBLE);
//
//                    }
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TravelsDetailsActivity.class);
                intent.putExtra("CLICK_TRAVEL", travel);
                intent.putExtra("CURRENT_STUDENT", currentUser);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mTravel.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView startLocation;
        private final TextView endLocation;
         private ImageView removeTravel;
         TextView days,type;



        public viewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time_set);
            startLocation = itemView.findViewById(R.id.from_set);
            endLocation = itemView.findViewById(R.id.to_set);
            removeTravel = itemView.findViewById(R.id.delete_travel);
            days=itemView.findViewById(R.id.days);
            type=itemView.findViewById(R.id.type);
        }
    }


}
