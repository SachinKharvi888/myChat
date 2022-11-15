package com.example.mychat.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychat.LoginActivity;
import com.example.mychat.ProfileFrdActivity;
import com.example.mychat.R;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    CircleImageView profileImage;
    TextView username,frdcount,reqcount;
    LinearLayout profilefrds;

    Button logout;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    DatabaseReference reference;
    FirebaseUser fuser;
    FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage=view.findViewById(R.id.userProfile);
        username=view.findViewById(R.id.usernameprofile);
        profilefrds=view.findViewById(R.id.requestpage);

        profilefrds=view.findViewById(R.id.requestpage);
        reqcount=view.findViewById(R.id.reqCount);
        frdcount=view.findViewById(R.id.frdCount);

        logout=view.findViewById(R.id.logout);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        fuser= FirebaseAuth.getInstance().getCurrentUser();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "logout done",Toast.LENGTH_SHORT).show();
                auth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users m = snapshot.getValue(Users.class);
                username.setText(m.getUsername());
                //a.setText(m.getBio().toString());
                // String s = snapshot.child("name").getValue().toString();
                try {
                    if (m.getImageURL().equals("default")){
                        profileImage.setImageResource(R.drawable.ic_launcher_background);
                        //a.setText(m.getBio());
                    }else {
                        Glide.with(getContext()).load(m.getImageURL()).into(profileImage);
                        //a.setText(m.getBio());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                frdcount(frdcount,fuser.getUid());
                reqcount(reqcount,fuser.getUid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        profilefrds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileFrdActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImage();
            }
        });

        return  view;
    }

    private void  frdcount(TextView likes,String profileid){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(profileid).child("users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    likes.setText(snapshot.getChildrenCount() + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void  reqcount(TextView likes,String profileid){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Request").child(profileid).child("users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    likes.setText(snapshot.getChildrenCount() + "");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


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
                        //bioss=bios.getText().toString();

                        reference= FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL",muri);
                        // map.put("Bio",bioss);



                        reference.updateChildren(map);
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