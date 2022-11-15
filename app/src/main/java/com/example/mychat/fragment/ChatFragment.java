package com.example.mychat.fragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychat.ChatlistActivity;
import com.example.mychat.R;
import com.example.mychat.adapter.FriendAdapter;
import com.example.mychat.adapter.TopStatusAdapter;
import com.example.mychat.model.Status;
import com.example.mychat.model.User_status;
import com.example.mychat.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {

    ImageButton newcahtlist;
    RecyclerView recyclerView,storyRecycler;;
    TextView frdcount,reqcount;
    CircleImageView newStoryadd;
    ArrayList<Status> userStatuses;
    ArrayList<User_status> userStatusess;



    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;


    String  name;
    String image;
    Task<Void> reference;
    FirebaseUser fuser;
    FirebaseDatabase database;

    private FriendAdapter FriendAdapter;
    private TopStatusAdapter topStatusAdapter;

    private ArrayList<User_status> mUser;

    private List<Users> tList2;
    private List<String> followingList;
    private List<String> frd2;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        newcahtlist=view.findViewById(R.id.newchatlist);


        newStoryadd=view.findViewById(R.id.newstoryadd);
        recyclerView = view.findViewById(R.id.mychatlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storyRecycler=view.findViewById(R.id.storyList);
        storyRecycler.setHasFixedSize(true);
        storyRecycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true));

        mUser = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        tList2 = new ArrayList<>();
        userStatusess = new ArrayList<>();

        checkFollowing();
         //readUser2();
        //checkfrds();
        // topStatusAdapter = new TopStatusAdapter(getContext(), userStatusess,true);
        topStatusAdapter=new TopStatusAdapter(getContext(),userStatusess,true);


        storageReference = FirebaseStorage.getInstance().getReference("Stories");
        fuser= FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name=snapshot.child("username").getValue().toString();
                image=snapshot.child("imageURL").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newStoryadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImage();
            }
        });




        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("stories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userStatusess.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot storySnapshot : snapshot.getChildren()) {
                        User_status status = new User_status();

                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileImage(storySnapshot.child("profileImage").getValue(String.class));
                        status.setLastupdated(storySnapshot.child("lastUpdated").getValue(Long.class));
                        status.setUserid(storySnapshot.child("userid").getValue(String.class));
                        frd2 = new ArrayList<>();

                        ArrayList<Status> statuses = new ArrayList<>();
                        //Log.d("Hello",m.getUsername());

                        for(DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);

                         //   Glide.with(getContext()).load(((sampleStatus.getImageUrl()))).into(newStoryadd);


                        }
                        for (String id : frd2){
                            if (status.getUserid().equals(id)){
                                userStatusess.add(status);

                            }
                            storyRecycler.setAdapter(topStatusAdapter);

                        }

                        status.setStatuses(statuses);
                       userStatusess.add(status);


                    }
                // topStatusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newcahtlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChatlistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;

    }
    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readUser();
                //readUser2();
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
                FriendAdapter=new FriendAdapter(getContext(),tList2,false);
                recyclerView.setAdapter(FriendAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkfrds() {
        frd2 = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    frd2.add(snapshot.getKey());
                }

              // readPosts();
                Toast.makeText(getContext(),"jhjhj", Toast.LENGTH_SHORT).show();

                readUser2();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readUser2() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("stories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                userStatusess.clear();
                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    User_status m = snapshot.getValue(User_status.class);
                    for (String id : frd2){
                        if (m.getUserid().equals(id)){
                            userStatusess.add(m);

                       }
                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readPosts(){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("stories");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userStatusess.clear();
                    Toast.makeText(getContext(),"jhcvcvjhj", Toast.LENGTH_SHORT).show();

                    try {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Toast.makeText(getContext(),"2", Toast.LENGTH_SHORT).show();

                            User_status post = snapshot.getValue(User_status.class);
                            Toast.makeText(getContext(),"3", Toast.LENGTH_SHORT).show();

                            for (String id : frd2){
                                Toast.makeText(getContext(),"jhcvcvjhj", Toast.LENGTH_SHORT).show();

                               if (post.getUserid().equals(id)){
                                    userStatusess.add(post);

                                   Toast.makeText(getContext(),userStatusess.toString(), Toast.LENGTH_SHORT).show();
                                }
                                //postList.add(post);
                            }
                            userStatusess.add(post);



                        }

                        //recyclerView.setAdapter(postAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    topStatusAdapter=new TopStatusAdapter(getContext(),userStatusess,true);
                    storyRecycler.setAdapter(topStatusAdapter);




                    topStatusAdapter.notifyDataSetChanged();

                    //progress_circular.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //upload
    private  void uploadimage(){
        final ProgressBar pd = new ProgressBar(getContext());
        pd.isShown();
        //  prog.setVisibility(View.VISIBLE);

        if (imageUri !=null){
            final StorageReference fileRefrece = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask=fileRefrece.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();

                    }
                    return fileRefrece.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri= task.getResult();
                        String muri = downloadUri.toString();
                        Date date = new Date();


                        User_status userStatus = new User_status();
                        userStatus.setName(name);
                        userStatus.setProfileImage(image);
                        userStatus.setLastupdated(date.getTime());
                        userStatus.setUserid(FirebaseAuth.getInstance().getUid());

                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("name", userStatus.getName());
                        obj.put("profileImage", userStatus.getProfileImage());
                        obj.put("lastUpdated", userStatus.getLastupdated());
                        obj.put("userid", userStatus.getUserid());


                        Status status = new Status(muri, userStatus.getLastupdated());

                        reference = FirebaseDatabase.getInstance().getReference("stories").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);

                        reference=FirebaseDatabase.getInstance().getReference().child("stories")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child("statuses")
                                .push()
                                .setValue(status);




                      //  sendimage(fuser.getUid(),muri);
                        //prog.setVisibility(View.INVISIBLE);
                    }else {
                        Toast.makeText(getContext(),"failed!",Toast.LENGTH_SHORT).show();
                        // prog.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    // prog.setVisibility(View.INVISIBLE);

                }
            });

        }else {
            Toast.makeText(getContext(),"no image selected !",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== IMAGE_REQUEST &&  data!=null&& data.getData()!=null){
            imageUri=data.getData();
            if (uploadTask!=null&& uploadTask.isInProgress()){
                Toast.makeText(getContext(),"uplaod in progress!",Toast.LENGTH_SHORT).show();
                // prog.setVisibility(View.INVISIBLE);

            }else {

                uploadimage();

            }
        }
    }



}