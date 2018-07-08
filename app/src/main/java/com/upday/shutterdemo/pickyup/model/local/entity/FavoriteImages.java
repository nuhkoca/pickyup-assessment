package com.upday.shutterdemo.pickyup.model.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.v7.util.DiffUtil;

@Entity(tableName = "favorite_images", indices = {@Index(value = "iid", unique = true)})
public class FavoriteImages {

    public static DiffUtil.ItemCallback<FavoriteImages> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteImages>() {
        @Override
        public boolean areItemsTheSame(FavoriteImages oldItem, FavoriteImages newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(FavoriteImages oldItem, FavoriteImages newItem) {
            return oldItem.equals(newItem);
        }
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "iid")
    private String iid;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "height")
    private double height;
    @ColumnInfo(name = "width")
    private double width;

    public FavoriteImages(String iid, String url, String description, double height, double width) {
        this.iid = iid;
        this.url = url;
        this.description = description;
        this.height = height;
        this.width = width;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        FavoriteImages favoriteImages = (FavoriteImages) obj;

        return favoriteImages.id == this.id;
    }
}