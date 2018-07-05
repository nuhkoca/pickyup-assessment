package com.upday.shutterdemo.pickyup.repository.api;

import com.upday.shutterdemo.pickyup.BuildConfig;
import com.upday.shutterdemo.pickyup.PickyUpApp;
import com.upday.shutterdemo.pickyup.api.IShutterstockAPI;
import com.upday.shutterdemo.pickyup.model.remote.ImagesWrapper;

import retrofit2.Retrofit;
import rx.Observable;

public class EndpointRepository {

    private IShutterstockAPI mIShutterstockAPI;
    private static EndpointRepository INSTANCE;

    private EndpointRepository() {
        mIShutterstockAPI = getRetrofit().create(IShutterstockAPI.class);
    }

    public static EndpointRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EndpointRepository();
        }

        return INSTANCE;
    }

    private static Retrofit getRetrofit() {
        return PickyUpApp.provideRetrofit(BuildConfig.BASE_URL);
    }

    public Observable<ImagesWrapper> getImages(String query, String language, boolean safeSearch, String sort, long page, int perPage) {
        return mIShutterstockAPI.getImages(query, language, safeSearch, sort, page, perPage);
    }
}