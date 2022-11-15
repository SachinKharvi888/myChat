package com.example.mychat.model;

public class Status {
    private  String  imageUrl;
    private long timeStamp;
    private  String userid;

  public Status(String imageUrl, long timeStamp){
      this.imageUrl=imageUrl;
      this.timeStamp=timeStamp;
      this.userid=userid;
  }

  public Status(){

  }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
