package com.upday.shutterdemo.pickyup.ui.images.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.remote.Images;
import com.upday.shutterdemo.pickyup.model.remote.ImagesWrapper;
import com.upday.shutterdemo.pickyup.repository.api.EndpointRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PageKeyedImagesDataSource extends PageKeyedDataSource<Long, Images> {

    private EndpointRepository mEndpointRepository;

    private MutableLiveData<NetworkState> mNetworkState;
    private MutableLiveData<NetworkState> mInitialLoading;

    private String mQuery;
    private String mLanguage;
    private boolean mSafeSearch;
    private String mSort;

    PageKeyedImagesDataSource(String query, String language, boolean safeSearch, String sort) {
        mEndpointRepository = EndpointRepository.getInstance();

        mNetworkState = new MutableLiveData<>();
        mInitialLoading = new MutableLiveData<>();

        this.mQuery = query;
        this.mLanguage = language;
        this.mSafeSearch = safeSearch;
        this.mSort = sort;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<NetworkState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull final LoadInitialCallback<Long, Images> callback) {
        final List<Images> imagesList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);
        mInitialLoading.postValue(NetworkState.LOADING);

        mEndpointRepository.getImages(mQuery, mLanguage, mSafeSearch, mSort, 1, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ImagesWrapper>>() {
                    @Override
                    public Observable<? extends ImagesWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ImagesWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));
                        mInitialLoading.postValue(new NetworkState(NetworkState.Status.FAILED));

                        Timber.e(e);
                    }

                    @Override
                    public void onNext(ImagesWrapper imagesWrapper) {
                        if (imagesWrapper.getTotalCount() > 0
                                && imagesWrapper.getData() != null
                                && imagesWrapper.getData().size() > 0) {

                            imagesList.addAll(imagesWrapper.getData());
                            callback.onResult(imagesList, null, 2L);

                            mNetworkState.postValue(NetworkState.LOADED);
                            mInitialLoading.postValue(NetworkState.LOADED);
                        } else {
                            mNetworkState.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
                            mInitialLoading.postValue(new NetworkState(NetworkState.Status.NO_ITEM));
                        }
                    }
                });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Images> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Long> params, @NonNull final LoadCallback<Long, Images> callback) {
        final List<Images> imagesList = new ArrayList<>();

        mNetworkState.postValue(NetworkState.LOADING);

        mEndpointRepository.getImages(mQuery, mLanguage, mSafeSearch, mSort, params.key, params.requestedLoadSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ImagesWrapper>>() {
                    @Override
                    public Observable<? extends ImagesWrapper> call(Throwable throwable) {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Subscriber<ImagesWrapper>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetworkState.postValue(new NetworkState(NetworkState.Status.FAILED));

                        Timber.e(e);
                    }

                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onNext(ImagesWrapper imagesWrapper) {
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
                });
    }
}