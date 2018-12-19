package com.example.myapplication.dao;



import com.example.myapplication.entity.Grocery;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface GroceryDao {
     Grocery findByName(String name);
     ArrayList<Grocery> fetchAll();
    // add user
     boolean create(Grocery grocery);
     boolean update(Grocery grocery);
     boolean delete(Grocery grocery);
     Grocery fetchShoppingListActive();
}
