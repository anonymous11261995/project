package com.example.myapplication.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by TienTruong on 10/6/2018.
 */

public class AppUtil {
    public static String SHARE_PREFERENCES_PREF_NAME = "MyApp";

    public static String convertStringToUrl(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD).trim();
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d")
                    .replaceAll("[^a-zA-Z0-9-]", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
