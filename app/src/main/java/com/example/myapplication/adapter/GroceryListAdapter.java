package com.example.myapplication.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.GroceryList;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class GroceryListAdapter extends FirestoreRecyclerAdapter<GroceryList, GroceryListAdapter.GroceryListHolder> {
    private static final String TAG = GroceryListAdapter.class.getSimpleName();
    private OnItemClickListener listener;

    public GroceryListAdapter(@NonNull FirestoreRecyclerOptions<GroceryList> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroceryListHolder holder, int position, @NonNull GroceryList model) {
        Log.d(TAG,"name: " + model.getName());
        holder.textViewName.setText(model.getName());

    }

    @NonNull
    @Override
    public GroceryListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item,
                parent, false);
        return new GroceryListHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class GroceryListHolder extends RecyclerView.ViewHolder {
        TextView textViewName;

        public GroceryListHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.grocery_name);

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

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
