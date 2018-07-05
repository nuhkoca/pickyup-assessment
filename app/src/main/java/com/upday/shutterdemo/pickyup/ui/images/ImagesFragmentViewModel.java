package com.upday.shutterdemo.pickyup.ui.images;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.helper.AppsExecutor;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.remote.Images;
import com.upday.shutterdemo.pickyup.ui.images.paging.ImageResultDataSourceFactory;
import com.upday.shutterdemo.pickyup.ui.images.paging.PageKeyedImagesDataSource;

public class ImagesFragmentViewModel extends ViewModel {

    private LiveData<NetworkState> mNetworkState;
    private LiveData<NetworkState> mInitialLoading;
    private LiveData<PagedList<Images>> mImagesResult;
    private LiveData<PagedList<Images>> mImagesResult1;
    private ImageResultDataSourceFactory imageResultDataSourceFactory;

    ImagesFragmentViewModel(ImageResultDataSourceFactory imageResultDataSourceFactory) {
        this.imageResultDataSourceFactory = imageResultDataSourceFactory;

        mNetworkState = Transformations.switchMap(this.imageResultDataSourceFactory.getPageKeyedImagesDataSourceMutableLiveData(), new Function<PageKeyedImagesDataSource, LiveData<NetworkState>>() {
            @Override
            public LiveData<NetworkState> apply(PageKeyedImagesDataSource input) {
                return input.getNetworkState();
            }
        });

        mInitialLoading = Transformations.switchMap(this.imageResultDataSourceFactory.getPageKeyedImagesDataSourceMutableLiveData(),
                new Function<PageKeyedImagesDataSource, LiveData<NetworkState>>() {
                    @Override
                    public LiveData<NetworkState> apply(PageKeyedImagesDataSource input) {
                        return input.getInitialLoading();
                    }
                });

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(Constants.DEFAULT_INITIAL_LOAD_SIZE) //first load
                .setPrefetchDistance(Constants.DEFAULT_INITIAL_LOAD_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE) //page size
                .build();

        mImagesResult = new LivePagedListBuilder<>(this.imageResultDataSourceFactory, config)
                .setFetchExecutor(AppsExecutor.networkIO())
                .build();
    }

    public LiveData<PagedList<Images>> getImagesResult() {
        return mImagesResult;
    }

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    public LiveData<PagedList<Images>> refreshImagesResult() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(Constants.DEFAULT_INITIAL_LOAD_SIZE) //first load
                .setPrefetchDistance(Constants.DEFAULT_INITIAL_LOAD_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE) //page size
                .build();

        mImagesResult1 = new LivePagedListBuilder<>(this.imageResultDataSourceFactory, config)
                .setFetchExecutor(AppsExecutor.networkIO())
                .build();

        return mImagesResult1;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}