package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.Date;

public class Grocery {
    private String id;
    private Date created;
    private String name;
    private String userID;
    private ArrayList<String> userShared;

    public Grocery() {
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public ArrayList<String> getUserShared() {
        return userShared;
    }

    public void setUserShared(ArrayList<String> userShared) {
        this.userShared = userShared;
    }
}
