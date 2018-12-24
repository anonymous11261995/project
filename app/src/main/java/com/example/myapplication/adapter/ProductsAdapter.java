package com.example.myapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


import com.example.myapplication.helper.SwipeAndDragShoppingHelper;

/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeAndDragShoppingHelper.ActionCompletionContract {
    private static final String TAG = ProductsAdapter.class.getSimpleName();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

    }

    @Override
    public void onViewSwiped(int position, int direction) {

    }
}
