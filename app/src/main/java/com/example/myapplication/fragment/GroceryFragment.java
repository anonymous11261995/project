package com.example.myapplication.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.GroceryAdapter;
import com.example.myapplication.entity.ShoppingList;
import com.example.myapplication.service.ShoppingListService;

import java.util.ArrayList;

import static com.example.myapplication.activity.MainActivity.mDrawerLayout;

public class GroceryFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = GroceryFragment.class.getSimpleName();

    ImageView mButtonAddNewList;
    RecyclerView mRecyclerView;
    GroceryAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initViews();
        setUpRecyclerView();
        setOnListener();


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {


    }

    private void initViews() {
        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mButtonAddNewList = getView().findViewById(R.id.btn_add_new_list);
        mRecyclerView = getView().findViewById(R.id.recycler_view);

    }

    private void setOnListener() {


    }

    private void setUpRecyclerView() {
        ShoppingListService service = new ShoppingListService(getContext());

        ArrayList<ShoppingList> data =  service.getAllShoppingList();
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GroceryAdapter(getContext(), data);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new GroceryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShoppingList object, int position) {
                ShoppingListFragment fragment = new ShoppingListFragment();
                fragment.setShoppingList(object);
                activeFragment(fragment);
            }
        });

    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
