package com.example.myapplication.dao;

import com.example.myapplication.model.Grocery;
import com.google.firebase.firestore.Query;

public interface GroceryDao {
     void update(Grocery grocery);
     Query whereEqualTo(String field, Object value);
     Query startEnd(String fieldStart, Object valueStart, String fieldEnd, Object valueEnd);
}
