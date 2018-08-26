package com.upday.shutterdemo.pickyup.repository.api;

import com.upday.shutterdemo.pickyup.api.IShutterstockAPI;
import com.upday.shutterdemo.pickyup.model.remote.ImagesWrapper;

import javax.inject.Inject;

import rx.Observable;

public class EndpointRepository {

    private IShutterstockAPI iShutterstockAPI;

    @Inject
    public EndpointRepository(IShutterstockAPI iShutterstockAPI) {
        this.iShutterstockAPI = iShutterstockAPI;
    }

    public Observable<ImagesWrapper> getImages(String query, String language, boolean safeSearch, String sort, long page, int perPage) {
        return iShutterstockAPI.getImages(query, language, safeSearch, sort, page, perPage);
    }
}