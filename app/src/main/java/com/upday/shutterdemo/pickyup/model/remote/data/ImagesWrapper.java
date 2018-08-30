package com.upday.shutterdemo.pickyup.model.remote.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImagesWrapper extends BaseObservable {
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("per_page")
    @Expose
    private int perPage;
    @SerializedName("total_count")
    @Expose
    private long totalCount;
    @SerializedName("search_id")
    @Expose
    private String searchId;
    @SerializedName("data")
    @Expose
    private List<Images> data;

    @Bindable
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        notifyPropertyChanged(BR.page);
    }

    @Bindable
    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
        notifyPropertyChanged(BR.perPage);
    }

    @Bindable
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        notifyPropertyChanged(BR.totalCount);
    }

    @Bindable
    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
        notifyPropertyChanged(BR.searchId);
    }

    @Bindable
    public List<Images> getData() {
        return data;
    }

    public void setData(List<Images> data) {
        this.data = data;
        notifyPropertyChanged(BR.data);
    }
}