package com.example.socialmediaapp.models;

public class User
{
    private String name = "";
    private String imageUrl = "";
    private String id = "";

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public User()
    {

    }
    public User(String id, String name, String imageUrl)
    {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
