package com.example.myapplication.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.service.GroceryService;
import com.example.myapplication.service.ProductService;

import java.util.ArrayList;

/**
 * Created by TienTruong on 7/27/2018.
 */

@SuppressWarnings("CanBeFinal")
public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ProductsAdapter.class.getSimpleName();
    private final int TYPE_ITEM_BOUGHT = 1;
    private final int TYPE_ITEM = 2;
    private Context mContext;
    private FragmentActivity mActivity;
    private ProductService mProductService;
    private Grocery mGrocery;
    // private GroceryService mGroceryService;
    private ArrayList<Product> mListItems;

    public ProductsAdapter(FragmentActivity activity, Context context, ArrayList<Product> data, Grocery grocery) {
        this.mActivity = activity;
        this.mContext = context;
        this.mListItems = data;
        mProductService = new ProductService(context);
        mGrocery = grocery;
        // mGroceryService = new GroceryService(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i) {
            case TYPE_ITEM:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.adapter_item_product, viewGroup, false);
                return new ItemHolder(view);
            case TYPE_ITEM_BOUGHT:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.adapter_item_product_bought, viewGroup, false);
                return new ItemBoughtHolder(view);
            default:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.adapter_item_product, viewGroup, false);
                return new ItemHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) viewHolder;
            onBindItem(holder, position);

        } else {
            ItemBoughtHolder holder = (ItemBoughtHolder) viewHolder;
            onBindItemBought(holder, position);
        }


    }

    private void onBindItem(ItemHolder holder, final int position) {
        final Product object = mListItems.get(position);
        holder.itemName.setText(object.getName());
        if (object.getQuantity() == 0) {
            holder.itemQuantity.setVisibility(View.GONE);

        } else {
            holder.itemQuantity.setVisibility(View.VISIBLE);
            holder.itemQuantity.setText(String.valueOf(object.getQuantity()));
        }
        if (TextUtils.isEmpty(object.getUnit())) {
            holder.itemUnit.setVisibility(View.GONE);
        } else {
            holder.itemUnit.setVisibility(View.VISIBLE);
            holder.itemUnit.setText(object.getUnit());
        }
        if (TextUtils.isEmpty(object.getNote())) {
            holder.itemNote.setVisibility(View.GONE);
        } else {
            holder.itemNote.setVisibility(View.VISIBLE);
            holder.itemNote.setText(object.getNote());
        }
        TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground});
        int backgroundColor = array.getColor(0, 0xFF00FF);
        array.recycle();
        holder.itemLayout.setBackgroundColor(backgroundColor);
        holder.itemCheckbox.setChecked(false);

        //    set listenner
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.dialog_edit_product, null);
                final EditText editName = view.findViewById(R.id.text_name);
                final TextInputEditText inputQuantity = view.findViewById(R.id.quantityET);
                final TextInputEditText inputUnit = view.findViewById(R.id.unitET);
                final TextInputEditText inputNote = view.findViewById(R.id.noteET);
                editName.setText(object.getName());
                if (object.getQuantity() != 0) {
                    inputQuantity.setText(String.valueOf(object.getQuantity()));
                }
                inputUnit.setText(object.getUnit());
                inputNote.setText(object.getNote());

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(view);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editName.getText().toString().equals("")) {
                            ProductService productService = new ProductService(mContext);
                            object.setName(editName.getText().toString().trim());
                            object.setQuantity(Double.valueOf(inputQuantity.getText().toString().trim()));
                            object.setUnit(inputUnit.getText().toString().trim());
                            object.setNote(inputNote.getText().toString().trim());
                            productService.update(object);
                            mListItems.set(position, object);
                            notifyItemChanged(position, object);
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        holder.itemCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setPurchased(true);
                mProductService.update(object);
                buildList();
            }
        });
    }

    private void onBindItemBought(ItemBoughtHolder holder, final int position) {
        final Product object = mListItems.get(position);
        holder.itemName.setText(object.getName());
        holder.itemName.setPaintFlags(holder.itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (object.getQuantity() == 0) {
            holder.itemQuantity.setVisibility(View.GONE);

        } else {
            holder.itemQuantity.setVisibility(View.VISIBLE);
            holder.itemQuantity.setText(String.valueOf(object.getQuantity()));
        }
        if (TextUtils.isEmpty(object.getUnit())) {
            holder.itemUnit.setVisibility(View.GONE);
        } else {
            holder.itemUnit.setVisibility(View.VISIBLE);
            holder.itemUnit.setText(object.getUnit());
        }
        if (TextUtils.isEmpty(object.getNote())) {
            holder.itemNote.setVisibility(View.GONE);
        } else {
            holder.itemNote.setVisibility(View.VISIBLE);
            holder.itemNote.setText(object.getNote());
        }

        holder.itemCheckbox.setChecked(true);
        holder.itemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.backgroundList));

        //    set listenner
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.dialog_edit_product, null);
                final EditText editName = view.findViewById(R.id.text_name);
                final TextInputEditText inputQuantity = view.findViewById(R.id.quantityET);
                final TextInputEditText inputUnit = view.findViewById(R.id.unitET);
                final TextInputEditText inputNote = view.findViewById(R.id.noteET);
                editName.setText(object.getName());
                if (object.getQuantity() != 0) {
                    inputQuantity.setText(String.valueOf(object.getQuantity()));
                }
                inputUnit.setText(object.getUnit());
                inputNote.setText(object.getNote());

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(view);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editName.getText().toString().equals("")) {
                            ProductService productService = new ProductService(mContext);
                            object.setName(editName.getText().toString().trim());
                            object.setQuantity(Double.valueOf(inputQuantity.getText().toString().trim()));
                            object.setUnit(inputUnit.getText().toString().trim());
                            object.setNote(inputNote.getText().toString().trim());
                            productService.update(object);
                            mListItems.set(position, object);
                            notifyItemChanged(position, object);
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        holder.itemCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setPurchased(false);
                mProductService.update(object);
                buildList();
            }
        });

    }

    public void buildList() {
        ArrayList<Product> data = mProductService.getDataGrocery(mGrocery);
        mListItems.clear();
        mListItems.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Product product = mListItems.get(position);
        if (product.isPurchased()) {
            return TYPE_ITEM_BOUGHT;
        } else {
            return TYPE_ITEM;
        }

    }

    public ArrayList<Product> getData() {
        return mListItems;
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemQuantity, itemUnit, itemNote;
        private LinearLayout itemLayout;
        private CheckBox itemCheckbox;


        private ItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_name);
            itemQuantity = itemView.findViewById(R.id.text_quantity);
            itemUnit = itemView.findViewById(R.id.text_unit);
            itemNote = itemView.findViewById(R.id.text_note);
            itemLayout = itemView.findViewById(R.id.layout_item);
            itemCheckbox = itemView.findViewById(R.id.checkbox);
        }

    }

    private class ItemBoughtHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemQuantity, itemUnit, itemNote;
        private LinearLayout itemLayout;
        private CheckBox itemCheckbox;


        private ItemBoughtHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.text_name);
            itemQuantity = itemView.findViewById(R.id.text_quantity);
            itemUnit = itemView.findViewById(R.id.text_unit);
            itemNote = itemView.findViewById(R.id.text_note);
            itemLayout = itemView.findViewById(R.id.layout_item);
            itemCheckbox = itemView.findViewById(R.id.checkbox);

        }

    }


}
