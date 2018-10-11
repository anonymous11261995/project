package com.example.myapplication.dao;

import android.content.Context;

import com.example.myapplication.model.GroceryList;

public class UserDaoImpl extends GenericDaoImpl implements UserDao {
    Context mContext;

    public UserDaoImpl(Context context) {
        super(context, GroceryList.class, "User");
        this.mContext = context;
    }
}
