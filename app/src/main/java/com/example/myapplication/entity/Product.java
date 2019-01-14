package com.example.myapplication.entity;


import java.util.Date;

/**
 * Created by TienTruong on 7/12/2018.
 */

public class Product {
    private String id;
    private String name;
    private Date created;
    private double quantity;
    private int order;
    private String unit;
    private String note;
    private boolean isAutocomplete;
    private boolean isPurchased;
    private Grocery grocery;

    public Product() {
        this.id = "";
        this.name = "";
        this.created = new Date();
        this.quantity = 0;
        this.unit = "";
        this.note = "";
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
