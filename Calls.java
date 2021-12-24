package com.example.contact.models;

import java.util.Date;

public class Calls {


    private String id;
    private String name;
    private String Fname;
    private String Phone;

    private String duration;
    private String date;
    private Date dateraw;

    private int photo;

    public String getId() {
        return id;
    }
    public Calls(String id, String name, String fname, int photo, String duration, String date, String phone, Date date_raw) {
        this.id = id;
        this.name = name;
        this.Fname = fname;
        this.photo = photo;
        this.duration = duration;
        this.date = date;
        this.Phone = phone;
        this.dateraw = date_raw;
    }

    public Date getDateraw() {
        return dateraw;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getFname() {
        return Fname;
    }

    public String getPhone() {
        return Phone;
    }

    public int getPhoto() {
        return photo;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
