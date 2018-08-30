package com.upday.shutterdemo.pickyup.ui.images.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import com.upday.shutterdemo.pickyup.model.remote.data.Images;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ImageResultDataSourceFactory extends DataSource.Factory<Long, Images> {

    private PageKeyedImagesDataSource pageKeyedImagesDataSource;

    private MutableLiveData<PageKeyedImagesDataSource> mPageKeyedImagesDataSourceMutableLiveData;

    @Inject
    public ImageResultDataSourceFactory(PageKeyedImagesDataSource pageKeyedImagesDataSource) {
        this.pageKeyedImagesDataSource = pageKeyedImagesDataSource;
        this.mPageKeyedImagesDataSourceMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource<Long, Images> create() {
        mPageKeyedImagesDataSourceMutableLiveData.postValue(pageKeyedImagesDataSource);

        return pageKeyedImagesDataSource;
    }

    public MutableLiveData<PageKeyedImagesDataSource> getPageKeyedImagesDataSourceMutableLiveData() {
        return mPageKeyedImagesDataSourceMutableLiveData;
    }

    public PageKeyedImagesDataSource getPageKeyedImagesDataSource() {
        return pageKeyedImagesDataSource;
    }
}