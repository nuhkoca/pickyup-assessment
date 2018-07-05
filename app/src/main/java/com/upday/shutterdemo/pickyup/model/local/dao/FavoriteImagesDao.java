package com.upday.shutterdemo.pickyup.model.local.dao;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;

@Dao
public interface FavoriteImagesDao {
    @Query("SELECT * FROM favorite_images")
    DataSource.Factory<Integer, FavoriteImages> getAll();

    @Query("SELECT iid FROM favorite_images WHERE iid = :iid LIMIT 1")
    String getItemById(String iid);

    @Insert
    void insertItem(FavoriteImages favoriteImages);

    @Query("DELETE FROM favorite_images WHERE iid = :iid")
    void deleteItem(String iid);

    @Query("DELETE FROM favorite_images")
    void deleteAll();
}