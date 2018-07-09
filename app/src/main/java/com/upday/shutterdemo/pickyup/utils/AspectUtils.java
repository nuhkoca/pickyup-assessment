package com.upday.shutterdemo.pickyup.utils;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

import java.util.Locale;

public class AspectUtils {
    public static void calculateAndApplyTo(double width, double height, ConstraintLayout constraintLayout, int viewId) {
        ConstraintSet set = new ConstraintSet();

        String ratio = String.format(
                Locale.US,
                "%f:%f",
                width,
                height);

        set.clone(constraintLayout);
        set.setDimensionRatio(viewId, ratio);
        set.applyTo(constraintLayout);
    }
}