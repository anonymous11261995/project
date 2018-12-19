package com.example.myapplication.entity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/12/2018.
 */

public class Grocery {
    private String id;
    private String name;
    private Date created;
    private int color;
    private boolean isActive;
    private ArrayList<Product> products;

    public Grocery() {
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
