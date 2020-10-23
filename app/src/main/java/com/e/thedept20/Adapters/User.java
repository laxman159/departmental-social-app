package com.e.thedept20.Adapters;

public class User
{

    private String Username;
    private String profileImage;
    private String Status;

    public User()
        {
        }

    public User(String username, String profileImage, String status)
        {
        Username = username;
        this.profileImage = profileImage;
        Status = status;
        }

    public String getUsername()
        {
        return Username;
        }

    public void setUsername(String username)
        {
        Username = username;
        }

    public String getProfileImage()
        {
        return profileImage;
        }

    public void setProfileImage(String profileImage)
        {
        this.profileImage = profileImage;
        }

    public String getStatus()
        {
        return Status;
        }

    public void setStatus(String status)
        {
        Status = status;
        }
}
