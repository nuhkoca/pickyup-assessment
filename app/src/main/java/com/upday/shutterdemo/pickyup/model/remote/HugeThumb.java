package com.upday.shutterdemo.pickyup.model.remote;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HugeThumb extends BaseObservable {
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("height")
    @Expose
    private double height;
    @SerializedName("width")
    @Expose
    private double width;

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyPropertyChanged(BR.url);
    }

    @Bindable
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        notifyPropertyChanged(BR.height);
    }

    @Bindable
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        notifyPropertyChanged(BR.width);
    }
}