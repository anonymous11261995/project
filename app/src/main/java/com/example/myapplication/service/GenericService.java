package com.example.myapplication.service;

import android.content.Context;

import com.example.myapplication.dao.CategoryDao;
import com.example.myapplication.dao.ProductDao;
import com.example.myapplication.dao.GroceryDao;
import com.example.myapplication.entity.Category;
import com.example.myapplication.entity.Grocery;
import com.example.myapplication.entity.Product;
import com.example.myapplication.helper.DatabaseHelper;
import com.example.myapplication.utils.DefinitionSchema;

import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericService implements DefinitionSchema {
    ProductDao mProductDao;
    GroceryDao mShoppingListDao;
    CategoryDao mCategoryDao;
    Context mContext;

    public GenericService(Context context) {
        this.mProductDao = DatabaseHelper.mProductDao;
        this.mShoppingListDao = DatabaseHelper.mShoppingListDao;
        this.mCategoryDao = DatabaseHelper.mCategoryDao;
        this.mContext = context;
    }


    public String createCodeId(String name) {
        String textTime = String.valueOf(new Date().getTime());
        String code = name + "-" + textTime;
        return convertStringToUrl(code);
    }

    public boolean checkBeforeUpdateList(String name) {
        if (name.trim().equals("")) {
            return false;
        }
        ArrayList<Grocery> groceries = mShoppingListDao.fetchAllShoppingList();
        for (Grocery grocery : groceries) {
            if (name.equals(grocery.getName())) {
                return false;
            }
        }
        return true;
    }

    protected String convertStringToUrl(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d").replaceAll("[^a-zA-Z0-9-]", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    void orderProductInGroup(Category category, Grocery grocery) {
        String idCategory = category.getId();
        ArrayList<Product> list = mProductDao.getProductByCategoryAndShopping(idCategory, grocery.getId());
        for (int i = 0; i < list.size(); i++) {
            Product product = list.get(i);
            product.setOrderInGroup(i + 1);
            mProductDao.update(product);
        }
    }

    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public int regexNumber(String text) {
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(text);
        while (m.find()) {
            return Integer.parseInt(m.group());
        }
        return 0;
    }

    List<String> sortByFrequencyItem(List<String> input) {
        List<String> listLowerCase = new ArrayList<>();
        for (String item : input) {
            listLowerCase.add(item.toLowerCase().trim());
        }

        Map<String, Integer> map = new HashMap<>();
        for (String s : listLowerCase) {
            if (map.containsKey(s)) {
                map.put(s, map.get(s) + 1);
            } else {
                map.put(s, 1);
            }
        }
        Set<Map.Entry<String, Integer>> set = map.entrySet();
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        List<String> output = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : list) {
            output.add(upperCaseFirstChar(entry.getKey()));
        }
        return output;
    }

    public String upperCaseFirstChar(String text) {
        if (text == null) return "";
        if (text.equals("")) return "";
        return text.toUpperCase().charAt(0) + text.substring(1, text.length());
    }



    public String escapeCharacterSpecial(String text) {
        return text.replace("'", "&#39;");

    }

    public JSONObject paserQuantityUnit(String text) {
        JSONObject json = new JSONObject();
        try {
            String name = text;
            float quanity = 1;
            String unit = "";
            String array[] = text.split(" ");
            for (int i = 0; i < array.length; i++) {
                //System.out.println(array[i]);
                if (array[i].matches("[+-]?([0-9]*[.])?[0-9]+") && i + 1 < array.length) {
                    quanity = Float.valueOf(array[i]);
                    unit = array[i + 1];
                }
            }
            String unitQuantity = String.valueOf(quanity).replace(".0", "") + " " + unit;
            json.put("name", name.replace(unitQuantity, "").trim());
            json.put("unit", unit);
            json.put("quantity", quanity);

        } catch (Exception e) {

        }
        return json;

    }

}
