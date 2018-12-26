package com.example.myapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.example.myapplication.R;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.helper.SwipeAndDragShoppingHelper;
import com.example.myapplication.service.GroceryService;
import com.example.myapplication.service.ProductService;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeAndDragShoppingHelper.ActionCompletionContract {
    private static final String TAG = ProductsAdapter.class.getSimpleName();
    private Context mContext;
    private FragmentActivity mActivity;
    private ArrayList<Product> mListItems;
    private ProductService mProductService;

    public ProductsAdapter(FragmentActivity activity, Context context, ArrayList<Product> data) {
        this.mActivity = activity;
        this.mContext = context;
        this.mListItems = data;
        mProductService = new ProductService(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_product, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemHolder holder = (ItemHolder) viewHolder;
        Product object = mListItems.get(i);
        holder.itemName.setText(object.getName());
        if (object.getQuantity() > 1) {
            holder.itemQuantity.setText(object.getQuantity());
        } else {
            holder.itemQuantity.setVisibility(View.GONE);
        }
        //set listenner
//        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

    }

    @Override
    public void onViewSwiped(int position, int direction) {

    }

    public void customNotifyItemInserted(int postion, Product product) {
        mListItems.add(0, product);
        notifyItemInserted(postion);
    }


    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemQuantity;
        private ConstraintLayout layoutItem;
        private CheckBox itemCheckbox;

        private ItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_name);
            itemQuantity = itemView.findViewById(R.id.text_quantity);
            layoutItem = itemView.findViewById(R.id.layout_item);
            itemCheckbox = itemView.findViewById(R.id.checkbox);

            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

    }


}
