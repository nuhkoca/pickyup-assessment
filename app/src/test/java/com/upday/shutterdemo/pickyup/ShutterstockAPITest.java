package com.upday.shutterdemo.pickyup;

import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;

import com.upday.shutterdemo.pickyup.helper.AppsExecutor;
import com.upday.shutterdemo.pickyup.model.local.FavoriteImagesRepository;
import com.upday.shutterdemo.pickyup.model.remote.EndpointRepository;
import com.upday.shutterdemo.pickyup.model.remote.data.Images;
import com.upday.shutterdemo.pickyup.model.remote.data.ImagesWrapper;
import com.upday.shutterdemo.pickyup.ui.images.ImagesFragmentViewModel;
import com.upday.shutterdemo.pickyup.ui.images.paging.ImageResultDataSourceFactory;
import com.upday.shutterdemo.pickyup.ui.images.paging.PageKeyedImagesDataSource;
import com.upday.shutterdemo.pickyup.util.FirebaseMLKitUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Single.class, AndroidSchedulers.class, Schedulers.class})
@PowerMockIgnore("javax.net.ssl*")
public class ShutterstockAPITest {

    private static final String QUERY = "berlin";
    private static final String LANGUAGE = "en";
    private static final boolean SAFE_SEARCH = true;
    private static final String SORT = "popular";
    private static final int PAGE = 1;
    private static final int PAGE_SIZE = 20;

    @Mock
    @Inject
    EndpointRepository endpointRepository;
    @Mock
    @Inject
    ImagesFragmentViewModel imagesFragmentViewModel;
    @Mock
    @Inject
    ImageResultDataSourceFactory imageResultDataSourceFactory;
    @Mock
    private
    Observer<PagedList<Images>> observer;
    @Mock
    @Inject
    PageKeyedImagesDataSource pageKeyedImagesDataSource;
    @Mock
    @Inject
    AppsExecutor appsExecutor;
    @Mock
    @Inject
    FirebaseMLKitUtils firebaseMLKitUtils;
    @Mock
    @Inject
    FavoriteImagesRepository favoriteImagesRepository;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        endpointRepository = mock(EndpointRepository.class);
        imageResultDataSourceFactory = spy(
                new ImageResultDataSourceFactory(pageKeyedImagesDataSource));

        imagesFragmentViewModel = spy(new ImagesFragmentViewModel(imageResultDataSourceFactory, appsExecutor, firebaseMLKitUtils, favoriteImagesRepository));
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    @Test
    public void request_ShouldHaveSomeData_ReturnsTrue() {
        Single<ImagesWrapper> observable = (Single<ImagesWrapper>) mock(Single.class);

        Assert.assertNotNull(observable);

        when(endpointRepository.getImages(QUERY, LANGUAGE, SAFE_SEARCH, SORT, PAGE, PAGE_SIZE))
                .thenReturn(observable);
        when(observable.subscribeOn(Schedulers.io())).thenReturn(observable);
        when(observable.observeOn(AndroidSchedulers.mainThread())).thenReturn(observable);

        imagesFragmentViewModel.getImagesResult().observeForever(observer);

        imagesFragmentViewModel.getImagesResult();

        assertTrue(imagesFragmentViewModel.getImagesResult() != null);
    }

    @After
    public void tearDown() {
        RxAndroidPlugins.reset();
        AndroidSchedulers.mainThread().shutdown();
        Schedulers.shutdown();
    }
}