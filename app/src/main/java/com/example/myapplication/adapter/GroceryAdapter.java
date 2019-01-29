package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.helper.SwipeDeleteHelper;
import com.example.myapplication.service.GroceryService;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeDeleteHelper.ActionCompletionContract {
    private OnItemClickListener listener;
    private Context mContext;
    private FragmentActivity mActivity;
    private ArrayList<Grocery> mListItems;
    private GroceryService mGroceryService;

    public GroceryAdapter(FragmentActivity activity, Context context, ArrayList<Grocery> data) {
        this.mActivity = activity;
        this.mContext = context;
        this.mListItems = data;
        mGroceryService = new GroceryService(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_grocery, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        Grocery object = mListItems.get(position);
        holder.itemName.setText(object.getName());
        ArrayList<Product> products = object.getProducts();
        int numberItemBought = 0;
        for (Product product : products) {
            if (product.isPurchased()) {
                numberItemBought++;
            }
        }
        String status = String.valueOf(numberItemBought) + "/" + String.valueOf(products.size());
        holder.itemStatus.setText(status);
        //event
        holder.imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemMenuClick(mListItems.get(position), position);
                }
            }
        });

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(mListItems.get(position), position);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }


    public void customNotifyDataSetChanged() {
        ArrayList<Grocery> data = mGroceryService.getAllShoppingList();
        mListItems.clear();
        mListItems.addAll(data);
        notifyDataSetChanged();
    }


    @Override
    public void onViewSwiped(int position) {
        Grocery grocery = mListItems.get(position);
        deleteItem(grocery);

    }

    public void deleteItem(final Grocery grocery) {
        final int index = mListItems.indexOf(grocery);
        mListItems.remove(index);
        notifyItemRemoved(index);
        mGroceryService.delete(grocery);
        View view = mActivity.findViewById(R.id.layout_fragment);
        Snackbar snackbar = Snackbar.make(view, "1 item deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoDelete(grocery, index);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }


    private void undoDelete(Grocery grocery, int position) {
        mListItems.add(position, grocery);
        notifyItemInserted(position);
        mGroceryService.create(grocery);
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemStatus;
        private ImageView imageMenu;
        private LinearLayout layoutItem;


        private ItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_name);
            itemStatus = itemView.findViewById(R.id.text_status);
            layoutItem = itemView.findViewById(R.id.layout_item);
            imageMenu = itemView.findViewById(R.id.image_menu);

        }

    }

    public interface OnItemClickListener {
        void onItemClick(Grocery object, int position);

        void onItemMenuClick(Grocery grocery, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
