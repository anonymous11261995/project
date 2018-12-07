package com.example.myapplication.service;

import android.content.Context;


import com.example.myapplication.R;
import com.example.myapplication.entity.Category;
import com.example.myapplication.entity.Product;
import com.example.myapplication.helper.DatabaseHelper;
import com.example.myapplication.utils.DefinitionSchema;

import java.util.ArrayList;

public class CategoryService extends GenericService {
    private Context mContext;

    public CategoryService(Context context) {
        super(context);
        mContext = context;
    }

    public boolean checkBeforeUpdate(String name){
        if(name.equals("")) return false;
        name = name.trim().toLowerCase();
        ArrayList<Category> list = getAllCategory();
        for (Category category : list) {
            if (name.equals(category.getName().toLowerCase()) || name.equals(mContext.getString(R.string.default_other_category).toLowerCase())) {
                return false;
            }
        }
        return true;
    }
    public Category createCategory(String name) {
        ArrayList<Category> list = getAllCategory();
        Category category = new Category();
        if (list.size() == 0) {
            category.setOrderView(1);
            category.setName(name);
            category.setId(createCodeId(name));
        } else {
            Category categoryLast = list.get(list.size() - 1);
            int orderLast = categoryLast.getOrderView();
            category.setOrderView(orderLast + 1);
            category.setName(name);
            category.setId(createCodeId(name));
        }
        DatabaseHelper.mCategoryDao.createCategory(category);
        return category;
    }

    public void update(Category category) {
        mCategoryDao.updateCategory(category);

    }

    public boolean delete(Category category) {
        Category categoryDefault = initCategoryDefault();
        //set again category for product
        ArrayList<Product> products = mProductDao.getProductByCategory(category.getId());
        for (Product product : products) {
            product.setCategory(categoryDefault);
            mProductDao.update(product);
        }
        return mCategoryDao.deleteCategory(category);
    }


    public ArrayList<Category> getAllCategory() {
        return (ArrayList<Category>) mCategoryDao.fetchAll();
    }

    public Category getCategoryByName(String name) {
        Category category = mCategoryDao.findByName(name);
        if (category.getName() == null) {
            return initCategoryDefault();
        }
        return category;

    }

    public Category getCategoryByID(String id) {
        Category category = mCategoryDao.findById(id);
        if (category.getName() == null) {
            return initCategoryDefault();
        }
        return category;

    }


    public Category getCategoryOfNameProduct(String nameProduct) {
        ArrayList<Product> products = mProductDao.findByName(nameProduct);
        if (products.size() == 0) {
            return initCategoryDefault();
        }
        Category category = getCategoryByID(products.get(0).getId());
        return category;
    }

    public Category initCategoryDefault() {
        Category category = new Category();
        category.setId(DEFAULT_CATEGORY_ID);
        category.setName(mContext.getString(R.string.default_other_category));
        return category;
    }


}
