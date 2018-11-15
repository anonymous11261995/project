package com.example.myapplication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.model.Grocery;
import com.example.myapplication.model.Product;
import com.example.myapplication.utils.AppUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;


public class ProductFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ProductFragment.class.getSimpleName();
    private static final String COLLECTION_PRODUCT_PATH = AppUtil.COLLECTION_PRODUCT_PATH;

    private ImageView mActionBack;
    private ImageView mActionMenu;

    private Grocery mGrocery;
    private ProductAdapter mAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef = db.collection(COLLECTION_PRODUCT_PATH);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initViews();
        setUpRecyclerView();
        setOnListener();


    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imageview_product_back:
                replaceFragment(new GroceryFragment());
                break;
            default:
                break;
        }

    }

    private void initViews() {
        mActionBack = getView().findViewById(R.id.imageview_product_back);
        mActionMenu = getView().findViewById(R.id.imageview_product_menu);
        TextView textViewHeader = getView().findViewById(R.id.textview_product_header);
        textViewHeader.setText(mGrocery.getName());

    }

    private void setOnListener() {
        mActionBack.setOnClickListener(this);

    }

    private void setUpRecyclerView() {
        //TODO
        //Query query = productRef.whereEqualTo("groceryID", mGrocery.getId());
        Query query = productRef.orderBy("created");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();
        mAdapter = new ProductAdapter(getContext(), options);
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_view_product);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        mAdapter.setOnClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Product product = documentSnapshot.toObject(Product.class);
                if (product.getIsPurchased()) {
                    documentSnapshot.getReference().update("isPurchased", false);
                } else {
                    documentSnapshot.getReference().update("isPurchased", true);
                }
            }
        });
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_container_main, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setGrocery(Grocery grocery) {
        this.mGrocery = grocery;
    }
}
