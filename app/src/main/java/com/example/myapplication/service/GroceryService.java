package com.example.myapplication.service;

import android.content.Context;
import android.util.Log;


import com.example.myapplication.R;
import com.example.myapplication.entity.Category;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.utils.ColorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/16/2018.
 */

public class GroceryService extends GenericService {
    private static final String TAG = GroceryService.class.getSimpleName();
    private CategoryService mCategoryService;
    private ProductService mProductService;
    private Context mContext;

    public GroceryService(Context context) {
        super(context);
        this.mContext = context;
        mCategoryService = new CategoryService(context);
        mProductService = new ProductService(context);
    }


    public Product addProductToShopping(String nameProduct, Grocery list) {
        String name = "", unit = "";
        int quantity = 1;
        try {
            JSONObject json = paserQuantityUnit(nameProduct);
            name = json.getString("name");
            unit = json.getString("unit");
            quantity = (int) json.getDouble("quantity");

        } catch (JSONException e) {

        }
        if (name.equals("")) {
            name = nameProduct;
            unit = "";
            quantity = 1;
        }
        Product product = mProductService.getProductSameName(name);

        product.setId(createCodeId(name));
        product.setName(name);
        product.setGrocery(list);
        //set default
        if (quantity != 1) product.setQuantity(quantity);
        if (!unit.equals("")) product.setUnit(unit);

        orderProductInGroup(product.getCategory(), list);
        mProductDao.create(product);
        return product;
    }

    private boolean createShoppingList(Grocery grocery) {
        String id = createCodeId(grocery.getName());
        Log.d(TAG, "id shopping list: " + id);
        grocery.setId(id);
        return mShoppingListDao.create(grocery);
    }

    public void clearAllProduct(ArrayList<Product> products) {
        for (Product p : products) {
            if (p.getName() != null) {
                mProductService.deleteProductFromList(p);
            }
        }
    }


    public void checkAll(ArrayList<Product> products) {
        for (Product p : products) {
            if (p.getName() != null) {
                p.setChecked(true);
                p.setLastChecked(new Date());
                mProductService.updateProduct(p);
            }

        }
    }

    public void unCheckAll(ArrayList<Product> products) {
        for (Product p : products) {
            if (p.getName() != null) {
                p.setChecked(false);
                p.setLastChecked(new Date());
                mProductService.updateProduct(p);
            }
        }

    }

    public void clearAllProductChecked(ArrayList<Product> products) {
        for (Product product : products) {
            if (product.getName() != null && product.isChecked()) {
                product.setGrocery(new Grocery());
                mProductService.updateProduct(product);
            }
        }
    }

    //function test
    public Grocery getShoppingListActive() {
        Grocery grocery = mShoppingListDao.fetchShoppingListActive();
        if (grocery.getName() == null) {
            ArrayList<Grocery> list = getAllShoppingList();
            if (list.size() != 0) {
                list.get(0).setActive(true);
                mShoppingListDao.update(list.get(0));
                return list.get(0);

            } else {
                Log.d(TAG, "Grocery: init");
                grocery = new Grocery();
                grocery.setName(mContext.getResources().getString(R.string.default_name_shopping_list));
                grocery.setActive(true);
                createShoppingList(grocery);
                return grocery;
            }

        }
        return grocery;
    }

    public ArrayList<Product> productShopping(Grocery grocery) {
        ArrayList<Product> list = new ArrayList<>();
        ArrayList<Product> productUnChecked = mProductService.productShoppingUnChecked(grocery);
        ArrayList<Product> productChecked = mProductService.productShoppingChecked(grocery);
        if (productUnChecked.size() != 0) {
            list.addAll(productUnChecked);
        }
        if (productChecked.size() != 0) {
            list.addAll(productChecked);
        }
        return list;
    }

    public ArrayList<Product> productShoppingSrceen(Grocery grocery) {
        ArrayList<Product> list = productShopping(grocery);
        ArrayList<Product> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            if (!product.isHide()) {
                result.add(product);
            }
        }
        if (result.size() == 1 || result.size() == 0) return new ArrayList<>();
        int lenght = result.size() - 1;
        for (int i = 0; i < lenght; i++) {
            if (result.get(i).getName() == null && result.get(i + 1).getName() == null) {
                result.remove(i);
                i--;
                lenght--;
            }
        }
        if (result.get(result.size() - 1).getName() == null) {
            result.remove(result.size() - 1);
        }
        return result;
    }

    public ArrayList<Product> productsSnooze(Grocery grocery) {
        return mProductService.productSnoozeShopping(grocery);
    }



    public ArrayList<Grocery> getAllShoppingList() {
        ArrayList<Grocery> result = new ArrayList<>();
        ArrayList<Grocery> list = mShoppingListDao.fetchAllShoppingList();
        for (Grocery grocery : list) {
            if (grocery.getColor() == 0) {
                int color = ColorUtils.randomColor(mContext);
                grocery.setColor(color);
                mShoppingListDao.update(grocery);
            }
            result.add(grocery);
        }
        return result;
    }

    public void updateList(Grocery grocery) {
        mShoppingListDao.update(grocery);
    }

    public Grocery createNewListShopping(String name) {
        ArrayList<Grocery> list = getAllShoppingList();
        for (Grocery grocery : list) {
            grocery.setActive(false);
            mShoppingListDao.update(grocery);
        }
        Grocery newList = new Grocery();
        newList.setActive(true);
        newList.setId(createCodeId(name));
        newList.setName(name);
        newList.setColor(ColorUtils.randomColor(mContext));
        mShoppingListDao.create(newList);
        return newList;
    }

    public Grocery activeShopping(String name) {
        Grocery sl = new Grocery();
        ArrayList<Grocery> list = getAllShoppingList();
        for (Grocery grocery : list) {
            if (grocery.getName().equals(name)) {
                grocery.setActive(true);
                sl = grocery;
            } else {
                grocery.setActive(false);
            }
            mShoppingListDao.update(grocery);
        }
        return sl;

    }

    public void deleteShopping(Grocery grocery) {
        mShoppingListDao.delete(grocery);
        if (grocery.isActive()) {
            ArrayList<Grocery> list = getAllShoppingList();
            if (list.size() != 0) {
                Grocery groceryActive = list.get(0);
                groceryActive.setActive(true);
                mShoppingListDao.update(groceryActive);
            }
        }


    }

    public Grocery getListByName(String name) {
        return mShoppingListDao.fetchShoppingListByName(name);
    }


    public void addProductShared(String nameShoppingList, ArrayList<Product> mDataChecked) {
        String otherCategory = mContext.getResources().getString(R.string.default_other_category);
        String nameCategoryBought = mContext.getResources().getString(R.string.default_category_bought);
        //unit,unitPrice,quanity,note,name đã có trong object Product
        Grocery grocery = getListByName(nameShoppingList);
        activeShopping(nameShoppingList);
        for (Product product : mDataChecked) {
            //Log.d("ABCd", "name: " + product.getName());
            String nameCategory = product.getCategory().getName();
            Category category = mCategoryService.getCategoryOfNameProduct(product.getName());
            //Log.d("ABCD", "category: " + categorySystem.getName());
            //Khi không có category thì sẽ tạo mới
            if (category.getName().equals(otherCategory) && !nameCategory.equals(otherCategory) && !nameCategory.equals(nameCategoryBought)) {
                category = mCategoryService.getCategoryByName(nameCategory);
                if (category.getName().equals(otherCategory)) {
                    category = mCategoryService.createCategory(nameCategory);
                }
            }
            product.setCategory(category);
            product.setId(createCodeId(product.getName()));
            product.setGrocery(grocery);
            //set default
            product.setCreated(new Date());
            product.setModified(new Date());
            product.setLastChecked(new Date());
            product.setHistory(true);
            product.setOrderInGroup(0);
            orderProductInGroup(product.getCategory(), grocery);
            mProductDao.create(product);
        }
    }

    public void addProductCrawled(String nameList, Product product) {
        Grocery grocery = getListByName(nameList);
        activeShopping(nameList);
        Category category = mCategoryService.getCategoryOfNameProduct(product.getName());
        product.setCategory(category);
        product.setId(createCodeId(product.getName()));
        product.setGrocery(grocery);
        //set value defualt
        if (product.getUnitPrice() == null) {
            product.setUnitPrice(0.0);
        }
        product.setQuantity(1);
        product.setCreated(new Date());
        product.setModified(new Date());
        product.setLastChecked(new Date());
        product.setHistory(true);
        product.setOrderInGroup(0);
        orderProductInGroup(product.getCategory(), grocery);
        mProductDao.create(product);

    }


    //Function private
    void orderProductInGroup(Category category, Grocery grocery) {
        String idCategory = category.getId();
        ArrayList<Product> list = mProductDao.getProductByCategoryAndShopping(idCategory, grocery.getId());
        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            product.setOrderInGroup(i + 1);
            mProductDao.update(product);
        }
    }

    public void moveItems(Grocery listTarget, ArrayList<Product> data) {
        activeShopping(listTarget.getName());
        for (Product product : data) {
            product.setGrocery(new Grocery());
            mProductDao.update(product);
            Product newProduct = product;
            newProduct.setId(createCodeId(product.getName()));
            newProduct.setGrocery(listTarget);
            newProduct.setCreated(new Date());
            newProduct.setModified(new Date());
            newProduct.setLastChecked(new Date());
            newProduct.setOrderInGroup(0);
            orderProductInGroup(newProduct.getCategory(), listTarget);
            mProductDao.create(newProduct);
        }
    }

    public void copyItems(Grocery listTarget, ArrayList<Product> data) {
        activeShopping(listTarget.getName());
        for (Product product : data) {
            Product newProduct = product;
            newProduct.setId(createCodeId(product.getName()));
            newProduct.setGrocery(listTarget);
            newProduct.setCreated(new Date());
            newProduct.setModified(new Date());
            newProduct.setLastChecked(new Date());
            newProduct.setOrderInGroup(0);
            orderProductInGroup(newProduct.getCategory(), listTarget);
            mProductDao.create(newProduct);
        }
    }

}