package com.upday.shutterdemo.pickyup.model.remote.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assets extends BaseObservable {
    @SerializedName("huge_thumb")
    @Expose
    private HugeThumb hugeThumb;

    @Bindable
    public HugeThumb getHugeThumb() {
        return hugeThumb;
    }

    public void setHugeThumb(HugeThumb hugeThumb) {
        this.hugeThumb = hugeThumb;
        notifyPropertyChanged(BR.hugeThumb);
    }
}