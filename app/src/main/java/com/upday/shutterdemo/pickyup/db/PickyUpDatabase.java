package com.upday.shutterdemo.pickyup.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.upday.shutterdemo.pickyup.model.local.dao.FavoriteImagesDao;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;

@Database(entities = {FavoriteImages.class}, version = 2, exportSchema = false)
public abstract class PickyUpDatabase extends RoomDatabase {

    public abstract FavoriteImagesDao favoriteImagesDao();
}