package com.upday.shutterdemo.pickyup.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.upday.shutterdemo.pickyup.helper.Constants;

public class ColumnUtils {
    public static int getOptimalNumberOfColumn(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (context != null) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }

        int widthDivider = Constants.DEFAULT_COLUMN_WIDTH;

        if (context != null) {
            int width = displayMetrics.widthPixels;
            int nColumns = width / widthDivider;
            if (nColumns < 2) return 2;
            return nColumns;
        }

        return 2;
    }
}