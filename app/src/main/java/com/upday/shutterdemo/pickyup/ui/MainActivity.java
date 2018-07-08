package com.upday.shutterdemo.pickyup.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.upday.shutterdemo.pickyup.R;
import com.upday.shutterdemo.pickyup.databinding.ActivityMainBinding;
import com.upday.shutterdemo.pickyup.helper.Constants;
import com.upday.shutterdemo.pickyup.helper.SearchView;
import com.upday.shutterdemo.pickyup.ui.favorites.FavoritesFragment;
import com.upday.shutterdemo.pickyup.ui.images.ImagesFragment;
import com.upday.shutterdemo.pickyup.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding mActivityMainBinding;

    private MenuItem mPrevMenuItem;
    private long mBackPressed;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    private SearchView mSearchView;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupUI();
        setupInterstitialAd();
    }

    private void setupInterstitialAd() {
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd = null;
                    MainActivity.super.onBackPressed();
                } else {
                    MainActivity.super.onBackPressed();
                }

                super.onAdClosed();
            }
        });
    }

    private void disposeInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
    }

    private void setupUI() {
        mActivityMainBinding.vpImages.setOffscreenPageLimit(Constants.VIEW_PAGER_OFFSET_LIMIT);
        mActivityMainBinding.vpImages.setAdapter(new ViewPagerInflater(getSupportFragmentManager()));

        //disabled since other fragments become touchable besides the active one
        //mActivityMainBinding.vpImages.setPageTransformer(true, new DepthPageTransformer());

        mActivityMainBinding.bnvImages.setOnNavigationItemSelectedListener(this);

        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null) {
                    mPrevMenuItem.setChecked(false);
                } else {
                    mActivityMainBinding.bnvImages.getMenu().getItem(0).setChecked(false);
                }
                mActivityMainBinding.bnvImages.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = mActivityMainBinding.bnvImages.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mActivityMainBinding.vpImages.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.delete_all).setVisible(false);
        menu.findItem(R.id.report).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        mSearchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case R.id.report:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + getString(R.string.mail_address)));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_with)));

                return true;

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemThatWasClicked;

        if (item.getItemId() < 0) {
            itemThatWasClicked = 0;
        } else {
            itemThatWasClicked = item.getItemId();
        }

        switch (itemThatWasClicked) {
            case R.id.bottom_nav_images:
                mActivityMainBinding.vpImages.setCurrentItem(0);
                return true;

            case R.id.bottom_nav_fav:
                mActivityMainBinding.vpImages.setCurrentItem(1);
                return true;

            case R.id.bottom_nav_settings:
                mActivityMainBinding.vpImages.setCurrentItem(2);
                return true;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        int timeDelay = getResources().getInteger(R.integer.delay_in_seconds_to_close);

        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            mSearchView.clearFocus();
        } else {
            if (mBackPressed + timeDelay > System.currentTimeMillis()) {

                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    super.onBackPressed();
                }

            } else {
                Toast.makeText(getBaseContext(), getString(R.string.twice_press_to_exit),
                        Toast.LENGTH_SHORT).show();
            }
        }

        mBackPressed = System.currentTimeMillis();
    }

    //caches the fragments in the backstack and survives against screen orientation
    public class ViewPagerInflater extends FragmentPagerAdapter {
        private Fragment frags[] = new Fragment[Constants.VIEW_PAGER_OFFSET_LIMIT];

        ViewPagerInflater(FragmentManager fm) {
            super(fm);
            frags[0] = ImagesFragment.getInstance();
            frags[1] = FavoritesFragment.getInstance();
            frags[2] = SettingsFragment.getInstance();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.bottom_nav_images_header);

                case 1:
                    return getString(R.string.bottom_nav_favorites_header);

                case 2:
                    return getString(R.string.bottom_nav_settings_header);

                default:
                    break;
            }

            return null;
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            frags[position] = (Fragment) super.instantiateItem(container, position);
            return frags[position];
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposeInterstitialAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupInterstitialAd();
    }

    @Override
    protected void onStop() {
        disposeInterstitialAd();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener = null;
        }

        disposeInterstitialAd();

        super.onDestroy();
    }
}