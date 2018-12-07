package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;


import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.dialog.CustomDialog;
import com.example.myapplication.entity.Category;
import com.example.myapplication.entity.Product;
import com.example.myapplication.entity.ShoppingList;
import com.example.myapplication.fragment.ProductDetailFragment;
import com.example.myapplication.fragment.ShoppingListFragment;
import com.example.myapplication.helper.SwipeAndDragShoppingHelper;
import com.example.myapplication.holder.HeaderListProductHolder;
import com.example.myapplication.holder.ProductCheckedHeaderHolder;
import com.example.myapplication.holder.ProductCheckedItemHolder;
import com.example.myapplication.holder.ProductItemHolder;
import com.example.myapplication.service.ProductService;
import com.example.myapplication.service.ShoppingListService;
import com.example.myapplication.utils.DefinitionSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DefinitionSchema, SwipeAndDragShoppingHelper.ActionCompletionContract {
    private static final String TAG = ShoppingListAdapter.class.getSimpleName();
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();
    private static final int PENDING_TIMEOUT = AppConfig.PENDING_TIMEOUT; // 3sec
    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private static final int ITEM_CHECKED_TYPE = 3;
    private static final int HEADER_CHECKED_TYPE = 4;
    private Context mContext;
    private FragmentActivity mActivity;
    private ShoppingListService mShoppingListService;
    private ProductService mProductService;
    private ItemTouchHelper touchHelper;
    private ArrayList<Product> mData = new ArrayList<>();
    private ShoppingList mShoppingList;

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    private HashMap<String, Runnable> pendingRemove = new HashMap<>();
    private HashMap<String, Runnable> pendingSnooze = new HashMap<>();

    private ArrayList<String> mItemsPendingRemove = new ArrayList<>();
    private ArrayList<String> mItemsPendingSnooze = new ArrayList<>();


    public ShoppingListAdapter(FragmentActivity activity, Context context, ArrayList<Product> data, ShoppingList shoppingList) {
        this.mContext = context;
        this.mActivity = activity;
        this.mShoppingListService = new ShoppingListService(context);
        this.mProductService = new ProductService(context);
        this.mData.addAll(data);
        this.mShoppingList = shoppingList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shopping_normal, parent, false);
                return new ProductItemHolder(view);
            }
            case HEADER_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_list_product, parent, false);
                return new HeaderListProductHolder(view);
            }
            case HEADER_CHECKED_TYPE: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_product_checked, parent, false);
                return new ProductCheckedHeaderHolder(view);
            }
            default: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_shopping_checked, parent, false);
                return new ProductCheckedItemHolder(view);
            }
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ProductItemHolder) {
                ProductItemHolder vh = (ProductItemHolder) holder;
                onBindItemViewHolder(vh, position);

            } else if (holder instanceof HeaderListProductHolder) {
                HeaderListProductHolder vh = (HeaderListProductHolder) holder;
                onBindHeaderViewHolder(vh, position);

            } else if (holder instanceof ProductCheckedItemHolder) {
                ProductCheckedItemHolder vh = (ProductCheckedItemHolder) holder;
                onBindItemCheckedViewHolder(vh, position);

            } else if (holder instanceof ProductCheckedHeaderHolder) {
                ProductCheckedHeaderHolder vh = (ProductCheckedHeaderHolder) holder;
                onBindHeaderCheckedViewHolder(vh);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("EmptyMethod")
    private void onBindHeaderCheckedViewHolder(ProductCheckedHeaderHolder holder) {
        int width = mContext.getResources().getDisplayMetrics().widthPixels / 2;
        Log.d("phone", "width: " + width);
        holder.mButtonUnCheckAll.setWidth(width - 30);
        holder.mButtonDeleteAll.setWidth(width - 30);
        //event
        holder.mButtonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.onCreate(mContext.getString(R.string.dialog_message_confirm_delete_all_item)
                        , mContext.getString(R.string.abc_delete), mContext.getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.clearAllProductChecked(mData);
                        buildViewShoppingList();
                    }

                });

            }
        });

        holder.mButtonUnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.onCreate(mContext.getString(R.string.dialog_message_confirm_uncheck_all_item),
                        mContext.getString(R.string.abc_confirm), mContext.getString(R.string.abc_cancel));
                customDialog.setListener(new CustomDialog.OnClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        mShoppingListService.unCheckAll(mData);
                        buildViewShoppingList();
                    }

                });

            }
        });


    }

    private void onBindHeaderViewHolder(HeaderListProductHolder holder, int position) {
        Category category = mProductService.getCategoryOfProduct(mData.get(position));
        holder.headerName.setText(category.getName());
    }

    private void onBindItemCheckedViewHolder(ProductCheckedItemHolder holder, int position) {
        final Product product = mData.get(position);
        holder.itemLayout.setVisibility(View.VISIBLE);
        holder.itemContent.setText(product.getContent());
        holder.itemContent.setPaintFlags(holder.itemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        String description = mProductService.getDescription(product);
        if (!description.equals("")) {
            holder.itemDescription.setVisibility(View.VISIBLE);
            holder.itemDescription.setText(description);
        } else {
            holder.itemDescription.setVisibility(View.GONE);
        }
        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                productDetailFragment.setProduct(product);
                activeFragment(productDetailFragment);

            }
        });
        holder.itemCheckBox.setChecked(true);
        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                if (product.isChecked()) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            product.setChecked(false);
                            product.setLastChecked(new Date());
                            mProductService.updateProduct(product);
                            buildViewShoppingList();
                        }
                    }, AppConfig.DELAY_EFFECT);
                }
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void onBindItemViewHolder(ProductItemHolder viewHolder, int pos) {
        final ProductItemHolder holder = viewHolder;
        final Product product = mData.get(pos);
        if (mItemsPendingRemove.contains(product.getId())) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.itemLayoutSnoozed.setVisibility(View.GONE);
            holder.itemLayoutDeleted.setVisibility(View.VISIBLE);
            holder.itemUndoDeleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    undoDeleted(product);
                }
            });
        } else if (mItemsPendingSnooze.contains(product.getId())) {
            holder.itemLayout.setVisibility(View.GONE);
            holder.itemLayoutDeleted.setVisibility(View.GONE);
            holder.itemLayoutSnoozed.setVisibility(View.VISIBLE);
            holder.itemUndoSnoozed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    undoSnoozed(product);
                }
            });
        } else {
            holder.itemLayoutSnoozed.setVisibility(View.GONE);
            holder.itemLayoutDeleted.setVisibility(View.GONE);
            holder.itemLayout.setVisibility(View.VISIBLE);
            holder.itemContent.setText(product.getContent());
            holder.itemCheckBox.setChecked(false);
            String description = mProductService.getDescription(product);
            if (!description.equals("")) {
                holder.itemDescription.setVisibility(View.VISIBLE);
                holder.itemDescription.setText(description);
            } else {
                holder.itemDescription.setVisibility(View.GONE);
            }

            holder.itemMove.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    touchHelper.startDrag(holder);
                    return false;
                }
            });
            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("abcd", "kdjfkd");
                }
            });

            holder.group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                    productDetailFragment.setProduct(product);
                    activeFragment(productDetailFragment);

                }
            });
            holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_effect));
                    if (!product.isChecked()) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                product.setChecked(true);
                                product.setLastChecked(new Date());
                                mProductService.updateProduct(product);
                                buildViewShoppingList();
                            }
                        }, AppConfig.DELAY_EFFECT);
                    }
                }
            });
        }
    }

    private void buildViewShoppingList() {
        mData.clear();
        ArrayList<Product> products = mShoppingListService.productShoppingSrceen(mShoppingList);
        mData.addAll(products);
        if (mData.size() == 0) {
            ShoppingListFragment.mGuide.setVisibility(View.VISIBLE);
        } else {
            ShoppingListFragment.mGuide.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
        buidInfoShoppingList();
    }

    @Override
    public int getItemViewType(int position) {
        //Log.d("abc", "size: " + mData.size());

        if (mData.size() == 0)
            return super.getItemViewType(position);

        Product product = mData.get(position);

        if (product.isChecked() && TextUtils.isEmpty(product.getName())) {
            return HEADER_CHECKED_TYPE;
        }
        if (product.isChecked() && !TextUtils.isEmpty(product.getName())) {
            return ITEM_CHECKED_TYPE;
        }
        //Log.d("Build list", "Position: " + position + " name: " + mData.get(position).getName());
        if (TextUtils.isEmpty(product.getName())) {
            return HEADER_TYPE;
        }
        return ITEM_TYPE;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Log.v(TAG, "Log move position: " + oldPosition + " to " + newPosition);
        int limit;
        int productCheckedIndex = -1;
        for (int i = 0; i < mData.size(); i++) {
            Product product = mData.get(i);
            if (product.isChecked()) {
                productCheckedIndex = i;
                break;
            }
        }
        if (productCheckedIndex != -1) limit = productCheckedIndex;
        else limit = mData.size();
        if (oldPosition < limit && newPosition < limit && newPosition > 0) {
            Product product = mData.get(oldPosition);
            product.setModified(new Date());
            mProductService.updateProduct(product);
            Log.d(TAG, "name: " + product.getName() + " modfield: " + product.getModified());
            if (oldPosition < newPosition) {
                for (int i = oldPosition; i < newPosition; i++) {
                    Collections.swap(mData, i, i + 1);
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    Collections.swap(mData, i, i - 1);
                }
            }
            notifyItemMoved(oldPosition, newPosition);
            convertListToDatabase();
        }
    }

    @Override
    public void onViewSwiped(int pos, int direction) {
        try{
            final int position = pos;
            for (String key : mItemsPendingRemove) {
                Product product1 = mProductService.findProductById(key);
                removeProduct(product1);

            }
            for (String key : mItemsPendingSnooze) {
                Product product1 = mProductService.findProductById(key);
                snoozeProduct(product1);

            }
            buildViewShoppingList();
            final Product product = mData.get(position);
            if (direction == ItemTouchHelper.RIGHT) {
                if (!mItemsPendingRemove.contains(product.getId())) {
                    mItemsPendingRemove.add(product.getId());
                    Runnable pendingRemoveRunnable = new Runnable() {
                        @Override
                        public void run() {
                            removeProduct(product);
                            buildViewShoppingList();
                        }
                    };
                    handler.postDelayed(pendingRemoveRunnable, PENDING_TIMEOUT);
                    pendingRemove.put(product.getId(), pendingRemoveRunnable);

                }

            } else {
                if (!mItemsPendingSnooze.contains(product.getId())) {
                    mItemsPendingSnooze.add(product.getId());
                    Runnable pendingSnoozeRunnable = new Runnable() {
                        @Override
                        public void run() {
                            snoozeProduct(product);
                            buildViewShoppingList();
                        }
                    };
                    handler.postDelayed(pendingSnoozeRunnable, PENDING_TIMEOUT);
                    pendingSnooze.put(product.getId(), pendingSnoozeRunnable);

                }
            }
        }
        catch (Exception e){
            Log.e("Error","swipe: " + e.getMessage());
        }

    }

    private void undoDeleted(Product product) {
        Runnable pendingRemovalRunnable = pendingRemove.get(product.getId());
        pendingRemove.remove(product.getId());
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingRemove.remove(product.getId());
        buildViewShoppingList();

    }

    private void undoSnoozed(Product product) {
        Runnable pendingRemovalRunnable = pendingSnooze.get(product.getId());
        pendingSnooze.remove(product.getId());
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        mItemsPendingSnooze.remove(product.getId());
        buildViewShoppingList();

    }


    private void snoozeProduct(Product product) {
        try{
            if (mItemsPendingSnooze.contains(product.getId())) {
                mItemsPendingSnooze.remove(product.getId());
            }
            product.setHide(true);
            mProductService.updateProduct(product);
            pendingSnooze.remove(product.getId());
        }
        catch (Exception e){

        }

    }

    private void removeProduct(Product product) {
        try{
            if (mItemsPendingRemove.contains(product.getId())) {
                mItemsPendingRemove.remove(product.getId());
            }
            mProductService.deleteProductFromList(product);
            pendingRemove.remove(product.getId());
        }
        catch (Exception e){
            Log.e("Error", "swipe: " + e.getMessage());
        }

    }


    private void activeFragment(Fragment fragment) {
        FragmentManager mFragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void convertListToDatabase() {
        int k = 0;
        Category categoryUpdate = null;
        for (Product p : mData) {
            if (p.getCategory().getId() == null) {
                break;
            }
            if (p.getName() == null) {
                k = 0;
                categoryUpdate = p.getCategory();
            } else {
                p.setOrderInGroup(k);
                p.setCategory(categoryUpdate);
                mProductService.updateProduct(p);
                k++;
            }

        }
    }

    private void buidInfoShoppingList() {
        Double priceCart = 0.0, priceList = 0.0;
        int itemCart = 0, itemList = 0;
        for (Product product : mData) {
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
            ShoppingListFragment.mLayoutInfo.setVisibility(View.GONE);

        } else {
            ShoppingListFragment.mLayoutInfo.setVisibility(View.VISIBLE);
            ShoppingListFragment.mCartPriceInfo.setText(priceCartText);
            ShoppingListFragment.mCartCountInfo.setText(countCartText);
            ShoppingListFragment.mListCountInfo.setText(countListText);
            ShoppingListFragment.mListPriceInfo.setText(priceListText);

        }
    }

    public ArrayList<Product> getData() {
        return mData;
    }

    public void setData(ArrayList<Product> data) {
        mData.clear();
        mData.addAll(data);
    }
}
