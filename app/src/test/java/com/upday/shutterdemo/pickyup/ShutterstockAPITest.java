package com.upday.shutterdemo.pickyup;

import android.arch.lifecycle.Observer;
import android.arch.paging.PagedList;

import com.upday.shutterdemo.pickyup.model.remote.data.Images;
import com.upday.shutterdemo.pickyup.model.remote.data.ImagesWrapper;
import com.upday.shutterdemo.pickyup.model.remote.EndpointRepository;
import com.upday.shutterdemo.pickyup.ui.images.ImagesFragmentViewModel;
import com.upday.shutterdemo.pickyup.ui.images.paging.ImageResultDataSourceFactory;

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

import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Observable.class, AndroidSchedulers.class, Schedulers.class})
@PowerMockIgnore("javax.net.ssl*")
public class ShutterstockAPITest {

    private static final String QUERY = "berlin";
    private static final String LANGUAGE = "en";
    private static final boolean SAFE_SEARCH = true;
    private static final String SORT = "popular";
    private static final int PAGE = 1;
    private static final int PAGE_SIZE = 20;

    @Mock
    private
    EndpointRepository endpointRepository;
    @Mock
    private
    ImagesFragmentViewModel imagesFragmentViewModel;
    @Mock
    private
    ImageResultDataSourceFactory imageResultDataSourceFactory;
    @Mock
    private
    Observer<PagedList<Images>> observer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        endpointRepository = mock(EndpointRepository.class);
        imageResultDataSourceFactory = spy(
                new ImageResultDataSourceFactory(QUERY, LANGUAGE, SAFE_SEARCH, SORT));

        imagesFragmentViewModel = spy(new ImagesFragmentViewModel(imageResultDataSourceFactory));

        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    @Test
    public void request_ShouldHaveSomeData_ReturnsTrue() throws Exception {
        Observable<ImagesWrapper> observable = (Observable<ImagesWrapper>) mock(Observable.class);

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
        RxJavaHooks.reset();
        RxAndroidPlugins.getInstance().reset();
        AndroidSchedulers.reset();
        Schedulers.reset();
    }
}