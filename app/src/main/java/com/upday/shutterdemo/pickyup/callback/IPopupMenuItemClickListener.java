package com.upday.shutterdemo.pickyup.callback;

import android.view.View;

import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.model.remote.Images;

public interface IPopupMenuItemClickListener {
    void onPopupMenuItemClick(Images images, View view);

    interface Favorites {
        void onPopupMenuItemClick(FavoriteImages favoriteImages, View view);
    }
}