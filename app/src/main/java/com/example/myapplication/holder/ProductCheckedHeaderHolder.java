package com.example.myapplication.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;


@SuppressWarnings("CanBeFinal")
public class ProductCheckedHeaderHolder extends RecyclerView.ViewHolder {
    @SuppressWarnings("FieldCanBeLocal")
    public Button mButtonUnCheckAll;
    public Button mButtonDeleteAll;

    public ProductCheckedHeaderHolder(View itemView) {
        super(itemView);
        mButtonDeleteAll = itemView.findViewById(R.id.action_delete_all);
        mButtonUnCheckAll = itemView.findViewById(R.id.action_uncheck_all);
    }
}
