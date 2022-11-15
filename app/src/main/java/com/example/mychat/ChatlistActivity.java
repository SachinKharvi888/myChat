package com.example.mychat;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mychat.adapter.FriendAdapter;
import com.example.mychat.adapter.TopStatusAdapter;
import com.example.mychat.adapter.Useradapter;
import com.example.mychat.fragment.ChatFragment;
import com.example.mychat.model.Users;
import com.example.mychat.profileFragment.ProfileReqFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatlistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseUser fuser;
    private EditText search;
    ImageButton frdlistbtn,back;
    private FriendAdapter FriendAdapter;
    private List<Users> mUser;
    private List<Users> tList2;
    private List<String> followingList;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        back=findViewById(R.id.back1);
        recyclerView =findViewById(R.id.chatlistrecy);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatlistActivity.this));




        mUser = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        tList2 = new ArrayList<>();
        checkFollowing();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatlistActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

               // Fragment fragment = new ProfileReqFragment();
               // FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
               // fragmentTransaction.replace(R.id.container1, fragment);
               // fragmentTransaction.commit();
            }
        });


    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void readUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                tList2.clear();
                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    Users m = snapshot.getValue(Users.class);
                    for (String id : followingList){
                        if (m.getId().equals(id)){
                            tList2.add(m);

                        }
                    }
                }
                FriendAdapter=new FriendAdapter(ChatlistActivity.this,tList2,true);
                recyclerView.setAdapter(FriendAdapter);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}