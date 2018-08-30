package com.upday.shutterdemo.pickyup.model.remote;

import com.upday.shutterdemo.pickyup.api.IShutterstockAPI;
import com.upday.shutterdemo.pickyup.model.remote.data.ImagesWrapper;

import javax.inject.Inject;

import io.reactivex.Single;

public class EndpointRepository {

    private IShutterstockAPI iShutterstockAPI;

    @Inject
    public EndpointRepository(IShutterstockAPI iShutterstockAPI) {
        this.iShutterstockAPI = iShutterstockAPI;
    }

    public Single<ImagesWrapper> getImages(String query, String language, boolean safeSearch, String sort, long page, int perPage) {
        return iShutterstockAPI.getImages(query, language, safeSearch, sort, page, perPage);
    }
}