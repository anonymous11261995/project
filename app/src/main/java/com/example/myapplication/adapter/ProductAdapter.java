package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Grocery;
import com.example.myapplication.model.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProducttHolder> {
    private static final String TAG = ProductAdapter.class.getSimpleName();
    private ProductAdapter.OnItemClickListener listener;
    private Context mContext;

    public ProductAdapter(Context context, @NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
        this.mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductAdapter.ProducttHolder holder, int position, @NonNull Product model) {
        Log.d(TAG, "name: " + model.getName() + ", created: " + model.getCreated() + ", id: " + model.getId()
        + ", isPurchased: " + model.getIsPurchased() + ", quantity: " + model.getQuantity());
        holder.textViewName.setText(model.getName());
        if (model.getQuantity() != 1) {
            holder.textViewQuantity.setVisibility(View.VISIBLE);
            holder.textViewQuantity.setText(String.valueOf(model.getQuantity()));
        } else {
            holder.textViewQuantity.setVisibility(View.GONE);
        }
        if (model.getIsPurchased()) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.background_product_purchased));
            holder.textViewName.setPaintFlags(holder.textViewName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.background_product_not_purchased));
        }

    }

    @NonNull
    @Override
    public ProductAdapter.ProducttHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,
                parent, false);
        return new ProductAdapter.ProducttHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
//    public void editItem(int position){
//        getSnapshots().getSnapshot(position).getReference().update(getSnapshots().getSnapshot(position).toObject(Grocery.class));
//    }

    class ProducttHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewQuantity;
        CardView cardView;

        public ProducttHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textview_product_item_name);
            textViewQuantity = itemView.findViewById(R.id.textview_product_item_quantity);
            cardView = itemView.findViewById(R.id.cardview_product_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnClickListener(ProductAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
