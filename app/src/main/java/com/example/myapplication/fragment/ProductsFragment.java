package com.example.myapplication.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.adapter.AutocompleteAdapter;
import com.example.myapplication.adapter.ProductsAdapter;
import com.example.myapplication.dialog.CustomDialog;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.helper.PrefManager;
import com.example.myapplication.helper.SwipeAndDragShoppingHelper;
import com.example.myapplication.service.GroceryService;
import com.example.myapplication.service.ProductService;

import java.util.ArrayList;


/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class ProductsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProductsFragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Crashlytics.log(Log.DEBUG, TAG, "onActivityCreated fragment");
        initViews();
        initAutoCompleteTextView();
        setOnListener();
        hideSoftKeyBoard();
        initRecyclerView();

    }


    private void initViews() {

    }

    private void setOnListener() {

    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void initRecyclerView() {


    }


    private void initAutoCompleteTextView() {

    }


    @Override
    public void onClick(View v) {


    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
