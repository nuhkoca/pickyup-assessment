package com.upday.shutterdemo.pickyup.callback;

import android.view.View;
import android.widget.ImageView;

import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.model.remote.Images;

public interface IPopupMenuItemClickListener {
    void onPopupMenuItemClick(Images images, View view, ImageView imageView);

    interface Favorites {
        void onPopupMenuItemClick(FavoriteImages favoriteImages, View view, ImageView imageView);
    }
}