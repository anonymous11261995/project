package com.example.myapplication.dao;



import com.example.myapplication.entity.Grocery;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface GroceryDao {
     Grocery fetchShoppingListById(String id);
     Grocery fetchShoppingListByName(String name);
     ArrayList<Grocery> fetchAllShoppingList();
    // add user
     boolean create(Grocery grocery);
     boolean update(Grocery grocery);
     boolean delete(Grocery grocery);
     Grocery fetchShoppingListActive();
}
