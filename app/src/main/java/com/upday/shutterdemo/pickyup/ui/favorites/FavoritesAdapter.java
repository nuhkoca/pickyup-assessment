package com.upday.shutterdemo.pickyup.ui.favorites;

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
import com.upday.shutterdemo.pickyup.ui.IPopupMenuItemClickListener;
import com.upday.shutterdemo.pickyup.databinding.FavoriteImagesItemLayoutBinding;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.util.AspectUtils;

public class FavoritesAdapter extends PagedListAdapter<FavoriteImages, RecyclerView.ViewHolder> {

    private IPopupMenuItemClickListener mIPopupMenuItemClickListener;

    FavoritesAdapter(IPopupMenuItemClickListener iPopupMenuItemClickListener) {
        super(FavoriteImages.DIFF_CALLBACK);

        this.mIPopupMenuItemClickListener = iPopupMenuItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        FavoriteImagesItemLayoutBinding favoriteImagesItemLayoutBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.favorite_images_item_layout, parent, false);

        return new ImagesViewHolder(favoriteImagesItemLayoutBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FavoriteImages favoriteImages = getItem(position);

        if (favoriteImages != null) {
            ((ImagesViewHolder) holder).bindTo(favoriteImages);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ImagesViewHolder extends RecyclerView.ViewHolder {

        private FavoriteImagesItemLayoutBinding favoriteImagesItemLayoutBinding;

        ImagesViewHolder(View itemView) {
            super(itemView);

            favoriteImagesItemLayoutBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(FavoriteImages favoriteImages) {
            favoriteImagesItemLayoutBinding.setVariable(BR.favoriteImages, favoriteImages);
            favoriteImagesItemLayoutBinding.setVariable(BR.popupItemClickListener, mIPopupMenuItemClickListener);

            favoriteImagesItemLayoutBinding.executePendingBindings();

            AspectUtils.calculateAndApplyTo(
                    favoriteImages.getWidth(),
                    favoriteImages.getHeight(),
                    favoriteImagesItemLayoutBinding.clFavoriteImageHolder,
                    favoriteImagesItemLayoutBinding.ivFavoritePoster.getId()
            );
        }
    }
}