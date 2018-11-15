package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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

import com.example.myapplication.R;
import com.example.myapplication.service.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    static DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ImageView mNavHeaderImage;
    private TextView mNavHeaderTitle;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //TODO
        //checkLogin();
        initViews();
        setOnListener();
        firebaseAuthListener();
        activeFragment();

    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            if (mUser != null) {
                Log.d(TAG, "Logout user: " + mUser.getDisplayName());
                alert.setMessage("Are you sure you want to logout?");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(), "User haven't login", Toast.LENGTH_LONG).show();
            }


        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkLogin() {
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void activeFragment() {
        GroceryFragment fragment = new GroceryFragment();
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layout_container_main, fragment);
        fragmentTransaction.commit();

    }


    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        mNavHeaderImage = headerView.findViewById(R.id.nav_header_image);
        mNavHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        updateUserUI();
    }


    private void setOnListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void firebaseAuthListener() {
        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    updateUserUI();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    updateUserUI();
                }
            }
        };

    }

    private void updateUserUI() {
        if (mUser != null) {
            Log.d(TAG, "providers: " + mUser.getProviders().toString() + ", user_name: " + mUser.getDisplayName() + ", user_photo_url: " + mUser.getPhotoUrl() + ", user_email: " + mUser.getEmail());
            mNavHeaderTitle.setText(mUser.getDisplayName());
            mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            Picasso.get()
                    .load(mUser.getPhotoUrl() + "?type=large")
                    .transform(new CircleTransform())
                    .error(R.drawable.ic_user)
                    .into(mNavHeaderImage);
        } else {
            mNavHeaderTitle.setText("Login");
            mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            mNavHeaderImage.setImageResource(R.drawable.ic_user);
            mNavHeaderTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }

}
