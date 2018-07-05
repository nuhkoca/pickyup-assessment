package com.upday.shutterdemo.pickyup.ui.favorites;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;

public class FavoritesFragmentViewModelFactory implements ViewModelProvider.Factory {

    private FavoriteImagesDao mFavoriteImagesDao;

    FavoritesFragmentViewModelFactory(FavoriteImagesDao favoriteImagesDao) {
        this.mFavoriteImagesDao = favoriteImagesDao;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoritesFragmentViewModel(mFavoriteImagesDao);
    }
}