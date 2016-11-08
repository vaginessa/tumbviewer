package com.nutrition.express.blogposts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by huang on 5/26/16.
 */
public class PostPagerAdapter extends FragmentPagerAdapter {
    private String blogName;

    public PostPagerAdapter(FragmentManager fm, String blogName) {
        super(fm);
        this.blogName = blogName;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("blog_name", blogName);
        if (position == 0) {
            bundle.putInt("type", PostListActivity.POSTS_VIDEO_DEFAULT);
        } else if (position == 1) {
            bundle.putInt("type", PostListActivity.POSTS_VIDEO_LIKED);
        }
        PostListFragment fragment = new PostListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "列表";
        } else {
            return "喜欢";
        }
    }
}
