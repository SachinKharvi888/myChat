package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.mychat.fragment.ChatFragment;
import com.example.mychat.profileFragment.ProfileReqFragment;
import com.example.mychat.profileFragment.profileFrdFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ProfileFrdActivity extends AppCompatActivity {
    TabLayout tab;
    ViewPager viewPager;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_frd);

        back=findViewById(R.id.back2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFrdActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        Viewpageadapter viewpageadapter = new Viewpageadapter(getSupportFragmentManager());

        //adding title
        viewpageadapter.addFragment(new profileFrdFragment(),"Friends");
        viewpageadapter.addFragment(new ProfileReqFragment(),"Request");

        viewPager.setAdapter(viewpageadapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //fragments
    class Viewpageadapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public Viewpageadapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }



        @Override
        public int getCount() {
            return fragments.size();
        }
        public  void  addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


}