package com.example.myapplication.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.database.DBContentProvider;
import com.example.myapplication.entity.Grocery;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/20/2018.
 */

public class GroceryDaoImpl extends DBContentProvider implements GroceryDao {
    private final String[] GROCERY_LIST_COLUMNS = new String[]{"id", "name", "active", "created", "color", "note"};
    private final String GROCERY_TABLE = "grocery";
    private Cursor cursor;
    private ContentValues initialValues;

    public GroceryDaoImpl(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public Grocery findByName(String name) {
        final String selectionArgs[] = {String.valueOf(name)};
        final String selection = "name = ?";
        Grocery grocery = new Grocery();
        cursor = super.query(GROCERY_TABLE, GROCERY_LIST_COLUMNS, selection,
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
    public ArrayList<Grocery> fetchAll() {
        ArrayList<Grocery> list = new ArrayList<>();
        cursor = super.query(GROCERY_TABLE, GROCERY_LIST_COLUMNS, null,
                null, "created");

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
            return super.insert(GROCERY_TABLE, getContentValue()) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("DatabaseHelper", ex.getMessage());
            return false;
        }

    }

    @Override
    public boolean update(Grocery grocery) {
        final String selectionArgs[] = {String.valueOf(grocery.getId())};
        final String selection = "id = ?";
        setContentValue(grocery);
        try {
            return super.update(GROCERY_TABLE, getContentValue(), selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Grocery grocery) {
        final String selectionArgs[] = {String.valueOf(grocery.getId())};
        final String selection = "id = ?";
        try {
            return super.delete(GROCERY_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Update database", ex.getMessage());
            return false;
        }
    }

    @Override
    public Grocery getListActive() {
        final String selectionArgs[] = {String.valueOf(1)};
        final String selection = "is_active = ?";
        Grocery grocery = new Grocery();
        cursor = super.query(GROCERY_TABLE, GROCERY_LIST_COLUMNS, selection,
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
    protected Grocery cursorToEntity(Cursor cursor) {
        Grocery grocery = new Grocery();
        int idIndex, nameIndex, isActiveIndex, colorIndex, createdIndex, noteIndex;

        if (cursor != null) {
            if (cursor.getColumnIndex("id") != -1) {
                idIndex = cursor.getColumnIndexOrThrow("id");
                grocery.setId(cursor.getString(idIndex));
            }
            if (cursor.getColumnIndex("name") != -1) {
                nameIndex = cursor.getColumnIndexOrThrow(
                        "name");
                grocery.setName(cursor.getString(nameIndex));
            }

            if (cursor.getColumnIndex("active") != -1) {
                isActiveIndex = cursor.getColumnIndexOrThrow("active");
                if (cursor.getInt(isActiveIndex) == 0) {
                    grocery.setActive(false);
                } else {
                    grocery.setActive(true);
                }
            }
            if (cursor.getColumnIndex("color") != -1) {
                colorIndex = cursor.getColumnIndexOrThrow("color");
                grocery.setColor(cursor.getInt(colorIndex));
            }
            if (cursor.getColumnIndex("created") != -1) {
                createdIndex = cursor.getColumnIndexOrThrow("created");
                grocery.setCreated(new Date(cursor.getLong(createdIndex)));
            }

            if (cursor.getColumnIndex("note") != -1) {
                noteIndex = cursor.getColumnIndexOrThrow("created");
                grocery.setNote(cursor.getString(noteIndex));
            }
        }

        return grocery;
    }


    private void setContentValue(Grocery grocery) {
        initialValues = new ContentValues();
        initialValues.put("id", grocery.getId());
        initialValues.put("name", grocery.getName());
        initialValues.put("active", grocery.isActive());
        initialValues.put("color", grocery.getColor());
        initialValues.put("created", grocery.getCreated().getTime());
        initialValues.put("note", grocery.getNote());
    }

    private ContentValues getContentValue() {
        return initialValues;
    }
}
