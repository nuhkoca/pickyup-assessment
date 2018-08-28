package com.upday.shutterdemo.pickyup.ui.images.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.remote.Images;
import com.upday.shutterdemo.pickyup.model.remote.ImagesWrapper;
import com.upday.shutterdemo.pickyup.repository.api.EndpointRepository;
import com.upday.shutterdemo.pickyup.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class PageKeyedImagesDataSource extends PageKeyedDataSource<Long, Images> {

    private EndpointRepository endpointRepository;

    private CompositeDisposable compositeDisposable;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;

    @Inject
    PageKeyedImagesDataSource(EndpointRepository endpointRepository) {
        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

        this.endpointRepository = endpointRepository;

        compositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    private String getQuery() {
        return sharedPreferencesUtils.getStringData(Constants.QUERY_PREF_KEY, Constants.DEFAULT_QUERY_VALUE);
    }

    private String getLanguage() {
        return sharedPreferencesUtils.getStringData(Constants.LANG_PREF_KEY, Constants.DEFAULT_LANG_VALUE);
    }

    private boolean getSafeSearch() {
        return sharedPreferencesUtils.getBooleanData(Constants.SAFE_SEARCH_PREF_KEY, Constants.DEFAULT_SAFE_SEARCH_VALUE);
    }

    private String getSort() {
        return sharedPreferencesUtils.getStringData(Constants.SORT_PREF_KEY, Constants.DEFAULT_SORT_VALUE);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, Images> callback) {
        final List<Images> imagesList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        Disposable imageDisposable = endpointRepository.getImages(getQuery(), getLanguage(), getSafeSearch(), getSort(), 1, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .subscribe(images -> onImagesFetched(images, imagesList, callback), this::onError);

        compositeDisposable.add(imageDisposable);
    }

    private void onError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    private void onImagesFetched(ImagesWrapper imagesWrapper, List<Images> imagesList, LoadInitialCallback<Long, Images> callback) {
        if (imagesWrapper.getTotalCount() > 0
                && imagesWrapper.getData() != null
                && imagesWrapper.getData().size() > 0) {

            imagesList.addAll(imagesWrapper.getData());

            callback.onResult(imagesList, null, 2L);

            mNetworkState.postValue(NetworkState.LOADED);
        } else {
            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Images> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, Images> callback) {
        final List<Images> imagesList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        Disposable imagesDisposable = endpointRepository.getImages(getQuery(), getLanguage(), getSafeSearch(), getSort(), params.key, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .subscribe(images -> onPaginationImagesFetched(images, imagesList, callback, params), this::onPaginationError);

        compositeDisposable.add(imagesDisposable);
    }

    private void onPaginationError(Throwable throwable) {
        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
    }

    @SuppressWarnings("ConstantConditions")
    private void onPaginationImagesFetched(ImagesWrapper imagesWrapper, List<Images> imagesList, LoadCallback<Long, Images> callback, LoadParams<Long> params) {
        if (imagesWrapper.getTotalCount() > 0
                && imagesWrapper.getData() != null
                && imagesWrapper.getData().size() > 0) {

            imagesList.addAll(imagesWrapper.getData());

            long nextKey = (params.key == imagesWrapper.getTotalCount()) ? null : params.key + 1;
            callback.onResult(imagesList, nextKey);

            mNetworkState.postValue(NetworkState.LOADED);
        } else {
            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
        }
    }

    public void clear() {
        compositeDisposable.clear();
    }
}