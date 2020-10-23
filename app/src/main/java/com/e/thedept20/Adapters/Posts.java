package com.e.thedept20.Adapters;

public class Posts
{
    private String Date;
    private String Description;
    private String PostImage;
    private String ProfileImage;
    private String Time;
    private String Uid;
    private String fullname;
    private String Counter;

    public Posts()
        {
        }


    public Posts(String date, String description, String postImage, String profileImage, String time, String uid, String fullname)
        {
        Date = date;
        Description = description;
        PostImage = postImage;
        ProfileImage = profileImage;
        Time = time;
        Uid = uid;
        this.fullname = fullname;

        }

    public void setCounter(String counter)
        {
        Counter = counter;
        }

    public String getDate()
        {
        return Date;
        }

    public void setDate(String date)
        {
        Date = date;
        }

    public String getDescription()
        {
        return Description;
        }

    public void setDescription(String description)
        {
        Description = description;
        }

    public String getPostImage()
        {
        return PostImage;
        }

    public void setPostImage(String postImage)
        {
        PostImage = postImage;
        }

    public String getProfileImage()
        {
        return ProfileImage;
        }

    public void setProfileImage(String profileImage)
        {
        ProfileImage = profileImage;
        }

    public String getTime()
        {
        return Time;
        }

    public void setTime(String time)
        {
        Time = time;
        }

    public String getUid()
        {
        return Uid;
        }

    public void setUid(String uid)
        {
        Uid = uid;
        }

    public String getFullname()
        {
        return fullname;
        }

    public void setFullname(String fullname)
        {
        this.fullname = fullname;
        }


}
