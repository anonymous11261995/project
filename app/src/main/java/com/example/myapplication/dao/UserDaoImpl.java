package com.example.myapplication.dao;

import android.content.Context;

import com.example.myapplication.model.Grocery;

public class UserDaoImpl extends GenericDaoImpl implements UserDao {
    Context mContext;

    public UserDaoImpl(Context context) {
        super(context, Grocery.class, "User");
        this.mContext = context;
    }
}
