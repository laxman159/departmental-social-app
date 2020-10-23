package com.e.thedept20.Adapters;

import com.e.thedept20.LoginRegister.ProfilePicSetupActivity;

public class Comments
{
    private String Uid ,Comment, Date, Time, Username,Profileimage;

    public Comments()
        {
        }

    public Comments(String uid,String comment, String date, String time, String username,String profileimage )
        {
        Uid=uid;
        Comment = comment;
        Date = date;
        Time = time;
        Username = username;
        Profileimage=profileimage;
        }

    public String getUid()
        {
        return Uid;
        }

    public void setUid(String uid)
        {
        Uid = uid;
        }

    public String getComment()
        {
        return Comment;
        }

    public void setComment(String comment)
        {
        Comment = comment;
        }

    public String getDate()
        {
        return Date;
        }

    public void setDate(String date)
        {
        Date = date;
        }

    public String getTime()
        {
        return Time;
        }

    public void setTime(String time)
        {
        Time = time;
        }

    public String getUsername()
        {
        return Username;
        }

    public void setUsername(String username)
        {
        Username = username;
        }

    public String getProfileimage()
        {
        return Profileimage;
        }

    public void setProfileimage(String profileimage)
        {
        Profileimage = profileimage;
        }
}
