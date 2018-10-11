package com.example.myapplication.dao;

import android.content.Context;

import com.example.myapplication.model.GroceryList;

public class GroceryDaoImpl extends GenericDaoImpl implements GroceryDao {
    Context mContext;

    public GroceryDaoImpl(Context context) {
        super(context, GroceryList.class, "GroceryList");
        this.mContext = context;
    }


    @Override
    public void update(GroceryList groceryList) {
        mDocumentRef.add(groceryList);
    }

}
