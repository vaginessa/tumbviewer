package com.nutrition.express.download;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 2/17/17.
 */

public class DownloadManagerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.download_toolbar_title);
        }
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        List<Fragment> list = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        final VideoFragment videoFragment = new VideoFragment();
        final PhotoFragment photoFragment = new PhotoFragment();
        list.add(videoFragment);
        titles.add(getString(R.string.download_video_title));
        list.add(photoFragment);
        titles.add(getString(R.string.download_photo_title));

        CommonPagerAdapter pagerAdapter =
                new CommonPagerAdapter(getSupportFragmentManager(), list, titles);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    videoFragment.scrollToTop();
                } else {
                    photoFragment.scrollToTop();
                }
            }
        });

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected ActionMode startMultiChoice(ActionMode.Callback callback) {
        return startSupportActionMode(callback);
    }

}
