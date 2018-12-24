package com.example.myapplication.service;

import android.content.Context;

import com.example.myapplication.dao.ProductDao;
import com.example.myapplication.dao.GroceryDao;
import com.example.myapplication.helper.DatabaseHelper;

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

public class GenericService {
    ProductDao mProductDao;
    GroceryDao mGroceryDao;
    Context mContext;

    public GenericService(Context context) {
        this.mProductDao = DatabaseHelper.mProductDao;
        this.mGroceryDao = DatabaseHelper.mGroceryDao;
        this.mContext = context;
    }


    public String createCodeId(String name) {
        String textTime = String.valueOf(new Date().getTime());
        String code = name + "-" + textTime;
        return convertStringToUrl(code);
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


}
