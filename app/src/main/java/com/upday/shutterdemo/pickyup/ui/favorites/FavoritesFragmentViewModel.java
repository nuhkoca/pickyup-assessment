package com.upday.shutterdemo.pickyup.ui.favorites;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;

public class FavoritesFragmentViewModel extends ViewModel {

    private LiveData<PagedList<FavoriteImages>> mFavoriteImagesList;

    private FavoriteImagesDao mFavoriteImagesDao;

    FavoritesFragmentViewModel(FavoriteImagesDao favoriteImagesDao) {
        this.mFavoriteImagesDao = favoriteImagesDao;

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.PER_PAGE_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE).build();

        mFavoriteImagesList = new LivePagedListBuilder<>(mFavoriteImagesDao.getAll(), config).build();
    }

    public LiveData<PagedList<FavoriteImages>> getFavoriteImagesList() {
        return mFavoriteImagesList;
    }

    public LiveData<PagedList<FavoriteImages>> retrieveFavoriteImagesList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.PER_PAGE_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE).build();

        mFavoriteImagesList = new LivePagedListBuilder<>(mFavoriteImagesDao.getAll(), config).build();

        return mFavoriteImagesList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}