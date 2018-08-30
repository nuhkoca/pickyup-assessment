package com.upday.shutterdemo.pickyup.model.remote.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.util.DiffUtil;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images extends BaseObservable {

    public static DiffUtil.ItemCallback<Images> DIFF_CALLBACK = new DiffUtil.ItemCallback<Images>() {
        @Override
        public boolean areItemsTheSame(Images oldItem, Images newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(Images oldItem, Images newItem) {
            return oldItem.equals(newItem);
        }
    };

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("aspect")
    @Expose
    private double aspect;
    @SerializedName("assets")
    @Expose
    private Assets assets;
    @SerializedName("description")
    @Expose
    private String description;

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public double getAspect() {
        return aspect;
    }

    public void setAspect(double aspect) {
        this.aspect = aspect;
        notifyPropertyChanged(BR.aspect);
    }

    @Bindable
    public Assets getAssets() {
        return assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
        notifyPropertyChanged(BR.assets);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            return true;
        }

        Images imagesResult = (Images) obj;

        return imagesResult.id.equals(this.id);
    }
}