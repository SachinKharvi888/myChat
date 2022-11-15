package com.example.mychat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.MessageActivity;
import com.example.mychat.R;
import com.example.mychat.model.Chat;
import com.example.mychat.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FriendAdapter  extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{

    private Context mcontext;
    private List<Users> mUser;
    private  boolean ischat;
    private  boolean online;
    private boolean frd;

    private List<Chat> mChat;

    private Context mcontext1;
    private List<Users> mUser1;
    private  boolean ischat1;

    String theLastmsg;
    String newchats;

    DatabaseReference reference,newchatref;
    FirebaseAuth auth;

    FirebaseUser fuser;

    public  FriendAdapter(Context mcontext, List<Users> mUser, boolean ischat){
        this.mcontext=mcontext;
        this.mUser=mUser;
        this.ischat=ischat;


    }
    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.frditem,parent,false);
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();
        Users m = mUser.get(position);
        holder.username.setText(m.getUsername());
        if (m.getImageURL().equals("default")) {
            // holder.profile_image.setImageResource(R.drawable.profileus);
        } else {
            Glide.with(mcontext).load(m.getImageURL()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, MessageActivity.class);
                intent.putExtra("userid",m.getId());
                intent.putExtra("token",m.getToken());

                mcontext.startActivity(intent);
            }
        });

        if (!ischat){
            holder.last_seen.setVisibility(View.VISIBLE);

            lastMsg(m.getId(),holder.last_seen,holder.newchat);

        }else {

            holder.last_seen.setVisibility(View.GONE);
        }

    }

    //check last msg
    private  void  lastMsg(String userid, TextView last_msg, TextView newchat){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();
        //date.setTextColor(mcontext.getColor(R.color.red));
        theLastmsg="default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Chat chat  =snapshot1.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(userid)||
                            chat.getReceiver().equals(userid)&&chat.getSender().equals(firebaseUser.getUid())){
                        theLastmsg= chat.getMessage();
                        last_msg.setText(chat.getMessage());

                        if (chat.isIsseen()){
                            newchat.setText("");

                        }else{
                            if (chat.getReceiver().equals(firebaseUser.getUid())){
                                newchat.setText("New Chat");

                            }else {
                                newchat.setText("");
                            }


                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        if (position == mChat.size()-1){
            if (chat.isIsseen()) {
                holder.text_seen.setText("Seen");
            }else {
                holder.text_seen.setText("Delivered");
            }
        }else {
            holder.text_seen.setVisibility(View.GONE);
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
        public TextView last_seen,clrc,newchat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.frdusername11);
            profile_image=itemView.findViewById(R.id.frdprofile_image);
            last_seen=itemView.findViewById(R.id.last_seen);
            newchat=itemView.findViewById(R.id.newchattxt);
        }
    }
}
