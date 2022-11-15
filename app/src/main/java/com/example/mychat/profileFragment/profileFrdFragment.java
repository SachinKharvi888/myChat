package com.example.mychat.profileFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mychat.R;
import com.example.mychat.adapter.FriendAdapter;
import com.example.mychat.adapter.Useradapter;
import com.example.mychat.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class profileFrdFragment extends Fragment {

    private RecyclerView recyclerView;

    private EditText search;
    ImageButton frdlistbtn;
    FirebaseUser fuser;


    private FriendAdapter FriendAdapter;
    private List<Users> mUser;

    private List<Users> tList2;
    private List<String> followingList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFollowing();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_frd, container, false);

        recyclerView = view.findViewById(R.id.profileFrdlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUser = new ArrayList<>();
       fuser = FirebaseAuth.getInstance().getCurrentUser();
        tList2 = new ArrayList<>();
       // readUser();
        return  view;
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("users");
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
                FriendAdapter=new FriendAdapter(getContext(),tList2,true);
                recyclerView.setAdapter(FriendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}