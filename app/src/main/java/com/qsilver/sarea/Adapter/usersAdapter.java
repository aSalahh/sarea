package com.qsilver.sarea.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.qsilver.sarea.R;
import com.qsilver.sarea.model.User;
import com.qsilver.sarea.view.UserDataActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class usersAdapter extends RecyclerView.Adapter<usersAdapter.viewHolder>{
    private Context Context;
    private List<User> mUser;
    private String whichActivity;
    private  FirebaseUser fuser;
    private String current_user_id;
    private String current_user_type;
    private DatabaseReference reference;
    private final static String STUDENT_ACTIVITY="student";
    private final static String G1="g1";
    private final static String G2="g2";
    private final static String G3="g3";
    private final static String ADMIN="admin";

    private FirebaseStorage storage;
    private StorageReference storageReference;
    public usersAdapter(Context mContextm,String currentUserType){
        this.Context=mContextm;
        this.current_user_type=currentUserType;

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(Context).inflate(R.layout.user_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = fuser.getUid();
         User user = mUser.get(position);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        holder.userName.setText(user.getName());
        holder.userAddress.setText(user.getAddress());
        if (user.isInBus()){
            holder.inBus.setImageDrawable(ContextCompat.getDrawable(holder.inBus.getContext(),R.drawable.in_bus));

        }else {
            holder.inBus.setImageDrawable(ContextCompat.getDrawable(holder.inBus.getContext(),R.drawable.out_bus));

        }
        holder.inBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.isInBus()){
                    if (!current_user_type.equals("school")){
                        holder.inBus.setImageDrawable(ContextCompat.getDrawable(holder.inBus.getContext(),R.drawable.out_bus));
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TravelStudents")
                                .child(user.getId()).child("inBus");
                        reference.setValue(false);
                    }



                }else {
                    if (!current_user_type.equals("school")){
                        holder.inBus.setImageDrawable(ContextCompat.getDrawable(holder.inBus.getContext(),R.drawable.in_bus));
                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TravelStudents")
                                    .child(user.getId()).child("inBus");
                            reference.setValue(true);


                    }

                }
            }
        });
        StorageReference riversRef = storageReference.child("images/"+user.getImageId()+".jpg");

        try {
            File localFile =File.createTempFile("tempfile",".jpg");
            riversRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            holder.Image.setImageBitmap(bitmap);
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.progressBar.setVisibility(View.GONE);

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent intent=new Intent(Context, UserDataActivity.class);
                        intent.putExtra("CLICK_USER",user);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Context.startActivity(intent);

            }
        });

    }
    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<User> users) {
        this.mUser = users;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView userName,userAddress;
        private ImageView Image;
        private ProgressBar progressBar;
   ImageView inBus;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.current_user_name);
            userAddress=itemView.findViewById(R.id.user_address);
            Image=itemView.findViewById(R.id.current_user_profile_img);
            progressBar=itemView.findViewById(R.id.student_progressbar);
            inBus=itemView.findViewById(R.id.ic_inbus);
        }
    }




}
