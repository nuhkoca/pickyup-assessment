package com.upday.shutterdemo.pickyup.ui.favorites;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.widget.ImageView;

import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.repository.db.FavoriteImagesRepository;
import com.upday.shutterdemo.pickyup.utils.FirebaseMLKitUtils;

import javax.inject.Inject;

public class FavoritesFragmentViewModel extends ViewModel {

    private LiveData<PagedList<FavoriteImages>> mFavoriteImagesList;

    private FavoriteImagesRepository favoriteImagesRepository;

    @Inject
    FirebaseMLKitUtils firebaseMLKitUtils;

    @Inject
    FavoritesFragmentViewModel(FavoriteImagesRepository favoriteImagesRepository) {
        this.favoriteImagesRepository = favoriteImagesRepository;

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.PER_PAGE_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE).build();

        mFavoriteImagesList = new LivePagedListBuilder<>(favoriteImagesRepository.getAll(), config).build();
    }

    public LiveData<PagedList<FavoriteImages>> getFavoriteImagesList() {
        return mFavoriteImagesList;
    }

    public LiveData<PagedList<FavoriteImages>> retrieveFavoriteImagesList() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(Constants.PER_PAGE_SIZE)
                .setPageSize(Constants.PER_PAGE_SIZE).build();

        mFavoriteImagesList = new LivePagedListBuilder<>(favoriteImagesRepository.getAll(), config).build();

        return mFavoriteImagesList;
    }

    public void generateLabelsFromBitmap(final ImageView imageView) {
        firebaseMLKitUtils.generateLabelsFromBitmap(imageView);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}