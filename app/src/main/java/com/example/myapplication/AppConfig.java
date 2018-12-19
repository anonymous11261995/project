package com.example.myapplication;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by TienTruong on 10/6/2018.
 */

public class AppConfig {
    public static final String URL_STORE = "https://play.google.com/store/apps/details?id=";
    public static final String URL_REDIRECT_TO_STORE = "http://get.blistapp.com";
    //
    public static final String DATABASE_NAME = "data.db";
    public static final int DATABASE_VERSION = 1;
    // time 12h:  12 * 3600
    public static final int PENDING_TIMEOUT = 2000;
    public static final int DELAY_EFFECT = 350;
    public static final int DELAY_TEXT_CHANED = 500;
    public static final long CONFIG_EXPIRE_SECOND = 60;
    public static final String DATE_SERVER_PATTERN = "yyyy-MM-dd";
    public static final String DATE_SERVER_PATTERN_FULL = "yyyy-MM-dd HH:mm";
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    public static int ITEM_CACHE_LIST = 40;

    public static String getCurrencySymbol() {
        try {
            Currency currency = Currency.getInstance(Locale.getDefault());
            return currency.getSymbol();
        } catch (Exception e) {
            return "$";
        }

    }

    private AppConfig() {
    }


}
