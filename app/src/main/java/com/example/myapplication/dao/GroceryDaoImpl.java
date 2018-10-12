package com.example.myapplication.dao;

import android.content.Context;

import com.example.myapplication.model.Grocery;
import com.google.firebase.firestore.Query;

public class GroceryDaoImpl extends GenericDaoImpl implements GroceryDao {
    Context mContext;

    public GroceryDaoImpl(Context context) {
        super(context, Grocery.class, "Grocery");
        this.mContext = context;
    }


    @Override
    public void update(Grocery grocery) {
        mDocumentRef.add(grocery);
    }

    @Override
    public Query whereEqualTo(String field, Object value) {
       return mDocumentRef.whereEqualTo(field,value);
    }

    @Override
    public Query startEnd(String fieldStart, Object valueStart, String fieldEnd, Object valueEnd) {
        return mDocumentRef.startAt(fieldStart,valueStart).endAt(fieldEnd,valueEnd);
    }
}
