package com.e.thedept20.Adapters;

public class FindFriends
{
    private String Fullname;
    private String Status;
    private String profileImage;

    public FindFriends()
        {
        }

    public FindFriends(String fullname, String status, String profileImage)
        {
        Fullname = fullname;
        Status = status;
        this.profileImage = profileImage;
        }

    public String getFullname()
        {
        return Fullname;
        }

    public void setFullname(String fullname)
        {
        Fullname = fullname;
        }

    public String getStatus()
        {
        return Status;
        }

    public void setStatus(String status)
        {
        Status = status;
        }


    public String getProfileImage()
        {
        return profileImage;
        }

    public void setProfileImage(String profileImage)
        {
        this.profileImage = profileImage;
        }
}
