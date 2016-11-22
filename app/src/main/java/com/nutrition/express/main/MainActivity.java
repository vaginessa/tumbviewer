package com.nutrition.express.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> list = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        final DashboardFragment videoFragment = new DashboardFragment();
        final PhotoDashboardFragment photoFragment = new PhotoDashboardFragment();
        list.add(videoFragment);
        titles.add(getString(R.string.page_video));
        list.add(photoFragment);
        titles.add(getString(R.string.page_photo));
        list.add(new SearchFragment());
        titles.add(getString(R.string.page_search));
        list.add(new UserFragment());
        titles.add(getString(R.string.page_user));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new CommonPagerAdapter(getSupportFragmentManager(), list, titles));
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
                if (tab.getPosition() == 0) {
                    videoFragment.scrollToTop();
                } else if (tab.getPosition() == 1) {
                    photoFragment.scrollToTop();
                }
            }
        });
    }


}
