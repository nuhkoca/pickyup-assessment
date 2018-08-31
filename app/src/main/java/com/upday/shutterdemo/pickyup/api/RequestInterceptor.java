package com.upday.shutterdemo.pickyup.api;

import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + BuildConfig.BEARER_TOKEN)
                .build();
        return chain.proceed(newRequest);
    }
}