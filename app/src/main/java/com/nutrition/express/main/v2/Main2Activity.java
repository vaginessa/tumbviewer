package com.nutrition.express.main.v2;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.download.DownloadFragment;
import com.nutrition.express.download.DownloadManagerActivity;
import com.nutrition.express.following.FollowingActivity;
import com.nutrition.express.likes.LikesActivity;
import com.nutrition.express.login.LoginActivity;
import com.nutrition.express.main.DashboardFragment;
import com.nutrition.express.main.UserContract;
import com.nutrition.express.main.UserPresenter;
import com.nutrition.express.main.VideoDashboardFragment;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.rest.bean.UserInfo;
import com.nutrition.express.search.SearchActivity;
import com.nutrition.express.settings.SettingsActivity;
import com.nutrition.express.util.FrescoUtils;

import static com.nutrition.express.main.MainActivity.ERROR_401;
import static com.nutrition.express.main.MainActivity.ERROR_429;
import static com.nutrition.express.main.MainActivity.STORAGE_PERMISSION;
import static com.nutrition.express.main.MainActivity.TOAST_MESSAGE;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserContract.View {

    private UserContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

        initData();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initData() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STORAGE_PERMISSION);
        intentFilter.addAction(ERROR_401);
        intentFilter.addAction(ERROR_429);
        intentFilter.addAction(TOAST_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        DataManager.getInstance().refreshData();
        presenter = new UserPresenter(this);
        presenter.getMyInfo();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
        presenter.onDetach();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case STORAGE_PERMISSION:
                    requestStoragePermission();
                    break;
                case ERROR_401:
                    Toast.makeText(Main2Activity.this, "Unauthorized, please login", Toast.LENGTH_SHORT).show();
                    gotoLogin();
                    break;
                case ERROR_429:
                    Toast.makeText(Main2Activity.this, "429 error, please login again", Toast.LENGTH_SHORT).show();
                    gotoLogin();
                    break;
                case TOAST_MESSAGE:
                    Toast.makeText(Main2Activity.this, intent.getStringExtra(TOAST_MESSAGE), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void gotoLogin() {
        Intent loginIntent = new Intent(Main2Activity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.putExtra("type", LoginActivity.ROUTE_SWITCH);
        startActivity(loginIntent);
    }

    public void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
