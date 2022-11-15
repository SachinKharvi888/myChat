package com.example.mychat.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
  public  static  final int MSG_TYPE_LEFT = 0;
  public  static  final int MSG_TYPE_RIGHT = 1;

  String senderRoom;
  String reciverRoom;

  private Context mcontext;
  private List<Chat> mChat;
  private String imagUrl;

  private List<Users> mUser;
  private  boolean ischat;

  FirebaseUser fuser;
  DatabaseReference reference,msgDelete;
  DatabaseReference referenc;
  Chat chat ;

  public MessageAdapter(Context mcontext, List<Chat>mChat, String imagUrl){
    this.imagUrl=imagUrl;
    this.mcontext=mcontext;
    this.mChat=mChat;
  }

  public MessageAdapter(MessageActivity mcontext, String senderRoom, String reciverRoom){
    this.senderRoom=senderRoom;
    this.reciverRoom=reciverRoom;
  }
  public class ViewHolder extends RecyclerView.ViewHolder{
    public TextView show_message,name;
    public ImageView profile_image,likeImageR,likeImageL,likemsgR,likemsgL;
    public  TextView text_seen,tex,timel,timeR;
    ImageView showimage,showimageleft;


    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      show_message=itemView.findViewById(R.id.show_message);
     // profile_image=itemView.findViewById(R.id.profile_images);
      text_seen=itemView.findViewById(R.id.text_seen);
      //likeImageR=itemView.findViewById(R.id.likeimageR);
   //  likeImageL=itemView.findViewById(R.id.likeimageL);
      likemsgR=itemView.findViewById(R.id.likemsgeR);
      likemsgL=itemView.findViewById(R.id.likemsgL);
      tex=itemView.findViewById(R.id.likeimage3);
      showimage=itemView.findViewById(R.id.showimagee);
     showimageleft=itemView.findViewById(R.id.showimageleft);

     timel=itemView.findViewById(R.id.timel);
     timeR=itemView.findViewById(R.id.timeR);

    }
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
    if(viewType==MSG_TYPE_RIGHT){
      View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right,parent,false);
      return new ViewHolder(view);
    }else {
      View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,parent,false);
      return new ViewHolder(view);
    }
  }
  @SuppressLint({"ResourceAsColor", "RecyclerView"})
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    chat = mChat.get(position);
    fuser=FirebaseAuth.getInstance().getCurrentUser();
    String chtlis = mChat.get(position).getMessageId();

    int reactions[] = new int[]{
            R.drawable.close_24,
            R.drawable.ic_fb_angry,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_wow
    };

    ReactionsConfig config = new ReactionsConfigBuilder(mcontext)
            .withReactions(reactions)
            .build();

    ReactionPopup popup = new ReactionPopup(mcontext, config, (pos) -> {
      if(pos < 0)
        return false;

      if(mChat.get(position).getSender().equals(fuser.getUid())){
        //viewHolder.binding.feeling.setImageResource(reactions[pos]);
        holder.likemsgR.setImageResource(reactions[pos]);
        reference=FirebaseDatabase.getInstance().getReference("Chats").child(chtlis);
        HashMap<String, Object> map = new HashMap<>();
        map.put("feeling",pos);
        reference.updateChildren(map);
        holder.likemsgR.setImageResource(reactions[chat.getFeeling()]);

        chat.setFeeling(pos);

      }else {
        holder.likemsgL.setImageResource(reactions[pos]);
        reference=FirebaseDatabase.getInstance().getReference("Chats").child(chtlis);
        HashMap<String, Object> map = new HashMap<>();
        map.put("feeling",pos);
        reference.updateChildren(map);
        holder.likemsgL.setImageResource(reactions[chat.getFeeling()]);
        holder.likemsgL.setVisibility(View.VISIBLE);

        chat.setFeeling(pos);

      }

      return true; // true is closing popup, false is requesting a new selection
    });


    reference=FirebaseDatabase.getInstance().getReference("Chats").child(chtlis);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        int num = Integer.parseInt(snapshot.child("feeling").getValue().toString());
        if(mChat.get(position).getSender().equals(fuser.getUid())){

          holder.likemsgR.setImageResource(reactions[num]);
          if (num==0){
            holder.likemsgR.setVisibility(View.GONE);
          }else {
            holder.likemsgR.setVisibility(View.VISIBLE);

          }

          long time = snapshot.child("time").getValue(Long.class);
          SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
          holder.timeR.setText(dateFormat.format(new Date(time)));
        }else {
          holder.likemsgL.setImageResource(reactions[num]);

          if (num==0){
            holder.likemsgL.setVisibility(View.GONE);

          }else {
            holder.likemsgL.setVisibility(View.VISIBLE);

          }
          long time = snapshot.child("time").getValue(Long.class);
          SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
          holder.timel.setText(dateFormat.format(new Date(time)));
        }

        //holder.likeImageR.setImageResource(reactions[num]);


      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
    // holder.showimage.setVisibility(View.VISIBLE);

    fuser= FirebaseAuth.getInstance().getCurrentUser();
    if(mChat.get(position).getSender().equals(fuser.getUid())){
      if (chat.getMessage().equals("photo")){
        Glide.with(mcontext).load(chat.getImageUrl()).into(holder.showimage);
         holder.show_message.setVisibility(View.GONE);
         holder.showimage.setVisibility(View.VISIBLE);
      }else {
        holder.show_message.setVisibility(View.VISIBLE);
        holder.showimage.setVisibility(View.GONE);

      }
    }else {
      if (chat.getMessage().equals("photo")){
        Glide.with(mcontext).load(chat.getImageUrl()).into(holder.showimageleft);
        holder.show_message.setVisibility(View.GONE);
        holder.showimageleft.setVisibility(View.VISIBLE);

      }else {
        holder.show_message.setVisibility(View.VISIBLE);
        holder.showimageleft.setVisibility(View.GONE);
      }
    }
    holder.show_message.setText(chat.getMessage());

    holder.show_message.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if(mChat.get(position).getSender().equals(fuser.getUid())){

        }else{
          popup.onTouch(v,event);

        }


        return false;
      }
    });



    holder.show_message.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String chtlis = mChat.get(position).getMessageId();
        fuser= FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("Delete");
        builder.setMessage("want to delete message");

        //chat delete
        if(mChat.get(position).getSender().equals(fuser.getUid())){
          builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              FirebaseDatabase.getInstance().getReference("Chats").child(chtlis).removeValue();
            }
          });
          builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
          });
         // builder.show();
        }else {


        }
      }
    });


    if (imagUrl.equals(("default"))){
     // holder.profile_image.setImageResource(R.drawable.use4);
    }else {
     // Glide.with(mcontext).load(imagUrl).into(holder.profile_image);
    }

    try {
      if (position == mChat.size()-1){
        if (chat.isIsseen()) {
          holder.text_seen.setText("Seen");
        }else {
          holder.text_seen.setText("Delivered");
        }
      }else {
        holder.text_seen.setVisibility(View.GONE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
    Users m = mUser.get(position);

    holder.username.setText(m.getUsername());
    if (m.getImageURL().equals("default")) {
      //holder.profile_image.setImageResource(R.drawable.use4);
    } else {
     // Glide.with(mcontext).load(m.getImageURL()).into(holder.profile_image);
    }


  }


  @Override
  public int getItemCount() {
    return mChat.size();


  }

  @Override
  public int getItemViewType(int position) {

    fuser= FirebaseAuth.getInstance().getCurrentUser();
    if(mChat.get(position).getSender().equals(fuser.getUid())){
      return  MSG_TYPE_RIGHT;
    }else {
      return  MSG_TYPE_LEFT;
    }
  }
}