package com.nutrition.express.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.nutrition.express.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> list = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        list.add(new DashboardFragment());
        titles.add(getString(R.string.page_video));
        list.add(new PhotoDashboardFragment());
        titles.add(getString(R.string.page_photo));
        list.add(new SearchFragment());
        titles.add(getString(R.string.page_search));
        list.add(new UserFragment());
        titles.add(getString(R.string.page_user));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), list, titles));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }


}
