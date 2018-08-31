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
    private Stetho.Initializer initializer;

    @Inject
    public AppUtils(Context context, Stetho.Initializer initializer) {
        this.context = context;
        this.initializer = initializer;
    }

    public void initializeNecessaryPlugins() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Stetho.initialize(initializer);
        MobileAds.initialize(context, context.getString(R.string.app_id));
    }
}