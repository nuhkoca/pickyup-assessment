package com.upday.shutterdemo.pickyup.di.module;

import com.upday.shutterdemo.pickyup.ui.MainActivity;
import com.upday.shutterdemo.pickyup.ui.WebViewActivity;
import com.upday.shutterdemo.pickyup.ui.favorites.FavoritesFragment;
import com.upday.shutterdemo.pickyup.ui.images.ImagesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract MainActivity contributesMainActivityInjector();

    @ContributesAndroidInjector
    abstract WebViewActivity contributesWebViewActivityInjector();

    @ContributesAndroidInjector
    abstract FavoritesFragment contributesFavoritesFragmentInjector();

    @ContributesAndroidInjector
    abstract ImagesFragment contributesImagesFragmentInjector();
}
