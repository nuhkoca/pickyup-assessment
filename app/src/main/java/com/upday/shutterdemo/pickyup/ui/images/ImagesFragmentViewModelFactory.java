package com.upday.shutterdemo.pickyup.ui.images;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.ui.images.paging.ImageResultDataSourceFactory;

public class ImagesFragmentViewModelFactory implements ViewModelProvider.Factory {

    private ImageResultDataSourceFactory mImageResultDataSourceFactory;

    ImagesFragmentViewModelFactory(ImageResultDataSourceFactory imageResultDataSourceFactory) {
        this.mImageResultDataSourceFactory = imageResultDataSourceFactory;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ImagesFragmentViewModel(mImageResultDataSourceFactory);
    }
}