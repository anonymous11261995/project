package com.example.myapplication.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.GroceryAdapter;
import com.example.myapplication.dialog.DialogCustomLayout;
import com.example.myapplication.dialog.DialogRename;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.helper.SwipeDeleteHelper;
import com.example.myapplication.service.GroceryService;

import java.util.ArrayList;

import static com.example.myapplication.activity.MainActivity.mDrawerLayout;

public class GroceryFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = GroceryFragment.class.getSimpleName();

    ImageView mButtonAddNewList;
    RecyclerView mRecyclerView;
    GroceryAdapter mAdapter;
    GroceryService mGroceryService;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGroceryService = new GroceryService(getContext());
        setHasOptionsMenu(true);
        initViews();
        setUpRecyclerView();
        setOnListener();
        hideSoftKeyBoard();


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
        switch (v.getId()) {
            case R.id.btn_add_new_list:
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_list);
                dialogCustomLayout.onCreate(message, "");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mGroceryService.checkBeforeUpdateList(text)) {
                            mGroceryService.createList(text);
                            mAdapter.customNotifyItemInserted();
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });
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

        mButtonAddNewList = getView().findViewById(R.id.btn_add_new_list);
        mRecyclerView = getView().findViewById(R.id.recycler_view);

    }

    private void setOnListener() {
        mButtonAddNewList.setOnClickListener(this);


    }

    private void setUpRecyclerView() {
        ArrayList<Grocery> data = mGroceryService.getAllShoppingList();
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GroceryAdapter(getActivity(), getContext(), data);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new GroceryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Grocery object, int position) {
                activeFragment(new ProductsFragment());
            }

            @Override
            public void onItemLongClick(final Grocery object, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(object.getName())
                        .setItems(R.array.list_function, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        break;
                                    case 1:
                                        Log.d(TAG, "case 1");
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        DialogRename dialogRename = new DialogRename(getContext());
                                        dialogRename.show(object.getName());
                                        dialogRename.setListener(new DialogRename.OnClickListener() {
                                            @Override
                                            public void onClickPositiveButton(DialogInterface dialog, String name) {

                                            }
                                        });
                                        break;
                                    case 4:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        SwipeDeleteHelper swipeAndDragHelper = new SwipeDeleteHelper(mAdapter, getContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
