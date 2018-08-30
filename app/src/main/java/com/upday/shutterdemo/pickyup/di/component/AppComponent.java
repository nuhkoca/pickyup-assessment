package com.upday.shutterdemo.pickyup.di.component;

import android.app.Application;

import com.upday.shutterdemo.pickyup.PickyUpApp;
import com.upday.shutterdemo.pickyup.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class})
public interface AppComponent extends AndroidInjector<PickyUpApp> {

    @Override
    void inject(PickyUpApp instance);

    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
