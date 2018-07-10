package com.upday.shutterdemo.pickyup.callback;

import android.view.View;
import android.widget.ImageView;

public interface IPopupMenuItemClickListener<T> {
    void onPopupMenuItemClick(T t, View view, ImageView imageView);
}