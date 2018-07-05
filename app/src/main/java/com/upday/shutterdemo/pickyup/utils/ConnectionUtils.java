package com.upday.shutterdemo.pickyup.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.upday.shutterdemo.pickyup.PickyUpApp;

public class ConnectionUtils {
    public static boolean sniff() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) PickyUpApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        return networkInfo != null &&
                networkInfo.isConnected() &&
                networkInfo.isConnectedOrConnecting() &&
                networkInfo.isAvailable();
    }
}
