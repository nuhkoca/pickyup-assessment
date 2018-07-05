package com.upday.shutterdemo.pickyup.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.upday.shutterdemo.pickyup.model.remote.ImagesWrapper;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface IShutterstockAPI {

    @GET("v2/images/search")
    Observable<ImagesWrapper> getImages(@Query("query") @NonNull String query,
                                        @Query("language") @Nullable String language,
                                        @Query("safe") boolean safe,
                                        @Query("sort") @NonNull String sort,
                                        @Query("page") long page,
                                        @Query("per_page") int perPage);
}