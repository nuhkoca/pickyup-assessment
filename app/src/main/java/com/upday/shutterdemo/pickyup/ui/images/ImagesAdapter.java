package com.upday.shutterdemo.pickyup.ui.images;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.callback.IPopupMenuItemClickListener;
import com.upday.shutterdemo.pickyup.callback.IRetryListener;
import com.upday.shutterdemo.pickyup.databinding.ImagesItemLayoutBinding;
import com.upday.shutterdemo.pickyup.databinding.NetworkStateItemBinding;
import com.upday.shutterdemo.pickyup.model.remote.Images;
import com.upday.shutterdemo.pickyup.utils.AspectUtils;

public class ImagesAdapter extends PagedListAdapter<Images, RecyclerView.ViewHolder> {

    private NetworkState mNetworkState;
    private IRetryListener mIRetryListener;
    private IPopupMenuItemClickListener mIPopupMenuItemClickListener;

    ImagesAdapter(IRetryListener iRetryListener, IPopupMenuItemClickListener iPopupMenuItemClickListener) {
        super(Images.DIFF_CALLBACK);

        this.mIRetryListener = iRetryListener;
        this.mIPopupMenuItemClickListener = iPopupMenuItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == R.layout.images_item_layout) {
            ImagesItemLayoutBinding imagesItemLayoutBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.images_item_layout, parent, false);

            return new ImagesViewHolder(imagesItemLayoutBinding.getRoot());
        } else {
            NetworkStateItemBinding networkStateItemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.network_state_item, parent, false);

            return new NetworkViewHolder(networkStateItemBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.images_item_layout:
                Images images = getItem(position);

                if (images != null) {
                    ((ImagesViewHolder) holder).bindTo(images);
                }

                break;

            case R.layout.network_state_item:
                ((NetworkViewHolder) holder).bindTo(mNetworkState);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.images_item_layout;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        this.mNetworkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        private ImagesItemLayoutBinding imagesItemLayoutBinding;

        ImagesViewHolder(View itemView) {
            super(itemView);

            imagesItemLayoutBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(Images images) {
            imagesItemLayoutBinding.setVariable(BR.images, images);
            imagesItemLayoutBinding.setVariable(BR.image, images.getAssets().getHugeThumb());
            imagesItemLayoutBinding.setVariable(BR.popupItemClickListener, mIPopupMenuItemClickListener);

            imagesItemLayoutBinding.executePendingBindings();

            AspectUtils.equalizeAndApplyTo(
                    images.getAssets().getHugeThumb().getWidth(),
                    images.getAssets().getHugeThumb().getHeight(),
                    imagesItemLayoutBinding.clImagesHolder,
                    imagesItemLayoutBinding.ivPoster.getId()
            );
        }
    }

    class NetworkViewHolder extends RecyclerView.ViewHolder {

        private NetworkStateItemBinding networkStateItemBinding;

        NetworkViewHolder(View itemView) {
            super(itemView);

            networkStateItemBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(NetworkState networkState) {
            networkStateItemBinding.setVariable(BR.retryListener, mIRetryListener);

            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                networkStateItemBinding.clNetwork.setVisibility(View.VISIBLE);
                networkStateItemBinding.pbNetwork.setVisibility(View.VISIBLE);
                networkStateItemBinding.tvNetworkStateErrButton.setVisibility(View.GONE);
                networkStateItemBinding.tvNetworkStateErrText.setVisibility(View.GONE);
            } else if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                networkStateItemBinding.clNetwork.setVisibility(View.VISIBLE);
                networkStateItemBinding.pbNetwork.setVisibility(View.GONE);
                networkStateItemBinding.tvNetworkStateErrButton.setVisibility(View.VISIBLE);
                networkStateItemBinding.tvNetworkStateErrText.setVisibility(View.VISIBLE);
            } else {
                networkStateItemBinding.clNetwork.setVisibility(View.GONE);
                networkStateItemBinding.pbNetwork.setVisibility(View.GONE);
                networkStateItemBinding.tvNetworkStateErrButton.setVisibility(View.GONE);
                networkStateItemBinding.tvNetworkStateErrText.setVisibility(View.GONE);
            }

            networkStateItemBinding.executePendingBindings();
        }
    }
}