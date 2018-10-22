package com.example.myapplication.model;

import java.util.Date;

public class Product {
    private String id;
    private Date created;
    private String groceryID;
    private String name;
    private Boolean isPurchased;
    private int quantity;

    public Product() {
        this.id = "";
        this.groceryID = "";
        this.name = "";
        this.quantity = 1;
        this.isPurchased = false;
        this.created = new Date();
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

    public Boolean getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(Boolean purchased) {
        isPurchased = purchased;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getGroceryID() {
        return groceryID;
    }

    public void setGroceryID(String groceryID) {
        this.groceryID = groceryID;
    }
}
