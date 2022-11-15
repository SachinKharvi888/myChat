package com.example.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.R;
import com.example.mychat.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Useradapter extends RecyclerView.Adapter<Useradapter.ViewHolder>{

    private Context mcontext;
    private List<Users> mUser;
    private  boolean ischat;
    private  boolean online;
    private boolean frd;

    private Context mcontext1;
    private List<Users> mUser1;
    private  boolean ischat1;

    DatabaseReference reference,newchatref;
    FirebaseAuth auth;

    FirebaseUser fuser;

    public  Useradapter(Context mcontext, List<Users> mUser, boolean ischat){
        this.mcontext=mcontext;
        this.mUser=mUser;
        this.ischat=ischat;

    }

    @NonNull
    @Override
    public Useradapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.users,parent,false);
        return new Useradapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Useradapter.ViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();
        Users m = mUser.get(position);
        holder.username.setText(m.getUsername());
        if (m.getImageURL().equals("default")) {
           // holder.profile_image.setImageResource(R.drawable.profileus);
        } else {
            Glide.with(mcontext).load(m.getImageURL()).into(holder.profile_image);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request").child(m.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("users").child(fuser.getUid()).exists()){
                    holder.frdreqtxt.setText("Request");
                }else {
                    holder.frdreqtxt.setText("Add Friend");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.frdrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                frd=true;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Request");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (frd == true) {

                            if (snapshot.child(m.getId()).child("users").hasChild(fuser.getUid())) {
                                FirebaseDatabase.getInstance().getReference("Request").child(m.getId())
                                        .child("users").child(fuser.getUid()).removeValue();
                                holder.frdreqtxt.setText("Add friend");

                                frd=false;
                            } else {
                                FirebaseDatabase.getInstance().getReference("Request").child(m.getId())
                                        .child("users").child(fuser.getUid()).setValue(true);
                                holder.frdreqtxt.setText("Request");


                                frd=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        holder.frdrequestacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference("Friends").child(fuser.getUid())
                                .child("users").child(m.getId()).setValue(true);
                        FirebaseDatabase.getInstance().getReference("Friends").child(m.getId())
                                .child("users").child(fuser.getUid()).setValue(true);

                        FirebaseDatabase.getInstance().getReference("Request").child(fuser.getUid())
                                .child("users").child(m.getId()).removeValue();
                        Toast.makeText(mcontext, m.getUsername(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        holder.frdrequestcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("Request").child(fuser.getUid())
                        .child("users").child(m.getId()).removeValue();
            }
        });
        if (ischat){
            holder.frdrequest.setVisibility(View.VISIBLE);
            holder.frdrequestcan.setVisibility(View.GONE);
            holder.frdrequestacc.setVisibility(View.GONE);

            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Friends").child(fuser.getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("users").child(m.getId()).exists()){
                        holder.frdrequest.setVisibility(View.GONE);

                    }else {
                        holder.frdrequest.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            holder.frdrequest.setVisibility(View.GONE);





        }

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username,frdreqtxt;
        public ImageView profile_image;
        public ImageView imag_on;
        public ImageView imag_off;
        public TextView last_seen,clrc;
        ImageButton RecentChtbtn;
        CardView frdrequest,frdrequestcan,frdrequestacc;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            frdreqtxt=itemView.findViewById(R.id.frdrequesttxt);

            username=itemView.findViewById(R.id.username11);
            profile_image=itemView.findViewById(R.id.profile_image);
            frdrequest=itemView.findViewById(R.id.frdrequest);
           // frdrequest.setVisibility(View.GONE);

            frdrequestacc=itemView.findViewById(R.id.frdrequestacc);
            frdrequestcan=itemView.findViewById(R.id.frdrequestcan);


            // imag_on=itemView.findViewById(R.id.img_on);
           // imag_off=itemView.findViewById(R.id.img_off);
            //last_seen=itemView.findViewById(R.id.last_seen);
            //last_seen.setTextColor(R.color.purple_500);
           // clrc=itemView.findViewById(R.id.clrc);
           // RecentChtbtn=itemView.findViewById(R.id.recentChatbtn);
            //clrc.setTextColor(R.color.purple_500);
        }
    }
}
