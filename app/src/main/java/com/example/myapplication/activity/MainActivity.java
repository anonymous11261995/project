package com.example.myapplication.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.fragment.GroceryFragment;
import com.example.myapplication.fragment.ShoppingListFragment;
import com.example.myapplication.helper.DatabaseHelper;
import com.example.myapplication.helper.PrefManager;
import com.example.myapplication.service.CircleTransform;
import com.example.myapplication.service.GenericService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static DatabaseHelper mDb;
    public static NavigationView mNavigationView;
    public static DrawerLayout mDrawerLayout;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefManager = new PrefManager(this);
        mDb = new DatabaseHelper(this);
        initDatabase();
        initViews();
        setOnListener();
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
        Fragment fragment = new ShoppingListFragment();
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private void initViews() {
        mNavigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
    }

    private void setOnListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;
        switch (id) {
        }
        return true;

    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


}
