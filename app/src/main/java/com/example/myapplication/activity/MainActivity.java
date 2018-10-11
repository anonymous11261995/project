package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.GroceryListAdapter;
import com.example.myapplication.model.GroceryList;
import com.example.myapplication.transform.CircleTransform;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private NavigationView mNavigationView;
    private ImageView mNavHeaderImage;
    private TextView mNavHeaderTitle;

    private GroceryListAdapter mAdapter;
    // private NoteAdapter mAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groceryListRef = db.collection("GroceryList");
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initViews();
        setUpRecyclerView();
        setOnListener();
        firebaseAuthListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_list);
        buttonAddNote.setOnClickListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        mNavHeaderImage = headerView.findViewById(R.id.nav_header_image);
        mNavHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        updateUserUI();
    }

    private void setUpRecyclerView() {
        Query query = groceryListRef.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<GroceryList> options = new FirestoreRecyclerOptions.Builder<GroceryList>()
                .setQuery(query, GroceryList.class)
                .build();
        mAdapter = new GroceryListAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setOnListener() {
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void firebaseAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
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
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "providers: " + user.getProviders().toString() + ", user_name: " + user.getDisplayName() + ", user_photo_url: " + user.getPhotoUrl() + ", user_email: " + user.getEmail());
            mNavHeaderTitle.setText(user.getDisplayName());
            mNavigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            Picasso.get()
                    .load(user.getPhotoUrl() + "?type=large")
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_add_list:
                Toast.makeText(this, "Add new list", Toast.LENGTH_LONG).show();
                final View alertView;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                alertView = inflater.inflate(R.layout.dialog_create, null);
                TextView titleView = alertView.findViewById(R.id.dialog_create_title);
                titleView.setText("Create a list");
                builder.setView(alertView);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editView = alertView.findViewById(R.id.dialog_create_content);
                        String name = editView.getText().toString().trim();
                        if (name.trim().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please fill a name of list", Toast.LENGTH_LONG).show();
                        } else {
                            saveList(name);
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:
                break;
        }

    }

    private void saveList(String name) {
        GroceryList list = new GroceryList();
        Date now = new Date();
        list.setCreated(now);
        list.setName(name);
        list.setUserID(mUser.getEmail());
        list.setShared(new ArrayList<String>());
        String id = mUser.getEmail() + "-" + name + "-" + now.getTime();

        try {
            list.setId(URLDecoder.decode(id,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        groceryListRef.add(list);
        mAdapter.notifyDataSetChanged();

    }
}
