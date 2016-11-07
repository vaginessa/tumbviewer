package com.nutrition.express.blogposts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nutrition.express.R;
import com.nutrition.express.useraction.FollowBlogContract;
import com.nutrition.express.useraction.FollowBlogPresenter;

/**
 * Created by huang on 5/16/16.
 */
public class VideoListActivity extends AppCompatActivity implements FollowBlogContract.View {
    public static final int POSTS_VIDEO_DEFAULT = 0;
    public static final int POSTS_VIDEO_LIKED = 1;
    private boolean granted = false;
    private MenuItem followItem;
    private FollowBlogPresenter followBlogPresenter;
    private boolean followed = false;
    private String blogName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Intent intent = getIntent();
        blogName = intent.getStringExtra("blog_name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(blogName);
        }
        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new VideoPagerAdapter(getSupportFragmentManager(), blogName));
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        super.onTabReselected(tab);
                        if (tab.getPosition() == viewPager.getCurrentItem()) {
                            //hack code;
                            VideoListFragment fragment = (VideoListFragment) getSupportFragmentManager()
                                    .findFragmentByTag("android:switcher:" + R.id.viewPager + ":"
                                            + viewPager.getCurrentItem());
                            if (fragment != null) {
                                fragment.scrollToTop();
                            }
                        }
                    }
                });

        followBlogPresenter = new FollowBlogPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blog, menu);
        followItem = menu.findItem(R.id.blog_follow);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.blog_follow:
                if (followed) {
                    followBlogPresenter.unfollow(blogName);
                } else {
                    followBlogPresenter.follow(blogName);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFollow() {
        followItem.setTitle(R.string.blog_unfollow);
        followed = true;
    }

    @Override
    public void onUnfollow() {
        followItem.setTitle(R.string.blog_follow);
        followed = false;
    }

    @Override
    public void setPresenter(FollowBlogContract.Presenter presenter) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!granted) {
            requestStoragePermission();
        }
    }

    public void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            granted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
