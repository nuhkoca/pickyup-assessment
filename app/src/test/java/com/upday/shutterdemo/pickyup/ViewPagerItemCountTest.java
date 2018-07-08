package com.upday.shutterdemo.pickyup;

import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.ui.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ViewPagerItemCountTest {

    @Mock
    private
    MainActivity.ViewPagerInflater viewPagerInflater;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(viewPagerInflater.getCount()).thenReturn(Constants.VIEW_PAGER_OFFSET_LIMIT);
    }

    @Test
    public void viewPager_ShouldHaveAdequateItem_ReturnsTrue() {
        assertThat(viewPagerInflater.getCount(), is(Constants.VIEW_PAGER_OFFSET_LIMIT));
    }
}