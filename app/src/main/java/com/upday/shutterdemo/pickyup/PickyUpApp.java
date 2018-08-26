package com.upday.shutterdemo.pickyup;

import com.upday.shutterdemo.pickyup.di.component.AppComponent;
import com.upday.shutterdemo.pickyup.di.component.DaggerAppComponent;
import com.upday.shutterdemo.pickyup.utils.AppUtils;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class PickyUpApp extends DaggerApplication {

    private static PickyUpApp INSTANCE;

    public static PickyUpApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        AppUtils.initializeNecessaryPlugins(getApplicationContext());
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();

        appComponent.inject(this);

        return appComponent;
    }
}