package com.example.myapplication.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;


import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.utils.AppUtil;


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

    public Product addProductToGrocery(String text, Grocery grocery) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        Product product = new Product();
        product.setId(createCodeId(text));
        product.setName(text);
        product.setGrocery(grocery);
        mProductDao.create(product);
        return product;
    }

    public ArrayList<Product> getDataGrocery(Grocery grocery) {
        String queryUnchecked, queryChecked;
        if (grocery.getSortByValue() == AppUtil.LIST_SORT_BY_CUSTOM) {
            queryUnchecked = "select * from product" +
                    " where grocery = '" + grocery.getId() + "' and purchased = 0" +
                    " order by order_list, created";
            queryChecked = "select * from product" +
                    " where grocery = '" + grocery.getId() + "' and purchased = 1" +
                    " order by order_list, created";
        } else {
            queryUnchecked = "select * from product" +
                    " where grocery = '" + grocery.getId() + "' and purchased = 0" +
                    " order by name";
            queryChecked = "select * from product" +
                    " where grocery = '" + grocery.getId() + "' and purchased = 1" +
                    " order by name";
        }

        ArrayList<Product> productUnchecked = mProductDao.findByQuery(queryUnchecked);
        ArrayList<Product> productChecked = mProductDao.findByQuery(queryChecked);

        ArrayList<Product> data = new ArrayList<>();
        data.addAll(productUnchecked);
        data.addAll(productChecked);
        return data;
    }

    public void update(Product object) {
        mProductDao.update(object);
    }

    public void clearProductBought(ArrayList<Product> data) {
        for (Product product : data) {
            if (product.isPurchased()) {
                product.setGrocery(new Grocery());
                mProductDao.update(product);
            }
        }
    }
}
