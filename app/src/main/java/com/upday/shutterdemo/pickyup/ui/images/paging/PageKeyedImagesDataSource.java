package com.upday.shutterdemo.pickyup.ui.images.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.content.Context;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.api.Status;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.remote.EndpointRepository;
import com.upday.shutterdemo.pickyup.model.remote.data.Images;
import com.upday.shutterdemo.pickyup.model.remote.data.ImagesWrapper;
import com.upday.shutterdemo.pickyup.util.SharedPreferencesUtils;

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

    private SharedPreferencesUtils sharedPreferencesUtils;

    private Context context;

    @Inject
    PageKeyedImagesDataSource(EndpointRepository endpointRepository, Context context, SharedPreferencesUtils sharedPreferencesUtils) {
        this.endpointRepository = endpointRepository;
        this.context = context;
        this.sharedPreferencesUtils = sharedPreferencesUtils;

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

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
        return sharedPreferencesUtils.getStringData(context.getString(R.string.image_language_pref_key),
                context.getString(R.string.en_lang_value));
    }

    private boolean getSafeSearch() {
        return sharedPreferencesUtils.getBooleanData(context.getString(R.string.image_safe_search_pref_key), true);
    }

    private String getSort() {
        return sharedPreferencesUtils.getStringData(context.getString(R.string.image_sorting_pref_key),
                context.getString(R.string.popular_value));
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
        mInitialLoading.postValue(new NetworkState(Status.FAILED));
        mNetworkState.postValue(new NetworkState(Status.FAILED));
    }

    private void onImagesFetched(ImagesWrapper imagesWrapper, List<Images> imagesList, LoadInitialCallback<Long, Images> callback) {
        if (imagesWrapper.getTotalCount() > 0
                && imagesWrapper.getData() != null
                && imagesWrapper.getData().size() > 0) {

            imagesList.addAll(imagesWrapper.getData());

            callback.onResult(imagesList, null, 2L);

            mNetworkState.postValue(NetworkState.LOADED);
            mInitialLoading.postValue(NetworkState.LOADED);
        } else {
            mNetworkState.postValue(new NetworkState(Status.NO_ITEM));
            mInitialLoading.postValue(new NetworkState(Status.NO_ITEM));
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
        mNetworkState.postValue(new NetworkState(Status.FAILED));
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
            mNetworkState.postValue(new NetworkState(Status.NO_ITEM));
        }
    }

    public void clear() {
        compositeDisposable.clear();
    }
}