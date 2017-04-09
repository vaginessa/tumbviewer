package com.nutrition.express.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
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
import com.nutrition.express.downloadservice.DownloadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by huang on 2/17/17.
 */

public class DownloadManagerActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private VideoFragment videoFragment;
    private PhotoFragment photoFragment;
    private int videoIndex;
    private int photoIndex;

    private DownloadService downloadService;
    private boolean isBound;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.LocalBinder) service).getService();
            Queue<String> strings = downloadService.getDownloadQueue();
            setContentData(!strings.isEmpty());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
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
                if (tab.getPosition() == videoIndex) {
                    videoFragment.scrollToTop();
                } else if (tab.getPosition() == photoIndex) {
                    photoFragment.scrollToTop();
                }
            }
        });

        doBindService();

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

    private void setContentData(boolean hasDownloadPage) {
        List<Fragment> list = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        videoFragment = new VideoFragment();
        photoFragment = new PhotoFragment();
        if (hasDownloadPage) {
            DownloadFragment downloadFragment = new DownloadFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(DownloadService.DOWNLOAD_LIST,
                    new ArrayList<>(downloadService.getDownloadQueue()));
            downloadFragment.setArguments(bundle);
            downloadFragment.setDownloadService(downloadService);
            list.add(downloadFragment);
            titles.add(getString(R.string.video_download));
        }
        list.add(videoFragment);
        videoIndex = list.size() - 1;
        titles.add(getString(R.string.download_video_title));
        list.add(photoFragment);
        photoIndex = list.size() - 1;
        titles.add(getString(R.string.download_photo_title));

        CommonPagerAdapter pagerAdapter =
                new CommonPagerAdapter(getSupportFragmentManager(), list, titles);
        viewPager.setAdapter(pagerAdapter);
    }

    protected ActionMode startMultiChoice(ActionMode.Callback callback) {
        return startSupportActionMode(callback);
    }

    private void doBindService() {
        bindService(new Intent(this, DownloadService.class), connection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    private void doUnbindService() {
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }
}
