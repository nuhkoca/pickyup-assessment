package com.upday.shutterdemo.pickyup.util;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.upday.shutterdemo.pickyup.BuildConfig;
import com.upday.shutterdemo.pickyup.R;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class AppUtils {

    private Context context;

    @Inject
    public AppUtils(Context context) {
        this.context = context;
    }

    public void initializeNecessaryPlugins() {
        provideTimber();
        provideStetho();
        provideMobileAds();
    }

    private void provideTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void provideStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(context);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(context));

        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    private void provideMobileAds() {
        MobileAds.initialize(context, context.getString(R.string.app_id));
    }
}