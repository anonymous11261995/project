package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.GroceryListAdapter;
import com.example.myapplication.model.GroceryList;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.activity.MainActivity.mDrawerLayout;

public class GroceryListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = GroceryListFragment.class.getSimpleName();

    private FloatingActionButton buttonAddList;

    private GroceryListAdapter mAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference groceryListRef = db.collection("GroceryList");
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery, container, false);
        Log.d(TAG, "Load fragment shopping list");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        initViews();
        setUpRecyclerView();
        setOnListener();


    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_add_list:
                Toast.makeText(getContext(), "Add new list", Toast.LENGTH_LONG).show();
                final View alertView;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                alertView = inflater.inflate(R.layout.view_dialog_create, null);
                TextView titleView = alertView.findViewById(R.id.dialog_create_title);
                titleView.setText("Create a list");
                builder.setView(alertView);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editView = alertView.findViewById(R.id.dialog_create_content);
                        String name = editView.getText().toString().trim();
                        if (name.trim().isEmpty()) {
                            Toast.makeText(getContext(), "Please fill a name of list", Toast.LENGTH_LONG).show();
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

    private void initViews() {
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        buttonAddList = getView().findViewById(R.id.button_add_list);
    }

    private void setOnListener() {
        buttonAddList.setOnClickListener(this);

    }

    private void setUpRecyclerView() {
        Query query = groceryListRef.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<GroceryList> options = new FirestoreRecyclerOptions.Builder<GroceryList>()
                .setQuery(query, GroceryList.class)
                .build();
        mAdapter = new GroceryListAdapter(options);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    private void saveList(String name) {
        GroceryList list = new GroceryList();
        Date now = new Date();
        list.setCreated(now);
        list.setName(name);
        list.setUserID(mUser.getEmail());
        list.setShared(new ArrayList<String>());
        String id = mUser.getEmail() + "-" + name + "-" + now.getTime();

        try {
            list.setId(URLDecoder.decode(id, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        groceryListRef.add(list);
        mAdapter.notifyDataSetChanged();

    }
}
