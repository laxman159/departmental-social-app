package com.e.thedept20.Notifications;

public class Data
{
    private String user, body, title, send;
    private Integer icon;

    public Data()
        {
        }

    public Data(String user, String body, String title, String send, Integer icon)
        {
        this.user = user;
        this.body = body;
        this.title = title;
        this.send = send;
        this.icon = icon;
        }

    public String getUser()
        {
        return user;
        }

    public void setUser(String user)
        {
        this.user = user;
        }

    public String getBody()
        {
        return body;
        }

    public void setBody(String body)
        {
        this.body = body;
        }

    public String getTitle()
        {
        return title;
        }

    public void setTitle(String title)
        {
        this.title = title;
        }

    public String getSend()
        {
        return send;
        }

    public void setSend(String send)
        {
        this.send = send;
        }

    public Integer getIcon()
        {
        return icon;
        }

    public void setIcon(Integer icon)
        {
        this.icon = icon;
        }
}
