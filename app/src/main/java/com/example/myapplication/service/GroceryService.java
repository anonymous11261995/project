package com.example.myapplication.service;

import android.content.Context;
import android.util.Log;


import com.example.myapplication.R;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.utils.ColorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/16/2018.
 */

public class GroceryService extends GenericService {
    private static final String TAG = GroceryService.class.getSimpleName();
    private Context mContext;

    public GroceryService(Context context) {
        super(context);
        this.mContext = context;
    }

    public void deleteList(Grocery grocery) {
        mGroceryDao.delete(grocery);

    }

    public void restoreList(Grocery grocery) {
        mGroceryDao.create(grocery);
    }

    public Grocery createList(String name) {
        Grocery grocery = new Grocery();
        grocery.setActive(true);
        grocery.setId(createCodeId(name));
        grocery.setName(name);
        grocery.setColor(ColorUtils.randomColor(mContext));
        grocery.setCreated(new Date());
        mGroceryDao.create(grocery);
        return grocery;
    }

    public ArrayList<Grocery> getAllShoppingList() {
        ArrayList<Grocery> result = new ArrayList<>();
        ArrayList<Grocery> list = mGroceryDao.fetchAll();
        for (Grocery grocery : list) {
            if (grocery.getColor() == 0) {
                int color = ColorUtils.randomColor(mContext);
                grocery.setColor(color);
                mGroceryDao.update(grocery);
            }
            result.add(grocery);
        }
        return result;
    }

    public boolean checkBeforeUpdateList(String name) {
        if (name.trim().equals("")) {
            return false;
        }
        Grocery grocery = mGroceryDao.findByName(name);
        if (grocery.getName() != null) return false;
        return true;
    }

    public void activeList(Grocery grocery) {
        ArrayList<Grocery> list = getAllShoppingList();
        for (Grocery item : list) {
            if (!item.getId().equals(grocery.getId())) {
                item.setActive(false);
                mGroceryDao.update(item);
            }
        }
        grocery.setActive(true);
        mGroceryDao.update(grocery);
    }

    public Grocery getListActive(){
        return mGroceryDao.getListActive();
    }

}