package com.example.myapplication.entity;


import java.util.Date;

/**
 * Created by TienTruong on 7/12/2018.
 */

public class Product {
    private String id;
    private String name;
    private Date created;
    private int quantity;
    private int order;
    private boolean isAutocomplete;
    private boolean isPurchased;
    private Grocery grocery;

    public Product() {
        this.id = "";
        this.name = "";
        this.created = new Date();
        this.quantity = 1;
        this.order = 0;
        this.isAutocomplete = true;
        this.isPurchased = false;
        this.grocery = new Grocery();
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isAutocomplete() {
        return isAutocomplete;
    }

    public void setAutocomplete(boolean autocomplete) {
        isAutocomplete = autocomplete;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public Grocery getGrocery() {
        return grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
    }
}
