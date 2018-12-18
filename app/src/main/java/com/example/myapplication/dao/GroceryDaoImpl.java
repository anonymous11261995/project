package com.example.myapplication.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.myapplication.database.DBContentProvider;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.utils.DefinitionSchema;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class GroceryDaoImpl extends DBContentProvider implements GroceryDao, DefinitionSchema {
    private Cursor cursor;
    private ContentValues initialValues;

    public GroceryDaoImpl(SQLiteDatabase db) {
        super(db);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Grocery cursorToEntity(Cursor cursor) {
        Grocery grocery = new Grocery();
        int idIndex, nameIndex, isActiveIndex, colorIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex(COLUMN_ID) != -1) {
                idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                grocery.setId(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex(COLUMN_NAME) != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(
                        COLUMN_NAME);
                grocery.setName(cursor.getString(nameIndex));
            }

            if (cursor.getColumnIndex(COLUMN_IS_ACVITE) != -1) {
                isActiveIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_ACVITE);
                if (cursor.getInt(isActiveIndex) == 0) {
                    grocery.setActive(false);
                } else {
                    grocery.setActive(true);
                }
            }
            if (cursor.getColumnIndex(COLUMN_CODE_COLOR) != -1) {
                colorIndex = cursor.getColumnIndexOrThrow(COLUMN_CODE_COLOR);
                grocery.setColor(cursor.getInt(colorIndex));
            }
        }

        return grocery;
    }


    @Override
    public Grocery fetchShoppingListById(String id) {
        final String selectionArgs[] = {String.valueOf(id)};
        final String selection = COLUMN_ID + " = ?";
        Grocery grocery = new Grocery();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                grocery = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return grocery;
    }

    @Override
    public Grocery fetchShoppingListByName(String name) {
        final String selectionArgs[] = {String.valueOf(name)};
        final String selection = COLUMN_NAME + " = ?";
        Grocery grocery = new Grocery();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                grocery = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return grocery;
    }

    @Override
    public ArrayList<Grocery> fetchAllShoppingList() {
        ArrayList<Grocery> list = new ArrayList<>();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, null,
                null, COLUMN_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Grocery item = cursorToEntity(cursor);
                list.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return list;
    }

    @Override
    public boolean create(Grocery grocery) {
        setContentValue(grocery);
        try {
            return super.insert(SHOPPING_LIST_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("DatabaseHelper", ex.getMessage());
            return false;
        }

    }

    @Override
    public boolean update(Grocery grocery) {
        final String selectionArgs[] = {String.valueOf(grocery.getId())};
        final String selection = COLUMN_ID + " = ?";
        setContentValue(grocery);
        try {
            return super.update(SHOPPING_LIST_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Grocery grocery) {
        final String selectionArgs[] = {String.valueOf(grocery.getId())};
        final String selection = COLUMN_ID + " = ?";
        try {
            return super.delete(SHOPPING_LIST_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public Grocery fetchShoppingListActive() {
        final String selectionArgs[] = {String.valueOf(1)};
        final String selection = COLUMN_IS_ACVITE + " = ?";
        Grocery grocery = new Grocery();
        cursor = super.query(SHOPPING_LIST_TABLE, SHOPPING_LIST_COLUMNS, selection,
                selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                grocery = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return grocery;
    }

    private void setContentValue(Grocery grocery) {
        initialValues = new ContentValues();
        initialValues.put(COLUMN_ID, grocery.getId());
        initialValues.put(COLUMN_NAME, grocery.getName());
        initialValues.put(COLUMN_IS_ACVITE, grocery.isActive());
        initialValues.put(COLUMN_CODE_COLOR, grocery.getColor());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
