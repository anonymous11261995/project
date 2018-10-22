package com.example.myapplication.service;

import android.content.Context;

import com.example.myapplication.dao.GroceryDao;
import com.example.myapplication.dao.GroceryDaoImpl;
import com.example.myapplication.model.Grocery;
import com.example.myapplication.utils.AppUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;

public class GroceryService extends GenericService {
    private Context mContext;
    private GroceryDao mGroceryDao;
    private FirebaseUser mUserCurrent;


    public GroceryService(Context context, FirebaseUser user) {
        super(context);
        this.mContext = context;
        this.mGroceryDao = new GroceryDaoImpl(context);
        this.mUserCurrent = user;
    }

    public Grocery createList(String name) {
        Grocery list = new Grocery();
        Date now = new Date();
        list.setCreated(now);
        list.setName(name);
        list.setUserID(mUserCurrent.getEmail());
        list.setUserShared(new ArrayList<String>());
        String id = mUserCurrent.getEmail() + "-" + name + "-" + now.getTime();
        String encodeID = AppUtil.convertStringToUrl(id);
        list.setId(encodeID);
        mGroceryDao.update(list);
        return list;
    }

}
