package com.example.socialmediaapp.models;

import java.util.ArrayList;

public class Posts
{
    private Long timeInMillis = 1L;
    private String textToDisplay = "";
    private ArrayList<String> likedBy = new ArrayList<>();
    private String userName = "";
    private String userId = "";
    private String imageUrl = "";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getTextToDisplay() {
        return textToDisplay;
    }

    public void setTextToDisplay(String textToDisplay) {
        this.textToDisplay = textToDisplay;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Posts(String textToDisplay, Long timeInMillis, String userName, String userId, String imageUrl)
    {
        this.timeInMillis = timeInMillis;
        this.textToDisplay = textToDisplay;
        this.userName = userName;
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public Posts()
    {

    }
}
