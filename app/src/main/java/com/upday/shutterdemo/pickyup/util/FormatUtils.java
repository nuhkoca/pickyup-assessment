package com.upday.shutterdemo.pickyup.util;

import java.util.Locale;

public class FormatUtils {

    public static String formatFloat(float f) {
        return String.format(Locale.US, "%.2f", f);
    }
}