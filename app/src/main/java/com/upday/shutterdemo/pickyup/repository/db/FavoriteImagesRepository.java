package com.upday.shutterdemo.pickyup.repository.db;

import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.upday.shutterdemo.pickyup.callback.IDatabaseProgressListener;
import com.upday.shutterdemo.pickyup.helper.AppsExecutor;
import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FavoriteImagesRepository {

    private FavoriteImagesDao favoriteImagesDao;
    private AppsExecutor appsExecutor;

    @Inject
    public FavoriteImagesRepository(FavoriteImagesDao favoriteImagesDao, AppsExecutor appsExecutor) {
        this.favoriteImagesDao = favoriteImagesDao;
        this.appsExecutor = appsExecutor;

    }

    public DataSource.Factory<Integer, FavoriteImages> getAll() {
        return favoriteImagesDao.getAll();
    }

    public void insertOrThrow(FavoriteImages favoriteCountries, String cid, IDatabaseProgressListener iDatabaseProgressListener) {
        new insertOrThrowAsync(cid, favoriteImagesDao, iDatabaseProgressListener).execute(favoriteCountries);
    }

    public void deleteItem(final String iid) {
        appsExecutor.diskIO().execute(() -> favoriteImagesDao.deleteItem(iid));
    }

    public void deleteAll() {
        appsExecutor.diskIO().execute(() -> favoriteImagesDao.deleteAll());
    }

    private static class insertOrThrowAsync extends AsyncTask<FavoriteImages, Void, Boolean> {

        private String iid;
        private FavoriteImagesDao favoriteImagesDao;
        private IDatabaseProgressListener iDatabaseProgressListener;

        insertOrThrowAsync(String iid, FavoriteImagesDao favoriteImagesDao, IDatabaseProgressListener iDatabaseProgressListener) {
            this.iid = iid;
            this.favoriteImagesDao = favoriteImagesDao;
            this.iDatabaseProgressListener = iDatabaseProgressListener;
        }

        @Override
        protected Boolean doInBackground(FavoriteImages... favoriteImages) {
            if (iid.equals(favoriteImagesDao.getItemById(iid))) {
                return true;
            } else {
                favoriteImagesDao.insertItem(favoriteImages[0]);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            iDatabaseProgressListener.onItemRetrieved(result);
        }
    }
}