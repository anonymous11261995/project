package com.example.myapplication.dao;



import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public interface ProductDao {
    ArrayList<Product> fetchAll();

    ArrayList<Product> findByName(String name);

    Product findById(String id);

    boolean update(Product item);

    boolean create(Product item);

    ArrayList<Product> findByQuery(String query);

    ArrayList<Product> findByAutocomplete();

    ArrayList<Product> findByGrocery(Grocery grocery);


}
