package com.example.myapplication.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapplication.R;


/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ProductItemHolder extends RecyclerView.ViewHolder {
    public TextView itemContent;
    public TextView itemDescription;
    public ConstraintLayout itemMove;
    public CheckBox itemCheckBox;
    public ConstraintLayout group;
    public CardView itemLayout;
    public ConstraintLayout itemLayoutSnoozed;
    public TextView itemUndoSnoozed;
    public ConstraintLayout itemLayoutDeleted;
    public TextView itemUndoDeleted;

    public ProductItemHolder(View itemView) {
        super(itemView);
        itemContent = itemView.findViewById(R.id.product_content);
        itemMove = itemView.findViewById(R.id.product_move);
        itemCheckBox = itemView.findViewById(R.id.product_checkbox);
        itemDescription = itemView.findViewById(R.id.product_description);
        group = itemView.findViewById(R.id.product_constraint);
        itemLayout = itemView.findViewById(R.id.product_layout);
        itemLayoutSnoozed = itemView.findViewById(R.id.layout_snooze);
        itemUndoSnoozed = itemView.findViewById(R.id.text_undo_snoozed);
        itemLayoutDeleted = itemView.findViewById(R.id.layout_deleted);
        itemUndoDeleted = itemView.findViewById(R.id.text_undo_deleted);
    }
}
