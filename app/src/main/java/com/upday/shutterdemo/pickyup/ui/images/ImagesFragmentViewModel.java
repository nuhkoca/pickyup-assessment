package com.upday.shutterdemo.pickyup.ui.images;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.widget.ImageView;

import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.helper.AppsExecutor;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.model.remote.data.Images;
import com.upday.shutterdemo.pickyup.model.local.FavoriteImagesRepository;
import com.upday.shutterdemo.pickyup.ui.images.paging.ImageResultDataSourceFactory;
import com.upday.shutterdemo.pickyup.ui.images.paging.PageKeyedImagesDataSource;
import com.upday.shutterdemo.pickyup.util.FirebaseMLKitUtils;

import javax.inject.Inject;

public class ImagesFragmentViewModel extends ViewModel {

    private LiveData<NetworkState> mNetworkState;
    private LiveData<NetworkState> mInitialLoading;
    private LiveData<PagedList<Images>> mImagesResult;
    private MutableLiveData<Boolean> mIsAdded;

    private ImageResultDataSourceFactory imageResultDataSourceFactory;

    private FirebaseMLKitUtils firebaseMLKitUtils;
    private AppsExecutor appsExecutor;
    private FavoriteImagesRepository favoriteImagesRepository;

    @Inject
    public ImagesFragmentViewModel(ImageResultDataSourceFactory imageResultDataSourceFactory, AppsExecutor appsExecutor, FirebaseMLKitUtils firebaseMLKitUtils, FavoriteImagesRepository favoriteImagesRepository) {
        this.imageResultDataSourceFactory = imageResultDataSourceFactory;
        this.appsExecutor = appsExecutor;
        this.firebaseMLKitUtils = firebaseMLKitUtils;
        this.favoriteImagesRepository = favoriteImagesRepository;

        mIsAdded = new MutableLiveData<>();
        mIsAdded.postValue(false);

        mNetworkState = Transformations.switchMap(this.imageResultDataSourceFactory.getPageKeyedImagesDataSourceMutableLiveData(), PageKeyedImagesDataSource::getNetworkState);

        mInitialLoading = Transformations.switchMap(this.imageResultDataSourceFactory.getPageKeyedImagesDataSourceMutableLiveData(),
                PageKeyedImagesDataSource::getInitialLoading);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(Constants.DEFAULT_INITIAL_LOAD_SIZE) //first load
                .setPrefetchDistance(Constants.DEFAULT_INITIAL_LOAD_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE) //page size
                .build();

        mImagesResult = new LivePagedListBuilder<>(this.imageResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
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

        mImagesResult = new LivePagedListBuilder<>(this.imageResultDataSourceFactory, config)
                .setFetchExecutor(appsExecutor.networkIO())
                .build();

        return mImagesResult;
    }

    public void generateLabelsFromBitmap(final ImageView imageView) {
        firebaseMLKitUtils.generateLabelsFromBitmap(imageView);
    }

    public MutableLiveData<Boolean> addToDb(Images images) {
        String iid = images.getId();
        final String description = images.getDescription();
        String url = images.getAssets().getHugeThumb().getUrl();
        double height = images.getAssets().getHugeThumb().getHeight();
        double width = images.getAssets().getHugeThumb().getWidth();

        FavoriteImages favoriteImages = new FavoriteImages(
                iid,
                url,
                description,
                height,
                width
        );

        favoriteImagesRepository.insertOrThrow(favoriteImages, iid, result -> {
            if (result){
                mIsAdded.postValue(false);
            }else {
                mIsAdded.postValue(true);
            }
        });

        return mIsAdded;
    }

    @Override
    protected void onCleared() {
        imageResultDataSourceFactory.getPageKeyedImagesDataSource().clear();

        super.onCleared();
    }
}