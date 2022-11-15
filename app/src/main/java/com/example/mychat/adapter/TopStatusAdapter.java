package com.example.mychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychat.MainActivity;
import com.example.mychat.R;
import com.example.mychat.model.Status;
import com.example.mychat.model.User_status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.ViewHolder>{

    private Context mcontext;
    private ArrayList<User_status> mUser;
    private  boolean ischat;

    FirebaseAuth auth;

    FirebaseUser fuser;
    private List<Status> mUr;


    public TopStatusAdapter(Context mcontext, ArrayList<User_status> mUser, boolean ischat) {
        this.mcontext=mcontext;
        this.mUser=mUser;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public TopStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_story,parent,false);
        return new TopStatusAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusAdapter.ViewHolder holder, int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        auth=FirebaseAuth.getInstance();
        User_status m = mUser.get(position);
      // Status s = mUr.get(position);

       // Status lastStatus = m.getStatuses().get(m.getStatuses().size()-1);
        //Log.d("Hello",m.getUsername());
        //
        holder.storyname.setText(m.getName());

        if (m.getProfileImage().equals("default")) {
             holder.storyuserimage.setImageResource(R.drawable.use4);
        } else {
            Glide.with(mcontext).load(((m.getProfileImage()))).into(holder.storyuserimage);
        }





        holder.storyuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                ArrayList<String> mytime = new ArrayList<>();

                // for (Status ss :s.getImageUrl().toing()){
                       // myStories.add(new MyStory((m.getProfileImage())));
                   // }

                for(Status status : m.getStatuses()) {
                    myStories.add(new MyStory(status.getImageUrl()));

                    long time = status.getTimeStamp();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                    Toast.makeText(mcontext, dateFormat.format(new Date(time)), Toast.LENGTH_SHORT).show();

                    mytime.add((dateFormat.format(new Date(time))));
                    new StoryView.Builder(((MainActivity) mcontext).getSupportFragmentManager())
                            .setStoriesList(myStories) // Required
                            .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                            .setTitleText(m.getName()) // Default is Hidden
                            .setSubtitleText(mytime.toString()) // Default is Hidde
                            .setTitleLogoUrl(m.getProfileImage()) // Default is Hidden
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {
                                    //your action
                                }

                                @Override
                                public void onTitleIconClickListener(int position) {
                                    //your action
                                }
                            }) // Optional Listeners
                            .build() // Must be called before calling show method

                            .show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView storyuserimage;
        TextView storyname;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            storyuserimage=itemView.findViewById(R.id.storyUserimage);
            storyname=itemView.findViewById(R.id.storyname);

        }
    }
}
