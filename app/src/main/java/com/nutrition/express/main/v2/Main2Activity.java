package com.nutrition.express.main.v2;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.application.BaseActivity;
import com.nutrition.express.download.DownloadFragment;
import com.nutrition.express.download.DownloadManagerActivity;
import com.nutrition.express.following.FollowingActivity;
import com.nutrition.express.likes.LikesActivity;
import com.nutrition.express.main.DashboardFragment;
import com.nutrition.express.main.UserContract;
import com.nutrition.express.main.UserPresenter;
import com.nutrition.express.main.VideoDashboardFragment;
import com.nutrition.express.model.event.EventRefresh;
import com.nutrition.express.model.event.EventStatusBar;
import com.nutrition.express.model.rest.bean.UserInfo;
import com.nutrition.express.search.SearchActivity;
import com.nutrition.express.settings.SettingsActivity;
import com.nutrition.express.util.FrescoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Main2Activity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserContract.View {
    private DrawerLayout drawerLayout;

    private UserContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                return insets;
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBarLayout), new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                v.setBackgroundResource(R.color.colorPrimaryDark);
                return insets;
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                ((AppBarLayout.LayoutParams) v.getLayoutParams()).topMargin = insets.getSystemWindowInsetTop();
                return insets.consumeSystemWindowInsets();
            }
        });

        showVideoFragment();

        presenter = new UserPresenter(this);
        presenter.getMyInfo();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onFailure(Throwable t) {

    }

    @Override
    public void onError(int code, String error) {

    }

    @Override
    public void showMyInfo(UserInfo info) {
        String userName = info.getUser().getBlogs().get(0).getName();
        SimpleDraweeView avatar = (SimpleDraweeView) findViewById(R.id.user_avatar);
        FrescoUtils.setTumblrAvatarUri(avatar, userName, 128);
        TextView name = (TextView) findViewById(R.id.user_name);
        name.setText(userName);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_video) {
            showVideoFragment();
        } else if (id == R.id.nav_photo) {
            showPhotoFragment();
        } else if (id == R.id.nav_downloading) {
            showDownloadFragment();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_downloaded) {
            Intent intent = new Intent(this, DownloadManagerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_following) {
            Intent intent = new Intent(this, FollowingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_like) {
            Intent intent = new Intent(this, LikesActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private VideoDashboardFragment videoDashboardFragment;

    private void showVideoFragment() {
        if (videoDashboardFragment == null) {
            videoDashboardFragment = new VideoDashboardFragment();
        }
        showFragment(videoDashboardFragment, "video");
        setTitle(R.string.page_video);
    }

    private DashboardFragment photoFragment;
    private void showPhotoFragment() {
        if (photoFragment == null) {
            photoFragment = new DashboardFragment();
        }
        showFragment(photoFragment, "photo");
        setTitle(R.string.page_photo);
    }

    private DownloadFragment downloadFragment;
    private void showDownloadFragment() {
        if (downloadFragment == null) {
            downloadFragment = new DownloadFragment();
        }
        showFragment(downloadFragment, "download");
        setTitle(R.string.download_toolbar_title);
    }

    private void showFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshData(EventRefresh refreshEvent) {
        EventBus.getDefault().removeStickyEvent(refreshEvent);
        if (videoDashboardFragment != null) {
            videoDashboardFragment.refreshData();
        }
        if (photoFragment != null) {
            photoFragment.refreshData();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toggleStatusBar(EventStatusBar eventStatusBar) {
        int flag = View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        View view = getWindow().getDecorView();
        if (eventStatusBar.getStatus() == View.VISIBLE) {
            view.setSystemUiVisibility(view.getSystemUiVisibility() & ~flag);
        } else {
            view.setSystemUiVisibility(view.getSystemUiVisibility() | flag);
        }
        Log.d("toggleStatusBar", "->" + (eventStatusBar.getStatus() == View.GONE));
    }

}
