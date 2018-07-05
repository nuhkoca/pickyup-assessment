package com.upday.shutterdemo.pickyup.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.upday.shutterdemo.pickyup.PickyUpApp;
import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.helper.Constants;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesUtils {

    private static SharedPreferences mSharedPref;

    private static SharedPreferencesUtils INSTANCE;

    public static synchronized SharedPreferencesUtils getInstance() {
        mSharedPref = PickyUpApp.getInstance().getSharedPreferences(Constants.PICKYUP_SHARED_PREF, MODE_PRIVATE);

        if (INSTANCE == null) {
            INSTANCE = new SharedPreferencesUtils();
        }

        return INSTANCE;
    }

    public String getQuery() {
        return mSharedPref.getString(Constants.QUERY_PREF_KEY, Constants.DEFAULT_QUERY_VALUE);
    }

    public void saveQuery(String query) {
        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(Constants.QUERY_PREF_KEY, query);

        Timber.d("Query is %s", query);

        editor.apply();
    }

    public synchronized int getRunCount() {
        return mSharedPref.getInt("run-count", 0);
    }

    public synchronized void setRunCount() {
        SharedPreferences.Editor editor = mSharedPref.edit();

        int count = getRunCount() + 1;

        editor.putInt("run-count", count);

        editor.apply();
    }

    public static String loadLanguagePreference(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(context.getString(R.string.image_language_pref_key),
                context.getString(R.string.en_lang_value));
    }

    public static String loadSortingPreference(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(context.getString(R.string.image_sorting_pref_key),
                context.getString(R.string.popular_value));
    }

    public static boolean loadSafeSearchPreference(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(context.getString(R.string.image_safe_search_pref_key),
                true);
    }
}