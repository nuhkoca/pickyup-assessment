package com.upday.shutterdemo.pickyup.ui.favorites;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.ui.IPopupMenuItemClickListener;
import com.upday.shutterdemo.pickyup.databinding.CustomImagesLayoutBinding;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.ui.WebViewActivity;
import com.upday.shutterdemo.pickyup.util.ColumnUtils;
import com.upday.shutterdemo.pickyup.util.PopupMenuUtils;
import com.upday.shutterdemo.pickyup.util.SharedPreferencesUtils;
import com.upday.shutterdemo.pickyup.util.SnackbarUtils;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends DaggerFragment implements SharedPreferences.OnSharedPreferenceChangeListener, IPopupMenuItemClickListener<FavoriteImages> {

    private CustomImagesLayoutBinding mCustomImagesLayoutBinding;
    private FavoritesFragmentViewModel mFavoritesFragmentViewModel;

    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    ColumnUtils columnUtils;

    private FavoritesAdapter mFavoritesAdapter;

    private PagedList<FavoriteImages> mFavoriteImages;

    private SharedPreferences mSharedPreferences;

    public static FavoritesFragment getInstance() {
        return new FavoritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        mCustomImagesLayoutBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.custom_images_layout, container, false);

        return mCustomImagesLayoutBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setupRV() {
        int columnCount = columnUtils.getOptimalNumberOfColumn(getActivity());
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(columnCount, 1);

        mCustomImagesLayoutBinding.rvImages.setLayoutManager(staggeredGridLayoutManager);

        mFavoritesAdapter = new FavoritesAdapter(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRV();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getContext() != null) {
            mFavoritesFragmentViewModel = ViewModelProviders.of(this,
                    viewModelFactory).get(FavoritesFragmentViewModel.class);
        }

        mFavoritesFragmentViewModel.getFavoriteImagesList().observe(this, favoriteImages -> {
            if (favoriteImages != null && favoriteImages.size() > 0) {
                mFavoritesAdapter.submitList(favoriteImages);
                mCustomImagesLayoutBinding.pbImages.setVisibility(View.GONE);
                mCustomImagesLayoutBinding.tvErrText.setVisibility(View.GONE);

                mFavoriteImages = favoriteImages;
            } else {
                mCustomImagesLayoutBinding.pbImages.setVisibility(View.GONE);
                mCustomImagesLayoutBinding.tvErrText.setText(getString(R.string.no_item_in_db_warning_text));
                mCustomImagesLayoutBinding.tvErrText.setVisibility(View.VISIBLE);
            }
        });

        mCustomImagesLayoutBinding.rvImages.setAdapter(mFavoritesAdapter);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.delete_all).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case R.id.delete_all:
                deleteAll();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPopupMenuItemClick(final FavoriteImages favoriteImages, View view, final ImageView imageView) {
        PopupMenu popupMenu = new PopupMenu(Objects.requireNonNull(getActivity()), view);

        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.overflow_menu, popupMenu.getMenu());

        popupMenu.getMenu().findItem(R.id.add_to_fav).setVisible(false);

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(itemId -> {
                    switch (itemId) {
                        case R.id.remove_from_fav:
                            deleteItem(favoriteImages);

                            break;

                        case R.id.open_in_browser:
                            Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
                            browserIntent.putExtra(Constants.WEB_URL_KEY,
                                    favoriteImages.getUrl());

                            startActivity(browserIntent,
                                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());

                            break;

                        case R.id.label_image:
                            mFavoritesFragmentViewModel.generateLabelsFromBitmap(imageView);

                            break;

                        default:
                            break;
                    }
                });

        popupMenu.setOnMenuItemClickListener(builder);

        builder.build();

        popupMenu.show();
    }

    private void deleteItem(final FavoriteImages favoriteImages) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setTitle(String.format(getString(R.string.item_removing_warning_title), favoriteImages.getDescription()))
                .setMessage(getString(R.string.item_removing_warning_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok_action_text), (dialog, which) -> {
                    dialog.dismiss();

                    mFavoritesFragmentViewModel.deleteItem(favoriteImages);
                    getAll();

                    new SnackbarUtils.Builder()
                            .setView(mCustomImagesLayoutBinding.clImages)
                            .setMessage(String.format(getString(R.string.item_deleted_info_text), favoriteImages.getDescription()))
                            .setLength(SnackbarUtils.Length.LONG)
                            .show(getString(R.string.dismiss_action_text))
                            .build();
                })
                .setNegativeButton(getString(R.string.cancel_action_text), (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void deleteAll() {
        if (mFavoriteImages != null && mFavoriteImages.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle(getString(R.string.all_item_removing_warning_title))
                    .setMessage(getString(R.string.item_removing_warning_text))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok_action_text), (dialog, which) -> {
                        dialog.dismiss();

                        mFavoritesFragmentViewModel.deleteAll();
                        getAll();

                        new SnackbarUtils.Builder()
                                .setView(mCustomImagesLayoutBinding.clImages)
                                .setMessage(getString(R.string.all_item_deleted_info_text))
                                .setLength(SnackbarUtils.Length.LONG)
                                .show(getString(R.string.dismiss_action_text))
                                .build();
                    })
                    .setNegativeButton(getString(R.string.cancel_action_text), (dialog, which) -> dialog.dismiss());

            builder.create().show();
        }
    }

    private void getAll() {
        mFavoritesFragmentViewModel.retrieveFavoriteImagesList().observe(this, favoriteImages -> {
            mFavoritesAdapter.submitList(null);
            mFavoritesAdapter.submitList(favoriteImages);
            mFavoriteImages = favoriteImages;
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.confidence_key))) {
            sharedPreferencesUtils.putStringData(key, sharedPreferences.getString(key, getString(R.string.confidence_0_7_value)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mFavoritesFragmentViewModel != null) {
            mFavoritesFragmentViewModel.onCleared();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }
}