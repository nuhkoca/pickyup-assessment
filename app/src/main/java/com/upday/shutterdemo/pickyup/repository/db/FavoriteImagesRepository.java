package com.upday.shutterdemo.pickyup.repository.db;

import android.app.Application;
import android.arch.paging.DataSource;
import android.os.AsyncTask;

import com.upday.shutterdemo.pickyup.callback.IDatabaseProgressListener;
import com.upday.shutterdemo.pickyup.db.PickyUpDatabase;
import com.upday.shutterdemo.pickyup.helper.AppsExecutor;
import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;

public class FavoriteImagesRepository {
    private FavoriteImagesDao mFavoriteImagesDao;

    public FavoriteImagesRepository(Application application) {
        PickyUpDatabase pickyUpDatabase = PickyUpDatabase.getInstance(application);
        mFavoriteImagesDao = pickyUpDatabase.favoriteImagesDao();
    }

    public DataSource.Factory<Integer, FavoriteImages> getAll() {
        return mFavoriteImagesDao.getAll();
    }

    public void insertOrThrow(FavoriteImages favoriteCountries, String cid, IDatabaseProgressListener iDatabaseProgressListener) {
        new insertOrThrowAsync(cid, mFavoriteImagesDao, iDatabaseProgressListener).execute(favoriteCountries);
    }

    public void deleteItem(final String iid) {
        AppsExecutor.backgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                mFavoriteImagesDao.deleteItem(iid);
            }
        });
    }

    public void deleteAll() {
        AppsExecutor.backgroundThread().execute(new Runnable() {
            @Override
            public void run() {
                mFavoriteImagesDao.deleteAll();
            }
        });
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