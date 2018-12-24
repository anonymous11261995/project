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
import com.example.myapplication.utils.DefinitionSchema;

import java.util.ArrayList;


/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class ProductsFragment extends Fragment implements DefinitionSchema, View.OnClickListener,
        PopupMenu.OnMenuItemClickListener {
    private static final String TAG = ProductsFragment.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    ImageView mImageBack, mImageMenu, mImageShowLayoutAdd, mImageHideLayoutAdd, mImageInputAdd;
    TextView mTextNameList;
    AutoCompleteTextView mAutoCompleteTextView;
    ConstraintLayout mLayoutAddItem, mLayoutHeader;
    public static TextView mTextEmpty;
    ProductsAdapter mAdapter;
    private GroceryService mShoppingListService;
    private ProductService mProductService;
    private RecyclerView mRecyclerView;
    private Grocery mGrocery;

    PrefManager mPrefManager;


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
        mShoppingListService = new GroceryService(getContext());
        mProductService = new ProductService(getContext());
        initViews();
        initAutoCompleteTextView();
        setOnListener();
        hideSoftKeyBoard();
        initRecyclerView();

    }


    private void initViews() {
        mImageBack = getView().findViewById(R.id.image_back_screen);
        mImageMenu = getView().findViewById(R.id.image_menu);
        mImageShowLayoutAdd = getView().findViewById(R.id.image_show_layout_add);
        mImageHideLayoutAdd = getView().findViewById(R.id.image_hide_layout_add);
        mImageInputAdd = getView().findViewById(R.id.image_input_add);
        mTextNameList = getView().findViewById(R.id.text_name_list);
        mTextEmpty = getView().findViewById(R.id.text_empty_list);
        mAutoCompleteTextView = getView().findViewById(R.id.autocomplete);
        mLayoutAddItem = getView().findViewById(R.id.layout_add_item);
        mLayoutHeader = getView().findViewById(R.id.layout_header);
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        //set status
        mTextNameList.setText(mGrocery.getName());
        mLayoutAddItem.setVisibility(View.GONE);
        mLayoutHeader.setVisibility(View.VISIBLE);
        mImageInputAdd.setVisibility(View.GONE);
    }

    private void setOnListener() {
        mImageBack.setOnClickListener(this);
        mImageMenu.setOnClickListener(this);
        mImageShowLayoutAdd.setOnClickListener(this);
        mImageHideLayoutAdd.setOnClickListener(this);
        mImageInputAdd.setOnClickListener(this);
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    mImageInputAdd.setVisibility(View.GONE);
                } else {
                    mImageInputAdd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<Product> data = mShoppingListService.productShoppingSrceen(mGrocery);
        if (data.size() == 0) {
            mTextEmpty.setVisibility(View.VISIBLE);
        } else {
            mTextEmpty.setVisibility(View.GONE);
        }
        mAdapter = new ProductsAdapter(getActivity(), getContext(), data, mGrocery);
        //drag and swipe
        SwipeAndDragShoppingHelper swipeAndDragHelper = new SwipeAndDragShoppingHelper(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, mAdapter, getContext());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeAndDragHelper);
        mAdapter.setTouchHelper(mItemTouchHelper);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
            }
        }, 1);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    public void buildAgainList() {
        ArrayList<Product> data = mShoppingListService.productShoppingSrceen(mGrocery);
        if (data.size() == 0) {
            mTextEmpty.setVisibility(View.VISIBLE);
        } else {
            mTextEmpty.setVisibility(View.GONE);
        }
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();

    }


    private void initAutoCompleteTextView() {
        ArrayList<String> data = mProductService.getAutoComplete();
        final AutocompleteAdapter adapter = new AutocompleteAdapter(getContext(), R.layout.spinner_dropdown_item_layout, data);
        mAutoCompleteTextView.setAdapter(adapter);
        mAutoCompleteTextView.setThreshold(1);
        adapter.setOnClickListener(new AutocompleteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Log.d(TAG, "Input user: " + text);
                if (!text.equals("")) {
                    addProductToList(text);
                    buildAgainList();
                }

            }

        });
        mAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String text = v.getText().toString().trim();
                    Log.d(TAG, "Input user" + text);
                    if (!text.equals("")) {
                        addProductToList(text);
                        buildAgainList();
                        hideSoftKeyBoard();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
        switch (id) {
            case R.id.image_back_screen:
                activeFragment(new GroceryFragment());
                break;
            case R.id.image_menu:
                PopupMenu popup = new PopupMenu(v.getContext(), mImageMenu);
                popup.inflate(R.menu.products);
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.image_show_layout_add:
                mLayoutAddItem.setVisibility(View.VISIBLE);
                mLayoutHeader.setVisibility(View.GONE);
                mAutoCompleteTextView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mAutoCompleteTextView, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.image_hide_layout_add:
                mLayoutHeader.setVisibility(View.VISIBLE);
                mLayoutAddItem.setVisibility(View.GONE);
                break;
            case R.id.image_input_add:
                String text = mAutoCompleteTextView.getText().toString();
                addProductToList(text);
                buildAgainList();
                break;
            default:
                break;

        }

    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        final ArrayList<Product> productAll = mShoppingListService.productShopping(mGrocery);
        final ArrayList<Product> productNoSnooze = mShoppingListService.productShoppingSrceen(mGrocery);
        CustomDialog customDialog = new CustomDialog(getContext());
        switch (menuItem.getItemId()) {
            case R.id.action_move_copy_items:

                return true;
            case R.id.action_delete_all:
                customDialog.onCreate(getString(R.string.dialog_message_confirm_delete_all_item)
                        , getString(R.string.abc_delete), getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.clearAllProduct(productNoSnooze);
                        buildAgainList();
                    }

                });
                return true;
            case R.id.action_check_all:
                //Log.d(TAG, "Checked all: " + mGrocery.getName());
                mShoppingListService.checkAll(productNoSnooze);
                buildAgainList();
                return true;
            case R.id.action_uncheck_all:
                customDialog.onCreate(getString(R.string.dialog_message_confirm_uncheck_all_item),
                        getString(R.string.abc_confirm), getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.unCheckAll(productNoSnooze);
                        buildAgainList();
                    }

                });
                return true;
            default:
                return true;
        }
    }


    private Product addProductToList(String text) {
        mAutoCompleteTextView.setText("");
        Product product = mShoppingListService.addProductToShopping(text, mGrocery);
        return product;
    }


    public void setShoppingList(Grocery grocery) {
        this.mGrocery = grocery;
    }
}
