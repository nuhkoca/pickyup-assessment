package com.upday.shutterdemo.pickyup.adapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.upday.shutterdemo.pickyup.module.GlideApp;

public class ImageBindingAdapter {

    @BindingAdapter(value = {"android:src"})
    public static void bindImage(ImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            GlideApp.with(view.getContext())
                    .asBitmap()
                    .load(url)
                    .into(view);
        }
    }
}