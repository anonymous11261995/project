package com.example.myapplication.service;

import android.content.Context;
import android.util.Log;


import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductService extends GenericService {
    private static final String TAG = ProductService.class.getSimpleName();
    private Context mContext;
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();

    public ProductService(Context context) {
        super(context);
        this.mContext = context;
    }

    public ArrayList<String> getAutoComplete() {
        ArrayList<Product> data = mProductDao.findByAutocomplete();
        ArrayList<String> list = new ArrayList<>();
        for (Product p : data) {
            if (p.getName() != null) {
                list.add(p.getName());
            }
        }
        ArrayList<String> result = new ArrayList<>(sortByFrequencyItem(list));
        return result;

    }

}
