package com.upday.shutterdemo.pickyup.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.upday.shutterdemo.pickyup.di.qualifier.ViewModelKey;
import com.upday.shutterdemo.pickyup.ui.favorites.FavoritesFragmentViewModel;
import com.upday.shutterdemo.pickyup.ui.images.ImagesFragmentViewModel;
import com.upday.shutterdemo.pickyup.viewmodel.PickyUpViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesFragmentViewModel.class)
    abstract ViewModel bindsFavoritesFragmentViewModel(FavoritesFragmentViewModel favoritesFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImagesFragmentViewModel.class)
    abstract ViewModel bindsImagesFragmentViewModel(ImagesFragmentViewModel imagesFragmentViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindsPickyUpViewModelFactory(PickyUpViewModelFactory pickyUpViewModelFactory);

}
