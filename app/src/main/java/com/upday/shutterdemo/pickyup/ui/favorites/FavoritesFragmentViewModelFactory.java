package com.upday.shutterdemo.pickyup.ui.favorites;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.repository.db.FavoriteImagesRepository;

public class FavoritesFragmentViewModelFactory implements ViewModelProvider.Factory {

    private FavoriteImagesRepository mFavoriteImagesRepository;

    FavoritesFragmentViewModelFactory(FavoriteImagesRepository favoriteImagesRepository) {
        this.mFavoriteImagesRepository = favoriteImagesRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoritesFragmentViewModel(mFavoriteImagesRepository);
    }
}