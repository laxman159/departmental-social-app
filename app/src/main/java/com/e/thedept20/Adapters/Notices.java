package com.e.thedept20.Adapters;

public class Notices
{
    private String Date;
    private String Description;
    private String ProfileImage;
    private String Uid;
    private String fullname;
    private Long Counter;

    public Notices()
        {
        }

    public Notices(String date, String description, String profileImage,  String uid, String fullname, Long counter)
        {
        Date = date;
        Description = description;
        ProfileImage = profileImage;
        Uid = uid;
        this.fullname = fullname;
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

    public String getProfileImage()
        {
        return ProfileImage;
        }

    public void setProfileImage(String profileImage)
        {
        ProfileImage = profileImage;
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

    public Long getCounter()
        {
        return Counter;
        }

    public void setCounter(Long counter)
        {
        Counter = counter;
        }
}
