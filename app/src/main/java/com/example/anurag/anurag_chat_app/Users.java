package com.example.anurag.anurag_chat_app;

/**
 * Created by anurag on 6/3/18.
 */

public class Users {
    public String name;
    public String image;
    public String status;
    public String thumb_image;
    public String date;

    public Users()
    {

    }
    public Users(String name, String image, String status,String date,String thumb_image) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.date=date;
        this.thumb_image=thumb_image;
    }

    public String getName() {

        return name;
    }

    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date=date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getThumb_image(){return thumb_image;}
    public void setThumb_image(String thumb_image)
    {
        this.thumb_image=thumb_image;
    }
}