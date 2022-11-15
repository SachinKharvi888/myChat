package com.example.mychat.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class User_status {
    private String name,profileImage;
    private long lastupdated;
    private ArrayList<Status> statuses;
    private  String userid;

    public User_status(){

    }
public User_status(String name,String profileImage, long lastupdated,ArrayList<Status> statuses,String userid){
    this.name=name;
    this.profileImage=profileImage;
    this.lastupdated=lastupdated;
    this.statuses=statuses;
    this.userid=userid;

}




    public String getName() {
        return name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }
}
