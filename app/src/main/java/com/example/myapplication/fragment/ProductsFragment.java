package com.example.myapplication.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.adapter.ShoppingListAdapter;
import com.example.myapplication.dialog.CustomDialog;
import com.example.myapplication.dialog.DialogCustomLayout;
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
public class ProductsFragment extends Fragment implements DefinitionSchema, View.OnClickListener, TextView.OnEditorActionListener,
        PopupMenu.OnMenuItemClickListener, AdapterView.OnItemClickListener, TextWatcher {
    private static final String TAG = ProductsFragment.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();

    public static TextView mCartPriceInfo;
    public static TextView mListPriceInfo;
    public static TextView mCartCountInfo;
    public static TextView mListCountInfo;
    public static ConstraintLayout mLayoutInfo;
    public static TextView mGuide;
    ShoppingListAdapter mAdapter;
    private GroceryService mShoppingListService;
    private ProductService mProductService;
    private ArrayList<String> mAutocompleteList;
    private RecyclerView mRecyclerView;
    private Grocery mGrocery;

    PrefManager mPrefManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Crashlytics.log(Log.DEBUG, TAG, "onCreate fragment");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //set toolbar appearance
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setTitle(mGrocery.getName());

//        toolbar.setNavigationIcon(getResources().getDrawable(android.R.drawable.ic_));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("cek", "home selected");
//            }
//        });
        setHasOptionsMenu(true);
        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
       activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Crashlytics.log(Log.DEBUG, TAG, "onActivityCreated fragment");
        mShoppingListService = new GroceryService(getContext());
        mProductService = new ProductService(getContext());
        MainActivity.mNavigationView.getMenu().getItem(0).setChecked(true);
        initViews();
        //initAutoCompleteTextView();
        setOnListener();
        hideSoftKeyBoard();
        initRecyclerView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "action click");
        switch (item.getItemId()) {
            case android.R.id.home:
                activeFragment(new GroceryFragment());
                Log.d(TAG, "action click back");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.products, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void hideSoftKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void initViews() {

        mGuide = getView().findViewById(R.id.guide_shopping_list);
        mCartPriceInfo = getView().findViewById(R.id.shopping_list_cart_info);
        mListPriceInfo = getView().findViewById(R.id.shopping_list_info);
        mLayoutInfo = getView().findViewById(R.id.layout_info);
        mListCountInfo = getView().findViewById(R.id.shopping_list_info_total);
        mCartCountInfo = getView().findViewById(R.id.shopping_list_cart_info_total);

    }


    private void initRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.recycler_view_shopping_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(AppConfig.ITEM_CACHE_LIST);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<Product> data = mShoppingListService.productShoppingSrceen(mGrocery);
        if (data.size() == 0) {
            mGuide.setVisibility(View.VISIBLE);
        } else {
            mGuide.setVisibility(View.GONE);
        }
        mAdapter = new ShoppingListAdapter(getActivity(), getContext(), data, mGrocery);
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
        buidInfoShoppingList(data);

    }

    public void buildAgainList() {
        ArrayList<Product> data = mShoppingListService.productShoppingSrceen(mGrocery);
        if (data.size() == 0) {
            mGuide.setVisibility(View.VISIBLE);
        } else {
            mGuide.setVisibility(View.GONE);
        }
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
        buidInfoShoppingList(data);
    }

    private void setOnListener() {
    }

//    private void initAutoCompleteTextView() {
//        mAutocompleteList = mProductService.getAutoComplete();
//        final AutocompleteAdapter adapter = new AutocompleteAdapter(getContext(), R.layout.spinner_dropdown_item_layout, mAutocompleteList);
//        mAutoCompleteTextView.setAdapter(adapter);
//        mAutoCompleteTextView.setThreshold(1);
//        adapter.setOnClickListener(new AutocompleteAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(String text) {
//                Log.d(TAG, "Input user: " + text);
//                if (!text.equals("")) {
//                    addProductToList(text);
//                    buildAgainList();
//                }
//
//            }
//
//        });
//    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_effect));
        switch (id) {

//            case R.id.menu_image:
//                ArrayList<Product> productAll = mShoppingListService.productShopping(mGrocery);
//                ArrayList<Product> products = mAdapter.getData();
//                ArrayList<Product> productsSnooze = mShoppingListService.productsSnooze(mGrocery);
//                ArrayList<Product> productsChecked = mProductService.productShoppingChecked(mGrocery);
//                ArrayList<Product> productsUnChecked = mProductService.productShoppingUnChecked(mGrocery);
//                boolean emptyChecked = true;
//                for(Product product :productsChecked){
//                    if(!product.isHide() && product.isChecked() && product.getName() != null){
//                        emptyChecked = false;
//                    }
//                }
//                boolean emptyUnChecked = true;
//                for(Product product :productsUnChecked){
//                    if(!product.isHide() && !product.isChecked() && product.getName() != null){
//                        emptyUnChecked = false;
//                    }
//                }
//                PopupMenu popup = new PopupMenu(v.getContext(), mMenuShoppingList);
//                popup.inflate(R.menu.products);
//                popup.setOnMenuItemClickListener(this);
//                // set state for item in menu
//                if (products.size() == 0) {
//                    popup.getMenu().getItem(1).setEnabled(false);
//                }
//                if (emptyUnChecked) {
//                    popup.getMenu().getItem(2).setEnabled(false);
//                }
//                if (emptyChecked) {
//                    popup.getMenu().getItem(3).setEnabled(false);
//                }
//                if (productsSnooze.size() == 0) {
//                    popup.getMenu().getItem(4).setEnabled(false);
//                }
//                if(productAll.size() == 0){
//                    popup.getMenu().getItem(5).setEnabled(false);
//                    popup.getMenu().getItem(6).setEnabled(false);
//                    popup.getMenu().getItem(10).setEnabled(false);
//                }
//                popup.show();
//                break;case R.id.menu_image:
//                ArrayList<Product> productAll = mShoppingListService.productShopping(mGrocery);
//                ArrayList<Product> products = mAdapter.getData();
//                ArrayList<Product> productsSnooze = mShoppingListService.productsSnooze(mGrocery);
//                ArrayList<Product> productsChecked = mProductService.productShoppingChecked(mGrocery);
//                ArrayList<Product> productsUnChecked = mProductService.productShoppingUnChecked(mGrocery);
//                boolean emptyChecked = true;
//                for(Product product :productsChecked){
//                    if(!product.isHide() && product.isChecked() && product.getName() != null){
//                        emptyChecked = false;
//                    }
//                }
//                boolean emptyUnChecked = true;
//                for(Product product :productsUnChecked){
//                    if(!product.isHide() && !product.isChecked() && product.getName() != null){
//                        emptyUnChecked = false;
//                    }
//                }
//                PopupMenu popup = new PopupMenu(v.getContext(), mMenuShoppingList);
//                popup.inflate(R.menu.products);
//                popup.setOnMenuItemClickListener(this);
//                // set state for item in menu
//                if (products.size() == 0) {
//                    popup.getMenu().getItem(1).setEnabled(false);
//                }
//                if (emptyUnChecked) {
//                    popup.getMenu().getItem(2).setEnabled(false);
//                }
//                if (emptyChecked) {
//                    popup.getMenu().getItem(3).setEnabled(false);
//                }
//                if (productsSnooze.size() == 0) {
//                    popup.getMenu().getItem(4).setEnabled(false);
//                }
//                if(productAll.size() == 0){
//                    popup.getMenu().getItem(5).setEnabled(false);
//                    popup.getMenu().getItem(6).setEnabled(false);
//                    popup.getMenu().getItem(10).setEnabled(false);
//                }
//                popup.show();
//                break;
            case R.id.microphone:
                startVoiceInput();
                break;
            case R.id.image_history:
                break;
//            case R.id.input_add:
//                String text = mAutoCompleteTextView.getText().toString();
//                addProductToList(text);
//                buildAgainList();
//                hideSoftKeyBoard();
//                Log.d(TAG, "Add product: " + text);
//                break;
//            case R.id.input_delete:
//                mAutoCompleteTextView.setText("");
//                Log.d(TAG, "Delete product");
//                break;
            default:
                break;

        }

    }


    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        String codeLanguage = mPrefManager.getString(SHARE_PREFERENCES_LANGUAGE_CODE, "en");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, codeLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getResources().getString(R.string.try_saying_something));
        try {
            startActivityForResult(intent, AppConfig.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

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
            case R.id.action_create_list:
                DialogCustomLayout dialogCustomLayout = new DialogCustomLayout(getContext());
                String message = getString(R.string.dialog_message_create_list);
                dialogCustomLayout.onCreate(message, "");
                dialogCustomLayout.setListener(new DialogCustomLayout.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, String text) {
                        if (mShoppingListService.checkBeforeUpdateList(text)) {
                            mShoppingListService.createNewListShopping(text);
                            activeFragment(new ProductsFragment());
                        } else {
                            hideSoftKeyBoard();
                            Toast.makeText(getContext(), getResources().getString(R.string.toast_duplicate_name), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return true;
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
            case R.id.action_show_snooze:

                return true;
            case R.id.action_category_manage:

                return true;
            case R.id.action_manage_list:

                return true;
            case R.id.action_multi_list:
                return true;
            case R.id.action_quick_set_qty:

                return true;
            case R.id.action_shopping_to_pantry:

                return true;

            default:
                return true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString().trim();
        Log.d(TAG, "Input user: " + text);
        if (!text.equals("")) {
            addProductToList(text);
            buildAgainList();
            hideSoftKeyBoard();
        }
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            String text = v.getText().toString().trim();
            Log.d(TAG, "Input user" + text);
            if (!text.equals("")) {
                addProductToList(text);
                buildAgainList();
                Log.d(TAG, "show keyboard");
            } else {
                Log.d(TAG, "Input null");
            }
            return true;
        }
        return false;
    }

    private Product addProductToList(String text) {
        // mAutoCompleteTextView.setText("");
        Product product = mShoppingListService.addProductToShopping(text, mGrocery);
        return product;
    }

    private void buidInfoShoppingList(ArrayList<Product> products) {
        Double priceCart = 0.0, priceList = 0.0;
        int itemCart = 0, itemList = 0;
        for (Product product : products) {
            if (!TextUtils.isEmpty(product.getName())) {
                Double price = (product.getQuantity() * product.getUnitPrice());
                if (product.isChecked()) {
                    itemList++;
                    priceList += price;
                    itemCart++;
                    priceCart += price;
                } else {
                    itemList++;
                    priceList += price;
                }
            }
        }
        String priceCartText = CURRENCY_DEFUALT + mProductService.parserPrice(priceCart);
        String priceListText = CURRENCY_DEFUALT + mProductService.parserPrice(priceList);
        if (priceCart == 0.0) {
            priceCartText = CURRENCY_DEFUALT + "0.0";
        }
        if (priceList == 0.0) {
            priceListText = CURRENCY_DEFUALT + "0.0";
        }
        String countCartText = String.valueOf(itemCart);
        String countListText = String.valueOf(itemList);
        if (itemList == 0.0) {
            Log.d(TAG, "hide info list");
            mLayoutInfo.setVisibility(View.GONE);

        } else {
            mLayoutInfo.setVisibility(View.VISIBLE);
            mCartPriceInfo.setText(priceCartText);
            mCartCountInfo.setText(countCartText);
            mListCountInfo.setText(countListText);
            mListPriceInfo.setText(priceListText);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Log.d(TAG, "on text change: " + charSequence);
        if (charSequence.toString().equals("")) {
            //Log.d(TAG, "Show input type typing");
            //mLayoutInputTypeTypping.setVisibility(View.VISIBLE);
            //mLayoutInputTextUpdate.setVisibility(View.GONE);
        } else {
            //mLayoutInputTypeTypping.setVisibility(View.GONE);
            //mLayoutInputTextUpdate.setVisibility(View.VISIBLE);
            //Log.d(TAG, "Show input text update");
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TAG, "after text change: ");

    }

    public void setShoppingList(Grocery grocery) {
        this.mGrocery = grocery;
    }
}
