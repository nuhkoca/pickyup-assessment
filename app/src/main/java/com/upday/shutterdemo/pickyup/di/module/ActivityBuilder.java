package com.upday.shutterdemo.pickyup.di.module;

import com.upday.shutterdemo.pickyup.ui.MainActivity;
import com.upday.shutterdemo.pickyup.ui.WebViewActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract MainActivity contributesMainActivityInjector();

    @ContributesAndroidInjector
    abstract WebViewActivity contributesWebViewActivityInjector();
}
