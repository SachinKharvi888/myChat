package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.mychat.fragment.ActivityFragment;
import com.example.mychat.fragment.ChatFragment;
import com.example.mychat.fragment.HomeFragment;
import com.example.mychat.fragment.ProfileFragment;
import com.example.mychat.fragment.UsersFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;

public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation meowBottomNavigation;

    FirebaseUser fuser;


    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meowBottomNavigation=findViewById(R.id.meowBottomNavigation);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.star_rate_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.chat_bubble_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.home_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.user_circle_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.profile_24));

        meowBottomNavigation.show(3, true);


        fuser= FirebaseAuth.getInstance().getCurrentUser();

        replace( new HomeFragment());

        meowBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        replace( new ActivityFragment());
                        break;
                    case 2:
                        replace( new ChatFragment());
                        break;
                    case 3:
                        replace( new HomeFragment());
                        break;

                    case 4:
                        replace( new UsersFragment());
                        break;
                    case 5:
                        replace( new ProfileFragment());
                        break;
                }
                return null;
            }
        });


        FirebaseDatabase database =FirebaseDatabase.getInstance();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                HashMap<String,Object>map = new HashMap<>();
                map.put("token",token);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
                Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void replace(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framlayout,fragment);
        transaction.commit();
    }
    /////////status online
    private void  status(String status){
        try {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            HashMap<String,Object> hashMap =  new HashMap<>();
            hashMap.put("status",status);
            reference.updateChildren(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Date date = new Date();

        String times;
        long time = date.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        times= dateFormat.format(new Date(time));
        status(String.valueOf(times));
    }
}