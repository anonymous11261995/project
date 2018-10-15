package com.example.myapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProducttHolder> {
    private static final String TAG = ProductAdapter.class.getSimpleName();
    private ProductAdapter.OnItemClickListener listener;

    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductAdapter.ProducttHolder holder, int position, @NonNull Product model) {
        Log.d(TAG, "name: " + model.getName());
        holder.textViewName.setText(model.getName());

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

    class ProducttHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public ProducttHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textview_product_name);

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
