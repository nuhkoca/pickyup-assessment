package com.upday.shutterdemo.pickyup.di.module;

import com.upday.shutterdemo.pickyup.ui.favorites.FavoritesFragment;
import com.upday.shutterdemo.pickyup.ui.images.ImagesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilder {

    @ContributesAndroidInjector
    abstract FavoritesFragment contributesFavoritesFragmentInjector();

    @ContributesAndroidInjector
    abstract ImagesFragment contributesImagesFragmentInjector();
}