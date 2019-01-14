package com.example.myapplication.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.myapplication.database.DBContentProvider;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class ProductDaoImpl extends DBContentProvider implements ProductDao {
    private final String TAG = ProductDao.class.getName();
    private final String[] PRODUCT_COLUMNS = new String[]{"id", "grocery", "name", "created",
            "quantity", "unit", "note", "order_list", "autocomplete", "purchased"};
    private final String PRODUCT_TABLE = "product";
    private Cursor cursor;
    private ContentValues initialValues;

    public ProductDaoImpl(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public ArrayList<Product> fetchAll() {
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_TABLE, PRODUCT_COLUMNS, null,
                null, "created");

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }


    @Override
    public ArrayList<Product> findByName(String name) {
        name = escape(name);
        final String selection = "name= '" + name + "'";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_TABLE, PRODUCT_COLUMNS, selection,
                null, "created");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public Product findById(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = "id = ?";
        Product product = new Product();
        cursor = super.query(PRODUCT_TABLE, PRODUCT_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                product = cursorToEntity(cursor);
                break;
            }
            cursor.close();
        }
        return product;

    }

    @Override
    public ArrayList<Product> findByAutocomplete() {

        String selection = "autocomplete = 1";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_TABLE, PRODUCT_COLUMNS, selection,
                null, "created");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public ArrayList<Product> findByGrocery(Grocery grocery) {
        String selection = "grocery = '" + grocery.getId() + "'";
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.query(PRODUCT_TABLE, PRODUCT_COLUMNS, selection,
                null, "order_list,created");
        // Log.d(TAG,)
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToEntity(cursor);
                list.add(product);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    @Override
    public boolean update(Product product) {
        final String selectionArgs[] = {String.valueOf(product.getId())};
        final String selection = "id = ?";
        setContentValue(product);
        try {
            return super.update(PRODUCT_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean create(Product product) {
        setContentValue(product);
        try {
            return super.insert(PRODUCT_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }

    }

    @Override
    public ArrayList<Product> findByQuery(String query) {
        ArrayList<Product> list = new ArrayList<>();
        cursor = super.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product p = cursorToEntity(cursor);
                list.add(p);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }


    @Override
    protected Product cursorToEntity(Cursor cursor) {
        Product product = new Product();
        int idIndex, nameIndex, createIndex, quanityIndex, unitIndex, noteIndex, autocompleteIndex,
                isPurchsedIndex, groceryIndex, orderIndex;
        if (cursor != null) {
            idIndex = cursor.getColumnIndexOrThrow("id");
            product.setId(cursor.getString(idIndex));

            nameIndex = cursor.getColumnIndexOrThrow("name");
            String name = cursor.getString(nameIndex);
            product.setName(upcape(name));

            createIndex = cursor.getColumnIndexOrThrow("created");
            product.setCreated(new Date(cursor.getLong(createIndex)));

            quanityIndex = cursor.getColumnIndexOrThrow("quantity");
            product.setQuantity(cursor.getDouble(quanityIndex));

            unitIndex = cursor.getColumnIndexOrThrow("unit");
            product.setUnit(cursor.getString(unitIndex));

            noteIndex = cursor.getColumnIndexOrThrow("note");
            product.setNote(cursor.getString(noteIndex));

            autocompleteIndex = cursor.getColumnIndexOrThrow("autocomplete");
            if (cursor.getInt(autocompleteIndex) == 0) {
                product.setAutocomplete(false);
            } else {
                product.setAutocomplete(true);
            }

            isPurchsedIndex = cursor.getColumnIndexOrThrow("purchased");
            if (cursor.getInt(isPurchsedIndex) == 0) {
                product.setPurchased(false);
            } else {
                product.setPurchased(true);
            }

            groceryIndex = cursor.getColumnIndexOrThrow("grocery");
            if (cursor.getString(groceryIndex) != null) {
                Grocery grocery = new Grocery();
                grocery.setId(cursor.getString(groceryIndex));
                product.setGrocery(grocery);
            }

            orderIndex = cursor.getColumnIndexOrThrow("order_list");
            product.setOrder(cursor.getInt(orderIndex));

        }

        return product;
    }


    private void setContentValue(Product product) {
        try {
            initialValues = new ContentValues();
            initialValues.put("id", product.getId());
            initialValues.put("name", product.getName());
            initialValues.put("created", product.getCreated().getTime());
            initialValues.put("quantity", product.getQuantity());
            initialValues.put("unit", product.getUnit());
            initialValues.put("note", product.getNote());
            initialValues.put("order_list", product.getOrder());
            initialValues.put("autocomplete", product.isAutocomplete());
            initialValues.put("purchased", product.isPurchased());
            if (product.getGrocery() != null) {
                initialValues.put("grocery", product.getGrocery().getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
            initialValues = new ContentValues();
        }

    }

    private ContentValues getContentValue() {
        return initialValues;
    }


}
