package com.upday.shutterdemo.pickyup.utils;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.upday.shutterdemo.pickyup.BuildConfig;
import com.upday.shutterdemo.pickyup.R;

import timber.log.Timber;

public class AppUtils {

    public static void initializeNecessaryPlugins(Context context){
        provideTimber();
        provideStetho(context);
        provideMobileAds(context);
    }

    private static void provideTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private static void provideStetho(Context context) {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(context);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(context));

        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    private static void provideMobileAds(Context context) {
        MobileAds.initialize(context, context.getString(R.string.app_id));
    }
}
