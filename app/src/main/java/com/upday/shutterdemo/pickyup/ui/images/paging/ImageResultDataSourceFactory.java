package com.upday.shutterdemo.pickyup.ui.images.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.upday.shutterdemo.pickyup.model.remote.Images;

public class ImageResultDataSourceFactory extends DataSource.Factory<Long, Images> {

    private MutableLiveData<PageKeyedImagesDataSource> mPageKeyedImagesDataSourceMutableLiveData;

    private static ImageResultDataSourceFactory INSTANCE;

    private static String mQuery;
    private static String mLanguage;
    private static boolean mSafeSearch;
    private static String mSort;

    private ImageResultDataSourceFactory() {
        this.mPageKeyedImagesDataSourceMutableLiveData = new MutableLiveData<>();
    }

    public synchronized static ImageResultDataSourceFactory getInstance(String query, String language, boolean safeSearch, String sort) {
        if (INSTANCE == null) {
            INSTANCE = new ImageResultDataSourceFactory();
        }

        mQuery = query;
        mLanguage = language;
        mSafeSearch = safeSearch;
        mSort = sort;

        return INSTANCE;
    }

    @Override
    public DataSource<Long, Images> create() {
        PageKeyedImagesDataSource pageKeyedImagesDataSource = new PageKeyedImagesDataSource(mQuery, mLanguage, mSafeSearch, mSort);
        mPageKeyedImagesDataSourceMutableLiveData.postValue(pageKeyedImagesDataSource);

        return pageKeyedImagesDataSource;
    }

    public MutableLiveData<PageKeyedImagesDataSource> getPageKeyedImagesDataSourceMutableLiveData() {
        return mPageKeyedImagesDataSourceMutableLiveData;
    }
}