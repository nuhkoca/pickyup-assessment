package com.upday.shutterdemo.pickyup.ui.images;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.api.NetworkState;
import com.upday.shutterdemo.pickyup.callback.IDatabaseProgressListener;
import com.upday.shutterdemo.pickyup.callback.IMenuItemIdListener;
import com.upday.shutterdemo.pickyup.callback.IPopupMenuItemClickListener;
import com.upday.shutterdemo.pickyup.callback.IRetryListener;
import com.upday.shutterdemo.pickyup.databinding.CustomImagesLayoutBinding;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.helper.SearchView;
import com.upday.shutterdemo.pickyup.model.local.entity.FavoriteImages;
import com.upday.shutterdemo.pickyup.model.remote.Images;
import com.upday.shutterdemo.pickyup.repository.db.FavoriteImagesRepository;
import com.upday.shutterdemo.pickyup.test.SimpleIdlingResource;
import com.upday.shutterdemo.pickyup.ui.WebViewActivity;
import com.upday.shutterdemo.pickyup.ui.images.paging.ImageResultDataSourceFactory;
import com.upday.shutterdemo.pickyup.utils.ColumnUtils;
import com.upday.shutterdemo.pickyup.utils.ConnectionUtils;
import com.upday.shutterdemo.pickyup.utils.PopupMenuUtils;
import com.upday.shutterdemo.pickyup.utils.SharedPreferencesUtils;
import com.upday.shutterdemo.pickyup.utils.SnackbarUtils;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFragment extends DaggerFragment implements SharedPreferences.OnSharedPreferenceChangeListener, IRetryListener, View.OnClickListener, IPopupMenuItemClickListener<Images> {

    private CustomImagesLayoutBinding mCustomImagesLayoutBinding;
    private ImagesFragmentViewModel mImagesFragmentViewModel;

    private SharedPreferences mSharedPreferences;

    private ImagesAdapter mImagesAdapter;

    private SearchView mSearchView;

    private boolean sIsRotatedAndSearchViewStated = false;
    private String sSearchString;

    private String mLanguage;
    private boolean mSafeSearch;
    private String mSorting;

    @Inject
    FavoriteImagesRepository favoriteImagesRepository;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @VisibleForTesting
    public static ImagesFragment getInstanceForTesting() {
        return new ImagesFragment();
    }

    public static ImagesFragment getInstance() {
        return new ImagesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

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

        getIdlingResource();

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    private void setupRV() {
        int columnCount = ColumnUtils.getOptimalNumberOfColumn(getContext());
        final StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);

        mCustomImagesLayoutBinding.rvImages.setLayoutManager(staggeredGridLayoutManager);

        mImagesAdapter = new ImagesAdapter(this, this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            sIsRotatedAndSearchViewStated = savedInstanceState.getBoolean(Constants.SEARCH_VIEW_STATE);
            sSearchString = savedInstanceState.getString(Constants.SEARCH_VIEW_QUERY_STATE);
        }

        setupRV();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getContext() != null) {
            mImagesFragmentViewModel = ViewModelProviders.of(this,
                    new ImagesFragmentViewModelFactory(
                            ImageResultDataSourceFactory.getInstance(
                                    SharedPreferencesUtils.getInstance().getQuery(),
                                    SharedPreferencesUtils.loadLanguagePreference(getContext(), mSharedPreferences),
                                    SharedPreferencesUtils.loadSafeSearchPreference(getContext(), mSharedPreferences),
                                    SharedPreferencesUtils.loadSortingPreference(getContext(), mSharedPreferences)
                            )
                    )).get(ImagesFragmentViewModel.class);

            mLanguage = SharedPreferencesUtils.loadLanguagePreference(getContext(), mSharedPreferences);
            mSafeSearch = SharedPreferencesUtils.loadSafeSearchPreference(getContext(), mSharedPreferences);
            mSorting = SharedPreferencesUtils.loadSortingPreference(getContext(), mSharedPreferences);
        }

        mImagesFragmentViewModel.getImagesResult().observe(this, new Observer<PagedList<Images>>() {
            @Override
            public void onChanged(@Nullable PagedList<Images> images) {
                if (images != null) {
                    mImagesAdapter.submitList(images);

                    getIdlingResource();

                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(true);
                    }
                }
            }
        });

        mImagesFragmentViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    mImagesAdapter.setNetworkState(networkState);
                }
            }
        });

        mImagesFragmentViewModel.getInitialLoading().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null) {
                    if (networkState.getStatus() == NetworkState.Status.SUCCESS) {
                        mCustomImagesLayoutBinding.pbImages.setVisibility(View.GONE);
                        mCustomImagesLayoutBinding.tvErrText.setVisibility(View.GONE);
                        mCustomImagesLayoutBinding.tvErrButton.setVisibility(View.GONE);

                        getIdlingResource();

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }

                    } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        mCustomImagesLayoutBinding.pbImages.setVisibility(View.GONE);
                        mCustomImagesLayoutBinding.tvErrText.setVisibility(View.VISIBLE);
                        mCustomImagesLayoutBinding.tvErrButton.setVisibility(View.VISIBLE);
                        mCustomImagesLayoutBinding.tvErrText.setText(getString(R.string.response_error_text));

                        getIdlingResource();

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(false);
                        }

                    } else if (networkState.getStatus() == NetworkState.Status.NO_ITEM) {
                        mCustomImagesLayoutBinding.pbImages.setVisibility(View.GONE);
                        mCustomImagesLayoutBinding.tvErrText.setVisibility(View.VISIBLE);
                        mCustomImagesLayoutBinding.tvErrText.setText(getString(R.string.no_result_error_text));
                        mCustomImagesLayoutBinding.tvErrButton.setVisibility(View.GONE);

                        getIdlingResource();

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(false);
                        }

                    } else {
                        mCustomImagesLayoutBinding.pbImages.setVisibility(View.VISIBLE);
                        mCustomImagesLayoutBinding.tvErrText.setVisibility(View.GONE);
                        mCustomImagesLayoutBinding.tvErrButton.setVisibility(View.GONE);

                        getIdlingResource();

                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(false);
                        }
                    }
                }
            }
        });

        mCustomImagesLayoutBinding.rvImages.setAdapter(mImagesAdapter);
        mCustomImagesLayoutBinding.tvErrButton.setOnClickListener(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.search).setVisible(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager =
                (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        if (searchManager != null) {
            mSearchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));
        }

        mSearchView.setMaxWidth(Integer.MAX_VALUE);

        if (!sIsRotatedAndSearchViewStated) {
            sIsRotatedAndSearchViewStated = true;
        }

        if (!TextUtils.isEmpty(sSearchString)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mSearchView.setQuery(sSearchString, false);
                }
            });

            mSearchView.setIconified(false);
            mSearchView.setFocusable(true);
            mSearchView.requestFocusFromTouch();

            menu.findItem(R.id.search).expandActionView();
        }

        mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();

                SharedPreferencesUtils.getInstance().saveQuery(query);
                query = SharedPreferencesUtils.getInstance().getQuery();

                ImageResultDataSourceFactory.getInstance(query, mLanguage, mSafeSearch, mSorting);

                refreshImagesResult();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(Constants.SEARCH_VIEW_STATE, sIsRotatedAndSearchViewStated);

        if (mSearchView != null && mSearchView.getQuery() != null && !TextUtils.isEmpty(mSearchView.getQuery())) {
            outState.putString(Constants.SEARCH_VIEW_QUERY_STATE, String.valueOf(mSearchView.getQuery()));
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.image_language_pref_key))) {
            mLanguage = SharedPreferencesUtils.loadLanguagePreference(Objects.requireNonNull(getActivity()), sharedPreferences);
        }
        if (key.equals(getString(R.string.image_sorting_pref_key))) {
            mSorting = SharedPreferencesUtils.loadSortingPreference(Objects.requireNonNull(getActivity()), sharedPreferences);
        }
        if (key.equals(getString(R.string.image_safe_search_pref_key))) {
            mSafeSearch = SharedPreferencesUtils.loadSafeSearchPreference(Objects.requireNonNull(getContext()), sharedPreferences);
        }

        if (!key.equals(getString(R.string.confidence_key))) {
            behaveAccordingToFirstRun();
        }
    }

    private void behaveAccordingToFirstRun() {
        if (SharedPreferencesUtils.getInstance().getRunCount() >= Constants.VIEW_PAGER_OFFSET_LIMIT) {
            String query = SharedPreferencesUtils.getInstance().getQuery();

            ImageResultDataSourceFactory.getInstance(query,
                    mLanguage, mSafeSearch, mSorting
            );

            refreshImagesResult();
        } else {
            SharedPreferencesUtils.getInstance().setRunCount();
        }
    }

    private synchronized void refreshImagesResult() {
        setupRV();

        mImagesFragmentViewModel.refreshImagesResult().observe(this, new Observer<PagedList<Images>>() {
            @Override
            public void onChanged(@Nullable PagedList<Images> images) {
                if (images != null) {
                    mImagesAdapter.submitList(null);
                    mImagesAdapter.submitList(images);

                    getIdlingResource();

                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(true);
                    }
                }
            }
        });

        mCustomImagesLayoutBinding.rvImages.setAdapter(mImagesAdapter);
    }

    @Override
    public void onClick(View v) {
        final int itemThatWasClicked = v.getId();

        if (itemThatWasClicked == R.id.tvErrButton) {
            refreshImagesResult();
        }
    }

    @Override
    public void onRefresh() {
        boolean isConnection = ConnectionUtils.sniff();

        if (isConnection) {
            refreshImagesResult();
        }
    }

    @Override
    public void onPopupMenuItemClick(final Images images, View view, final ImageView imageView) {
        PopupMenu popupMenu = new PopupMenu(Objects.requireNonNull(getActivity()), view);

        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.overflow_menu, popupMenu.getMenu());

        popupMenu.getMenu().findItem(R.id.remove_from_fav).setVisible(false);

        PopupMenuUtils.Builder builder = new PopupMenuUtils.Builder()
                .listener(new IMenuItemIdListener() {
                    @Override
                    public void onItemIdReceived(int itemId) {
                        switch (itemId) {
                            case R.id.add_to_fav:
                                addToDb(images);

                                break;

                            case R.id.open_in_browser:
                                Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
                                browserIntent.putExtra(Constants.WEB_URL_KEY,
                                        images.getAssets().getHugeThumb().getUrl());

                                startActivity(browserIntent,
                                        ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());

                                break;

                            case R.id.label_image:
                                mImagesFragmentViewModel.generateLabelsFromBitmap(imageView, mSharedPreferences);

                                break;

                            default:
                                break;
                        }
                    }
                });

        popupMenu.setOnMenuItemClickListener(builder);

        builder.build();

        popupMenu.show();
    }

    private void addToDb(final Images images) {
        String iid = images.getId();
        final String description = images.getDescription();
        String url = images.getAssets().getHugeThumb().getUrl();
        double height = images.getAssets().getHugeThumb().getHeight();
        double width = images.getAssets().getHugeThumb().getWidth();

        FavoriteImages favoriteImages = new FavoriteImages(
                iid,
                url,
                description,
                height,
                width
        );

        favoriteImagesRepository.insertOrThrow(favoriteImages, iid, new IDatabaseProgressListener() {
            @Override
            public void onItemRetrieved(boolean result) {
                if (result) {
                    new SnackbarUtils.Builder()
                            .setView(mCustomImagesLayoutBinding.clImages)
                            .setMessage(String.format(getString(R.string.constraint_exception_text), description))
                            .setLength(SnackbarUtils.Length.LONG)
                            .show(getString(R.string.dismiss_action_text))
                            .build();
                } else {
                    new SnackbarUtils.Builder()
                            .setView(mCustomImagesLayoutBinding.clImages)
                            .setMessage(String.format(getString(R.string.database_adding_info_text), description))
                            .setLength(SnackbarUtils.Length.LONG)
                            .show(getString(R.string.dismiss_action_text))
                            .build();
                }
            }
        });
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
        if (mImagesFragmentViewModel != null) {
            mImagesFragmentViewModel.onCleared();
        }

        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }
}