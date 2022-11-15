package com.example.mychat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mychat.adapter.MessageAdapter;
import com.example.mychat.fragment.ChatFragment;
import com.example.mychat.model.Chat;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView chatUserProfile;
    ImageButton sentbtn,back;
    TextView chatUserName,active;
    EditText chatTxt;
    RecyclerView recyclerView;

    ImageButton attachimage;

    CircleImageView profileImage;
    TextView username,frdcount,reqcount;
    LinearLayout profilefrds;


    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    DatabaseReference reference;
    FirebaseUser fuser;
    Intent intent;
    String userid;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    ValueEventListener seenListener;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        chatUserProfile=findViewById(R.id.userporfile1);
        chatUserName=findViewById(R.id.username1);
        sentbtn=findViewById(R.id.btn_send);
        chatTxt=findViewById(R.id.txt_btn);
        attachimage=findViewById(R.id.addimage);
        active=findViewById(R.id.active);
        recyclerView=findViewById(R.id.recycler_views);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        String token = intent.getStringExtra("token");
        String name = intent.getStringExtra("name");

        storageReference = FirebaseStorage.getInstance().getReference("ChatImage");
        fuser= FirebaseAuth.getInstance().getCurrentUser();



        attachimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });



        sentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                try {
                    String msg = chatTxt.getText().toString();
                    if (!msg.equals("")) {
                        sendMessage(fuser.getUid(), userid, msg);


                        sendNotifi(name,msg,token);
                        Users user = null;
                    } else {
                        Toast.makeText(MessageActivity.this, "You can't send a empty Message", Toast.LENGTH_SHORT).show();

                    }
                    chatTxt.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users m = snapshot.getValue(Users.class);
                chatUserName.setText(m.getUsername());
                Date date = new Date();


                if (m.getImageURL().equals("default")) {
                    chatUserProfile.setImageResource(R.drawable.user_circle_24);
                }else {
                    //change
                    Glide.with(getApplicationContext()).load(m.getImageURL()).into(chatUserProfile);
                }
                readMessage(fuser.getUid(),userid,m.getImageURL());

                if (m.getStatus().equals("Online")){
                    active.setText("Active");

                }else {

                    active.setText(snapshot.child("status").getValue().toString());
                   // active.setTextColor(R.color.green);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        SeenMessage(userid);

    }

    private  void sendMessage(String sender,String receiver,String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        String messageid = reference.push().getKey();
        Date date = new Date();
        HashMap<String,Object> hashMap  =new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("messageid",messageid);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        hashMap.put("feeling",0);
        hashMap.put("time",date.getTime());
        reference.child(messageid).setValue(hashMap);

        FirebaseDatabase.getInstance().getReference("Chatlist").child(receiver).child(fuser.getUid()).setValue(true);
        FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(receiver).setValue(true);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (notify) {

                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void  readMessage(String myid,String userid,String imageurl){
        mchat= new ArrayList<>();
        reference =FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mchat.clear();
                for(DataSnapshot snapshot1: datasnapshot.getChildren()){
                    Chat chat= snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid)||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);

                    }
                   messageAdapter=new MessageAdapter(MessageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void  SeenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid())&& chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot1.getRef().updateChildren(hashMap);
                        messageAdapter=new MessageAdapter(MessageActivity.this,chat.getSender(),chat.getReceiver());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void sendNotifi(String name,String message, String  token){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title",name);
            data.put("body",message);
            JSONObject notification = new JSONObject();
            notification.put("notification",data);
            notification.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notification, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(MessageActivity.this,"done",Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MessageActivity.this,"error",Toast.LENGTH_SHORT).show();

                }
            }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String ,String> map = new HashMap<>();
                    String key = "Key=AAAArC_2dPo:APA91bEzaRLBkqbJlVvhKTqiuMlFOjNVDVSKwiqcguH08GSk_5_h5khfXjyu4o04wsmIigEPLGwWHwSjMi0AN-DWFJnx8_l1fD-kCEzJ4yq15fqCLux8EF3dVsuBOwYNELbIwrVSZ45j";
                    map.put("Authorization",key);
                    map.put("Content-Type","application/json");
                    return map;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /////////status online
    private void  status(String status){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String,Object>hashMap =  new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
        // active.setText("werew");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("Offline");
        //active.setText("werfsdrfew");
    }
    //
//
//image select from phome storage
    //
    //
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = MessageActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    //upload
    private  void uploadimage(){
        final ProgressBar pd = new ProgressBar(MessageActivity.this);
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
                        String messageid = reference.push().getKey();

                        sendimage(fuser.getUid(),userid,muri);
                        //prog.setVisibility(View.INVISIBLE);
                    }else {
                        Toast.makeText(MessageActivity.this,"failed!",Toast.LENGTH_SHORT).show();
                        // prog.setVisibility(View.INVISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    // prog.setVisibility(View.INVISIBLE);

                }
            });

        }else {
            Toast.makeText(MessageActivity.this,"no image selected !",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== IMAGE_REQUEST &&  data!=null&& data.getData()!=null){
            imageUri=data.getData();
            if (uploadTask!=null&& uploadTask.isInProgress()){
                Toast.makeText(MessageActivity.this,"uplaod in progress!",Toast.LENGTH_SHORT).show();
                // prog.setVisibility(View.INVISIBLE);

            }else {

                uploadimage();

            }
        }
    }


    private  void sendimage(String sender,String receiver,String url) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        String messageid = reference.push().getKey();
        Date date = new Date();

        HashMap<String,Object> hashMap  =new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("messageid",messageid);
        hashMap.put("imageUrl",url);
        hashMap.put("message","photo");
        hashMap.put("isseen",false);
        hashMap.put("feeling",0);
        hashMap.put("time", date.getTime());

        // hashMap.put("Date",date.getTime());
        reference.child(messageid).setValue(hashMap);

        FirebaseDatabase.getInstance().getReference("Chatlist").child(receiver).child(fuser.getUid()).setValue(true);
        FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(receiver).setValue(true);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (notify) {

                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}