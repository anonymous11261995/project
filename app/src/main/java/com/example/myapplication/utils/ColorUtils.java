package com.example.myapplication.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;


import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Random;

public class ColorUtils {
    /**
     * Returns true if the text color should be white, given a background color
     *
     * @param color background color
     * @return true if the text should be white, false if the text should be black
     */
    public static boolean isWhiteText(@ColorInt final int color) {
        final int red = Color.red(color);
        final int green = Color.green(color);
        final int blue = Color.blue(color);

        // https://en.wikipedia.org/wiki/YIQ
        // https://24ways.org/2010/calculating-color-contrast/
        final int yiq = ((red * 299) + (green * 587) + (blue * 114)) / 1000;
        return yiq < 192;
    }

    public static int getDimensionDp(int resID, Context context) {
        return (int) (context.getResources().getDimension(resID) / context.getResources().getDisplayMetrics().density);
    }

    public static int dip2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int randomColor(Context context) {
        TypedArray ta;
        ta = context.getResources().obtainTypedArray(R.array.default_colors_picker);
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < ta.length(); i++) {
            colors.add(ta.getColor(i, 0));
        }
        return colors.get(new Random().nextInt(colors.size()));
    }

}
