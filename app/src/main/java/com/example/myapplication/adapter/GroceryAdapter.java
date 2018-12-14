package com.example.myapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.entity.ShoppingList;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemClickListener listener;
    private Context mContext;
    private ArrayList<ShoppingList> mData;

    public GroceryAdapter(Context context, ArrayList<ShoppingList> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grocery, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ItemHolder holder = (ItemHolder) viewHolder;
        ShoppingList object = mData.get(i);
        holder.itemName.setText(object.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemStatus;
        private ConstraintLayout layoutItem;

        private ItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_name);
            itemStatus = itemView.findViewById(R.id.text_status);
            layoutItem = itemView.findViewById(R.id.layout_item);
            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(mData.get(position), position);
                    }

                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick(ShoppingList object, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
