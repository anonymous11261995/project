package com.example.myapplication.service;

import android.content.Context;
import android.util.Log;


import com.example.myapplication.R;
import com.example.myapplication.entity.Category;
import com.example.myapplication.entity.Product;
import com.example.myapplication.entity.ShoppingList;
import com.example.myapplication.utils.ColorUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TienTruong on 7/16/2018.
 */

public class ShoppingListService extends GenericService {
    private static final String TAG = ShoppingListService.class.getSimpleName();
    private CategoryService mCategoryService;
    private ProductService mProductService;
    private Context mContext;

    public ShoppingListService(Context context) {
        super(context);
        this.mContext = context;
        mCategoryService = new CategoryService(context);
        mProductService = new ProductService(context);
    }


    public Product addProductToShopping(String nameProduct, ShoppingList list) {
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
        product.setShoppingList(list);
        //set default
        if (quantity != 1) product.setQuantity(quantity);
        if (!unit.equals("")) product.setUnit(unit);

        orderProductInGroup(product.getCategory(), list);
        mProductDao.create(product);
        return product;
    }

    private boolean createShoppingList(ShoppingList shoppingList) {
        String id = createCodeId(shoppingList.getName());
        Log.d(TAG, "id shopping list: " + id);
        shoppingList.setId(id);
        return mShoppingListDao.create(shoppingList);
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
                product.setShoppingList(new ShoppingList());
                mProductService.updateProduct(product);
            }
        }
    }

    //function test
    public ShoppingList getShoppingListActive() {
        ShoppingList shoppingList = mShoppingListDao.fetchShoppingListActive();
        if (shoppingList.getName() == null) {
            ArrayList<ShoppingList> list = getAllShoppingList();
            if (list.size() != 0) {
                list.get(0).setActive(true);
                mShoppingListDao.update(list.get(0));
                return list.get(0);

            } else {
                Log.d(TAG, "ShoppingList: init");
                shoppingList = new ShoppingList();
                shoppingList.setName(mContext.getResources().getString(R.string.default_name_shopping_list));
                shoppingList.setActive(true);
                createShoppingList(shoppingList);
                return shoppingList;
            }

        }
        return shoppingList;
    }

    public ArrayList<Product> productShopping(ShoppingList shoppingList) {
        ArrayList<Product> list = new ArrayList<>();
        ArrayList<Product> productUnChecked = mProductService.productShoppingUnChecked(shoppingList);
        ArrayList<Product> productChecked = mProductService.productShoppingChecked(shoppingList);
        if (productUnChecked.size() != 0) {
            list.addAll(productUnChecked);
        }
        if (productChecked.size() != 0) {
            list.addAll(productChecked);
        }
        return list;
    }

    public ArrayList<Product> productShoppingSrceen(ShoppingList shoppingList) {
        ArrayList<Product> list = productShopping(shoppingList);
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

    public ArrayList<Product> productsSnooze(ShoppingList shoppingList) {
        return mProductService.productSnoozeShopping(shoppingList);
    }



    public ArrayList<ShoppingList> getAllShoppingList() {
        ArrayList<ShoppingList> result = new ArrayList<>();
        ArrayList<ShoppingList> list = mShoppingListDao.fetchAllShoppingList();
        for (ShoppingList shoppingList : list) {
            if (shoppingList.getColor() == 0) {
                int color = ColorUtils.randomColor(mContext);
                shoppingList.setColor(color);
                mShoppingListDao.update(shoppingList);
            }
            result.add(shoppingList);
        }
        return result;
    }

    public void updateList(ShoppingList shoppingList) {
        mShoppingListDao.update(shoppingList);
    }

    public void createNewListShopping(String name) {
        ArrayList<ShoppingList> list = getAllShoppingList();
        for (ShoppingList shoppingList : list) {
            shoppingList.setActive(false);
            mShoppingListDao.update(shoppingList);
        }
        ShoppingList newList = new ShoppingList();
        newList.setActive(true);
        newList.setId(createCodeId(name));
        newList.setName(name);
        newList.setColor(ColorUtils.randomColor(mContext));
        mShoppingListDao.create(newList);
    }

    public ShoppingList activeShopping(String name) {
        ShoppingList sl = new ShoppingList();
        ArrayList<ShoppingList> list = getAllShoppingList();
        for (ShoppingList shoppingList : list) {
            if (shoppingList.getName().equals(name)) {
                shoppingList.setActive(true);
                sl = shoppingList;
            } else {
                shoppingList.setActive(false);
            }
            mShoppingListDao.update(shoppingList);
        }
        return sl;

    }

    public void deleteShopping(ShoppingList shoppingList) {
        mShoppingListDao.delete(shoppingList);
        if (shoppingList.isActive()) {
            ArrayList<ShoppingList> list = getAllShoppingList();
            if (list.size() != 0) {
                ShoppingList shoppingListActive = list.get(0);
                shoppingListActive.setActive(true);
                mShoppingListDao.update(shoppingListActive);
            }
        }


    }

    public ShoppingList getListByName(String name) {
        return mShoppingListDao.fetchShoppingListByName(name);
    }


    public void addProductShared(String nameShoppingList, ArrayList<Product> mDataChecked) {
        String otherCategory = mContext.getResources().getString(R.string.default_other_category);
        String nameCategoryBought = mContext.getResources().getString(R.string.default_category_bought);
        //unit,unitPrice,quanity,note,name đã có trong object Product
        ShoppingList shoppingList = getListByName(nameShoppingList);
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
            product.setShoppingList(shoppingList);
            //set default
            product.setCreated(new Date());
            product.setModified(new Date());
            product.setLastChecked(new Date());
            product.setHistory(true);
            product.setOrderInGroup(0);
            orderProductInGroup(product.getCategory(), shoppingList);
            mProductDao.create(product);
        }
    }

    public void addProductCrawled(String nameList, Product product) {
        ShoppingList shoppingList = getListByName(nameList);
        activeShopping(nameList);
        Category category = mCategoryService.getCategoryOfNameProduct(product.getName());
        product.setCategory(category);
        product.setId(createCodeId(product.getName()));
        product.setShoppingList(shoppingList);
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
        orderProductInGroup(product.getCategory(), shoppingList);
        mProductDao.create(product);

    }


    //Function private
    void orderProductInGroup(Category category, ShoppingList shoppingList) {
        String idCategory = category.getId();
        ArrayList<Product> list = mProductDao.getProductByCategoryAndShopping(idCategory, shoppingList.getId());
        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            product.setOrderInGroup(i + 1);
            mProductDao.update(product);
        }
    }

    public void moveItems(ShoppingList listTarget, ArrayList<Product> data) {
        activeShopping(listTarget.getName());
        for (Product product : data) {
            product.setShoppingList(new ShoppingList());
            mProductDao.update(product);
            Product newProduct = product;
            newProduct.setId(createCodeId(product.getName()));
            newProduct.setShoppingList(listTarget);
            newProduct.setCreated(new Date());
            newProduct.setModified(new Date());
            newProduct.setLastChecked(new Date());
            newProduct.setOrderInGroup(0);
            orderProductInGroup(newProduct.getCategory(), listTarget);
            mProductDao.create(newProduct);
        }
    }

    public void copyItems(ShoppingList listTarget, ArrayList<Product> data) {
        activeShopping(listTarget.getName());
        for (Product product : data) {
            Product newProduct = product;
            newProduct.setId(createCodeId(product.getName()));
            newProduct.setShoppingList(listTarget);
            newProduct.setCreated(new Date());
            newProduct.setModified(new Date());
            newProduct.setLastChecked(new Date());
            newProduct.setOrderInGroup(0);
            orderProductInGroup(newProduct.getCategory(), listTarget);
            mProductDao.create(newProduct);
        }
    }

}