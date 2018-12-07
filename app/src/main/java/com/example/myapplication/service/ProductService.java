package com.example.myapplication.service;

import android.content.Context;
import android.util.Log;


import com.example.myapplication.AppConfig;
import com.example.myapplication.R;
import com.example.myapplication.entity.Category;
import com.example.myapplication.entity.Product;
import com.example.myapplication.entity.ShoppingList;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductService extends GenericService{
    private static final String TAG = ProductService.class.getSimpleName();
    private Context mContext;
    private CategoryService mCategoryService;
    private ShoppingListService mShoppingListService;
    private static final String CURRENCY_DEFUALT = AppConfig.getCurrencySymbol();

    public ProductService(Context context) {
        super(context);
        this.mContext = context;
        mCategoryService = new CategoryService(context);
    }

    public ArrayList<String> getAutoComplete() {
        String query = COLUMN_IS_HISTORY + " = 1";
        ArrayList<Product> data = mProductDao.query(query);
        ArrayList<String> list = new ArrayList<>();
        for (Product p : data) {
            if (p.getName() != null) {
                list.add(p.getName());
            }
        }
        ArrayList<String> result = new ArrayList<>(sortByFrequencyItem(list));
        return result;

    }

    public Category getCategoryOfProduct(Product product) {
        String id = product.getCategory().getId();
        return mCategoryService.getCategoryByID(id);
    }


    public boolean updateProduct(Product product) {
        return mProductDao.update(product);
    }

    public Product findProductById(String id) {
        Product product = mProductDao.findById(id);
        return product;
    }

    public Product getProductSameName(String nameProduct) {
        //Get category, unit, unitPrice
        ArrayList<Product> products = mProductDao.findByName(nameProduct);
        if (products.size() == 0) {
            Product p = new Product();
            Category category = mCategoryService.initCategoryDefault();
            p.setCategory(category);
            return p;
        }
        Product temp = products.get(0);
        Product result = new Product();
        result.setCategory(temp.getCategory());
        result.setUnitPrice(temp.getUnitPrice());
        result.setUnit(temp.getUnit());
        return result;
    }


    public String listToTextShare(ArrayList<Product> products) {
        String flatCategory = "";
        Boolean addCategoryBought = true;
        if (products.size() == 0) {
            return null;
        }
        StringBuilder text = new StringBuilder();
        for (Product item : products) {
            String textCategory = item.getCategory().getName();
            if (addCategoryBought && item.isChecked()) {
                text.append(mContext.getResources().getString(R.string.default_category_bought));
                text.append("\n");
                addCategoryBought = false;
            }
            if (!flatCategory.equals(textCategory) && !item.isChecked()) {
                text.append(textCategory);
                text.append("\n");
                flatCategory = textCategory;
            }
            String line = getDescriptionProductShare(item);
            if (item.isChecked()) {
                text.append(" - ");
            } else {
                text.append(" + ");
            }
            text.append(line);
            String note = item.getNote();
            if (note == null) note = "";
            if (!note.equals("")) {
                note = " [" + note + "]";
                text.append(note);
            }
            text.append("\n");

        }
        text.append(TEXT_SEPARATOR);
        text.append("\n");
        text.append(TEXT_SIGNATURE_BLIST);
        text.append(" ");
        text.append(AppConfig.URL_REDIRECT_TO_STORE);
        return text.toString();
    }

    public ArrayList<Product> textShareToList(String textClipboard) {
        ArrayList<Product> result = new ArrayList<>();
        if (textClipboard == null) textClipboard = "";
        if (textClipboard.equals("")) return result;
        if (textClipboard.indexOf(SIGNRATURE_OUTOFMILK) != -1) {
            result = readTextShareOutOfMilk(textClipboard);
            return result;
        }
        if (textClipboard.indexOf(SIGNRAURE_OURGROCETIES) != -1) {
            result = readTextShareOursGroceries(textClipboard);
            return result;
        }
        if (textClipboard.indexOf(SIGNRATURE_BUYMEAPIE) != -1) {
            result = readTextShareBuyMeAPie(textClipboard);
            return result;
        }
        result = readTextBList(textClipboard);
        return result;

    }

    private ArrayList<Product> readTextBList(String textClipboard) {
        ArrayList<Product> result = new ArrayList<>();
        String[] arrString = textClipboard.split("\n");
        String categoryName = mContext.getResources().getString(R.string.default_other_category);
        for (int i = 0; i < arrString.length; i++) {
            try {
                String text = arrString[i].trim();
                if (text.equals(TEXT_SEPARATOR)) break;
                if (text.equals("")) continue;
                Product product = new Product();
                Category category = new Category();
                if (text.indexOf("+") != 0 && text.indexOf("-") != 0) {
                    //Log.d(TAG, "category: " + text);
                    categoryName = text.trim();
                    category.setName(categoryName);
                    product.setCategory(category);
                    result.add(product);
                    continue;
                }
                category.setName(categoryName);
                product.setCategory(category);
                Map map = readLineItemTextShare(text);
                product.setName(map.get("name").toString().trim());
                product.setUnitPrice(Double.valueOf(map.get("unitPrice").toString()));
                product.setUnit(map.get("unit").toString());
                product.setQuantity(Integer.valueOf(map.get("quantity").toString()));
                product.setNote(map.get("note").toString());
                product.setUrl(map.get("url").toString());
                if (text.indexOf("+") == 0) {
                    //Log.d(TAG, "Item uncheck: " + text);
                    product.setChecked(false);
                }
                if (text.indexOf("-") == 0) {
                    //Log.d(TAG, "Item checked: " + text);
                    product.setChecked(true);
                }

                result.add(product);
            } catch (Exception e) {
                Log.e("Error", "text_share_to_list" + e.getMessage());
            }

        }
        return result;
    }

    private ArrayList<Product> readTextShareBuyMeAPie(String textClipboard) {
        ArrayList<Product> result = new ArrayList<>();
        String[] arrString = textClipboard.split("\n");
        String categoryName = mContext.getResources().getString(R.string.default_other_category);
        Category category = new Category();
        category.setName(categoryName);
        for (int i = 0; i < arrString.length; i++) {
            try {
                String text = arrString[i];
                if (text.indexOf(SIGNRATURE_BUYMEAPIE) != -1) break;
                if (text.equals("")) continue;
                Product product = new Product();
                product.setName(text.replace(",", "").trim());
                product.setUnitPrice(0.0);
                product.setUnit("");
                product.setQuantity(1);
                product.setNote("");
                product.setUrl("");
                product.setChecked(false);
                product.setCategory(category);
                result.add(product);
            } catch (Exception e) {
                Log.e("Error", "text_share_to_list" + e.getMessage());
                continue;
            }


        }
        return result;
    }

    private ArrayList<Product> readTextShareOursGroceries(String textClipboard) {
        String textSpector = "---";
        ArrayList<Product> result = new ArrayList<>();
        String[] arrString = textClipboard.split("\n");
        String categoryName = mContext.getResources().getString(R.string.default_other_category);
        for (int i = 0; i < arrString.length; i++) {
            try {
                String text = arrString[i];
                if (text.equals(textSpector)) break;
                if (text.equals("")) continue;
                Product product = new Product();
                Category category = new Category();
                if (text.indexOf(" ") != 0) {
                    Log.d(TAG, "category: " + text);
                    categoryName = text.trim();
                    category.setName(categoryName);
                    product.setCategory(category);
                    result.add(product);
                    continue;
                }
                category.setName(categoryName);
                product.setCategory(category);
                Map map = readLineItemOursGroceries(text);
                product.setName(map.get("name").toString().trim());
                product.setUnitPrice(Double.valueOf(map.get("unitPrice").toString()));
                product.setUnit(map.get("unit").toString());
                product.setQuantity(Integer.valueOf(map.get("quantity").toString()));
                product.setNote(map.get("note").toString());
                product.setUrl(map.get("url").toString());
                product.setChecked(false);
                result.add(product);
            } catch (Exception e) {
                Log.e("Error", "text_share_to_list" + e.getMessage());
            }


        }
        return result;
    }

    private ArrayList<Product> readTextShareOutOfMilk(String textClipboard) {
        String textSpector = "====";
        ArrayList<Product> result = new ArrayList<>();
        String[] arrString = textClipboard.split("\n");
        String categoryName = mContext.getResources().getString(R.string.default_other_category);

        for (int i = 0; i < arrString.length - 1; i++) {
            try {
                String text = arrString[i].trim();
                String textNext = arrString[i + 1].trim();
                if (text.equals("") && textNext.equals("")) break;
                if (text.equals("") || text.contains(textSpector)) continue;
                Product product = new Product();
                Category category = new Category();
                if (textNext.contains(textSpector)) {
                    Log.d(TAG, "category: " + text);
                    categoryName = text.trim();
                    category.setName(categoryName);
                    product.setCategory(category);
                    result.add(product);
                    continue;
                }
                category.setName(categoryName);
                product.setCategory(category);
                Map map = readLineItemOutOfMilk(text);
                product.setName(map.get("name").toString().trim());
                product.setUnitPrice(Double.valueOf(map.get("unitPrice").toString()));
                product.setUnit(map.get("unit").toString());
                product.setQuantity(Integer.valueOf(map.get("quantity").toString()));
                product.setNote(map.get("note").toString());
                product.setUrl(map.get("url").toString());
                product.setChecked(false);
                result.add(product);
            } catch (Exception e) {
                Log.e("Error", "text_share_to_list" + e.getMessage());
            }


        }
        return result;
    }

    private Map readLineItemOursGroceries(String text) {
        Map map = new HashMap<String, String>();
        int quantity = 1;
        double price = 0.0;
        String unit = "", note = "", name = text, url = "";
        try {
            if (text.lastIndexOf("(") != -1 && text.lastIndexOf(")") != -1) {
                String quantityString = text.substring(text.lastIndexOf("(") + 1, text.lastIndexOf(")")).trim();
                if (!quantityString.equals("")) {
                    quantity = regexNumber(quantityString);
                }
                name = text.substring(1, text.lastIndexOf("("));
            }
            map.put("name", name.replaceFirst("-", "").trim());
            map.put("unit", "");
            map.put("quantity", String.valueOf(quantity));
            map.put("unitPrice", 0);
            map.put("note", "");
            map.put("url", "");

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            map.put("name", text.replaceFirst("-", ""));
            map.put("unit", "");
            map.put("quantity", 1);
            map.put("unitPrice", 0);
            map.put("note", "");
            map.put("url", "");
        }
        return map;
    }


    private Map readLineItemOutOfMilk(String text) {
        Map map = new HashMap<String, String>();
        int quantity = 1;
        double price = 0.0;
        String unit = "", note = "", name = text, url = "";
        try {
            if (text.lastIndexOf("(") != -1 && text.lastIndexOf(")") != -1) {
                String quantityUnit = text.substring(text.lastIndexOf("(") + 1, text.lastIndexOf(")")).trim();
                if (!quantityUnit.equals("")) {
                    quantity = regexNumber(quantityUnit);
                    unit = quantityUnit.replace(String.valueOf(quantity), "").trim();
                }
                name = text.substring(1, text.lastIndexOf("("));
            }
            String words[] = text.split(" ");
            for (String word : words) {
                if (word.indexOf(".00") != -1) {
                    price = Double.valueOf(regexNumber(word.replace(",", "")));
                    if (name.equals(text)) {
                        name = text.substring(1, text.indexOf(word));
                    }
                    break;
                }
            }
            map.put("name", name.replaceFirst("-", "").trim());
            map.put("unit", unit);
            map.put("quantity", String.valueOf(quantity));
            map.put("unitPrice", price / quantity);
            map.put("note", note);
            map.put("url", url);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            map.put("name", text.replaceFirst("-", ""));
            map.put("unit", "");
            map.put("quantity", 1);
            map.put("unitPrice", 0);
            map.put("note", "");
            map.put("url", "");
        }
        return map;
    }

    private Map readLineItemTextShare(String line) {
        Map map = new HashMap<String, String>();
        int quantity = 1;
        double price = 0.0;
        String unit = "", note = "", name = "", url = "";
        try {
            //get quantity and unit
            if (line.indexOf("(") != -1 && line.indexOf(")") != -1) {
                String quantityUnit = line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf(")")).trim();
                if (!quantityUnit.equals("")) {
                    quantity = regexNumber(quantityUnit);
                    unit = quantityUnit.replace(String.valueOf(quantity), "").trim();
                }
            }
            //get note
            if (line.indexOf("[") != -1 && line.indexOf("]") != -1) {
                note = line.substring(line.lastIndexOf("[") + 1, line.lastIndexOf("]")).trim();
                if (note.startsWith("https://") || note.startsWith("http://")) {
                    note = "";
                    url = note;
                }
            }
            //get price
            String words[] = line.split(" ");
            String wordPrice = "";
            for (String word : words) {
                if (word.indexOf(CURRENCY_DEFUALT) != -1 && word.indexOf("0.0") != -1) {
                    wordPrice = word;
                    price = Double.valueOf(word.replace(",", "").replace(CURRENCY_DEFUALT, ""));
                    break;
                }
            }
            if (quantity != 1) {
                name = line.substring(1, line.indexOf("(" + quantity + ")"));
            } else if (!wordPrice.equals("")) {
                name = line.substring(1, line.indexOf(wordPrice));
            } else if (!note.equals("")) {
                name = line.substring(1, line.indexOf("[" + note + "]"));
            } else if (!url.equals("")) {
                name = line.substring(1, line.indexOf("[" + note + "]"));
            } else {
                name = line.substring(1);
            }
            map.put("name", name.replaceAll("  ", " ").replace("+", ""));
            map.put("unit", unit);
            map.put("quantity", String.valueOf(quantity));
            map.put("unitPrice", price / quantity);
            map.put("note", note);
            map.put("url", url);

        } catch (Exception e) {
            map.put("name", line);
            map.put("unit", "");
            map.put("quantity", 1);
            map.put("unitPrice", 0);
            map.put("note", "");
            map.put("url", "");
            Log.e("Error", "parser_line_item: " + e.getMessage());
        }
        return map;
    }

    public ArrayList<String> getMasterList() {
        String query = COLUMN_IS_HISTORY + " = 1";
        ArrayList<Product> listData = mProductDao.query(query);
        ArrayList<String> result = new ArrayList<>();
        for (int i = listData.size() - 1; i >= 0; i--) {
            Product product = listData.get(i);
            String name = product.getName();
            if (!result.contains(name)) {
                result.add(name);
            }
        }
        return result;
    }


    public void deleteItemMasterList(String name) {
        ArrayList<Product> list = mProductDao.findByName(name);
        for (Product product : list) {
            product.setHistory(false);
            mProductDao.update(product);
        }
    }

    public void deleteItemAutocomplete(String name) {
        ArrayList<Product> list = mProductDao.findByName(name);
        for (Product product : list) {
            product.setHistory(false);
            mProductDao.update(product);
        }
    }

    public void deleteProductFromList(Product product) {
        ShoppingList shoppingList = new ShoppingList();
        product.setShoppingList(shoppingList);
        mProductDao.update(product);
    }

    public String parserPrice(double price) {
        price = (int) Math.round(price * 10) / (double) 10;
        String result = "";
        int k = 1;
        boolean isParser = false;
        String textPrice = String.valueOf(price);
        String reverse = new StringBuffer(textPrice).reverse().toString();
        for (int i = 0; i < reverse.length(); i++) {
            String c = Character.toString(reverse.charAt(i));
            if (isParser && k % 4 == 0) {
                result = result + "," + c;
            } else {
                result = result + c;
            }
            if (isParser) k++;
            if (c.equals(".")) {
                isParser = true;
            }
        }
        return new StringBuffer(result).reverse().toString();
    }

    private String getDescriptionProductShare(Product product) {
        StringBuilder result = new StringBuilder();
        if (product.getName() == null) return null;
        result.append(product.getContent());
        double price;
        if (product.getUnit() == null) product.setUnitPrice(0.0);
        price = (Double.valueOf(product.getQuantity()) * product.getUnitPrice());
        price = (int) Math.round(price * 10) / (double) 10;
        if (price != 0) {
            String textPrice = parserPrice(price);
            result.append(" " + CURRENCY_DEFUALT + textPrice);
        }
        return result.toString();
    }

    public String getDescription(Product product) {
        String desciption = "";
        double price;
        String note = product.getNote();
        double unitPrice = product.getUnitPrice();
        int quantity = product.getQuantity();
        try {
            if (note == null) {
                note = "";
            }
            price = (Double.valueOf(quantity) * unitPrice);
            price = (int) Math.round(price * 10) / (double) 10;
            if (note.equals("") && price == 0) {
                return "";
            } else if (note.equals("") && price != 0) {
                desciption = CURRENCY_DEFUALT + parserPrice(price);
            } else if (!note.equals("") && price == 0) {
                desciption = note;
            } else if (!note.equals("") && price != 0) {
                desciption = CURRENCY_DEFUALT + parserPrice(price) + " - " + note + "";
            }
            return desciption;
        } catch (Exception e) {
            Log.e("Error", "Product...");
            return "";
        }
    }


    private String nameProductFromTitle(String title) {
        if (title.indexOf("-") != -1) {
            int index = title.lastIndexOf("-");
            title = title.substring(0, index);
        }
        if (title.indexOf(".") != -1) {
            int index = title.lastIndexOf(".");
            title = title.substring(0, index);
        }
        if (title.indexOf("|") != -1) {
            int index = title.lastIndexOf("|");
            title = title.substring(0, index);
        }
        return title;
    }



//screen shopping
    public ArrayList<Product> productSnoozeShopping(ShoppingList list) {
        String query ="select * from product_user " +
                "where id_shopping_list = '" + list.getId() + "' and field_2 = '1' " ;
        ArrayList<Product> result = mProductDao.findByQuery(query);
        return result;
    }

    //get data for shopping
    public ArrayList<Product> productShoppingUnChecked(ShoppingList shoppingList) {
        String listID = shoppingList.getId();
        ArrayList<Product> result = new ArrayList<>();
        String query = "select product_user.* from product_user " +
                "inner join category on id_category = category.id " +
                "where id_shopping_list = '" + listID + "' and is_checked = 0 " +
                "order by category.order_view asc,product_user.order_in_group asc";
        ArrayList<Product> productsHaveCategory = mProductDao.findByQuery(query);
        if (productsHaveCategory.size() != 0) {
            Product productFirst = new Product();
            Category category = this.getCategoryOfProduct(productsHaveCategory.get(0));
            productFirst.setCategory(category);
            productFirst.setShoppingList(shoppingList);
            productsHaveCategory.add(0, productFirst);
            int lengh = productsHaveCategory.size();
            for (int i = 1; i < lengh; i++) {
                //Log.d(TAG, "Name product: " + list.get(i).getCategory().getId());
                String lastType = productsHaveCategory.get(i - 1).getCategory().getId();
                String currentType = productsHaveCategory.get(i).getCategory().getId();
                if (!currentType.equals(lastType)) {
                    //Log.d(TAG,"Add header");
                    Product product = new Product();
                    category = this.getCategoryOfProduct(productsHaveCategory.get(i));
                    product.setCategory(category);
                    product.setShoppingList(shoppingList);
                    productsHaveCategory.add(i, product);
                    lengh++;
                }
                productsHaveCategory.get(i).setCategory(category);
            }
        }
        String query2 = "select * from product_user " +
                "where is_checked = 0 and id_shopping_list = '" + listID + "' and id_category = '" + DEFAULT_CATEGORY_ID + "' " +
                "order by order_in_group asc";
        ArrayList<Product> productsNoCategory = mProductDao.findByQuery(query2);
        Category uncategory = new Category();
        uncategory.setId(DEFAULT_CATEGORY_ID);
        uncategory.setName(mContext.getString(R.string.default_other_category));
        if (productsNoCategory.size() != 0) {
            Product productCategory = new Product();
            productsNoCategory.add(0, productCategory);
        }
        for (Product product : productsNoCategory) {
            product.setCategory(uncategory);
            product.setShoppingList(shoppingList);
            result.add(product);
        }
        result.addAll(productsHaveCategory);
        return result;
    }

    public ArrayList<Product> productShoppingChecked(ShoppingList list) {
        String query ="select * from product_user " +
                "where is_checked = 1 and id_shopping_list = '" + list.getId() + "' " +
                "order by last_checked desc";
        ArrayList<Product> result = mProductDao.findByQuery(query);
        if (result.size() != 0) {
            Product productCategory = new Product();
            productCategory.setChecked(true);
            Category category = new Category();
            category.setId(DEFAULT_CATEGORY_ID);
            category.setName(mContext.getResources().getString(R.string.default_category_bought));
            category.setOrderView(-1);
            productCategory.setCategory(category);
            productCategory.setShoppingList(list);
            result.add(0, productCategory);
        }
        return result;
    }
//Multi Shopping List
}
