package com.example.myapplication.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.fragment.GroceryFragment;
import com.example.myapplication.helper.DatabaseHelper;
import com.example.myapplication.helper.PrefManager;

import java.io.File;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static DatabaseHelper mDb;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);
        mDb = new DatabaseHelper(this);
        initDatabase();
        initViews();
        displayFragment();
    }

    @Override
    protected void onResume() {
        if (!mDb.isOpening()) {
            Log.d("SQLite", "Open again database");
            mDb.openDataBase();
        }
        Log.d(TAG, "Resume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            mDb.close();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    private void initDatabase() {
        File sqlFile = getApplicationContext().getDatabasePath(AppConfig.DATABASE_NAME);
        if (!sqlFile.exists()) {
            SQLiteDatabase db_Read;
            db_Read = mDb.getWritableDatabase();
            db_Read.close();
            Log.d(TAG, "File data.sql not exitst ");
            if (mDb.copyDatabase(this)) {
                Log.d(TAG, "Copy database to your phone");
            }
        } else {
            Log.d(TAG, "File data.sql exitst");
        }
        mDb.openDataBase();
    }


    private void displayFragment() {
        Fragment fragment = new GroceryFragment();
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private void initViews() {

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


}
