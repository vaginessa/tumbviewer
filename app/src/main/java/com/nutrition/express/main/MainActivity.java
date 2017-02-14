package com.nutrition.express.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nutrition.express.R;
import com.nutrition.express.common.CommonPagerAdapter;
import com.nutrition.express.login.LoginActivity;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.data.bean.TumblrAccount;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String STORAGE_PERMISSION = "WRITE_EXTERNAL_STORAGE";
    public static final String ERROR_429 = "ERROR_429"; //
    public static final String ERROR_401 = "ERROR_401"; //

    private TumblrAccount positiveAccount;

    private ViewPager viewPager;

    private DashboardFragment videoFragment;
    private PhotoDashboardFragment photoFragment;

    private List<Fragment> list = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
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
                if (tab.getPosition() == 0 && videoFragment != null) {
                    videoFragment.scrollToTop();
                } else if (tab.getPosition() == 1 && photoFragment != null) {
                    photoFragment.scrollToTop();
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STORAGE_PERMISSION);
        intentFilter.addAction(ERROR_401);
        intentFilter.addAction(ERROR_429);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        DataManager.getInstance().refreshData();

        titles.add(getString(R.string.page_video));
        titles.add(getString(R.string.page_photo));
        titles.add(getString(R.string.page_search));
        titles.add(getString(R.string.page_user));
        loadData();
    }

    public void loadData() {
        //reset the referable blog
        DataManager.getInstance().clearReferenceBlog();

        positiveAccount = DataManager.getInstance().getPositiveAccount();

        videoFragment = new DashboardFragment();
        photoFragment = new PhotoDashboardFragment();
        list.clear();
        list.add(videoFragment);
        list.add(photoFragment);
        list.add(new SearchFragment());
        list.add(new UserFragment());
        viewPager.setAdapter(new CommonPagerAdapter(getSupportFragmentManager(), list, titles));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataManager dataManager = DataManager.getInstance();
        if (positiveAccount != dataManager.getPositiveAccount()) {
            if (dataManager.isLogin()) {
                loadData();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mark down
        positiveAccount = DataManager.getInstance().getPositiveAccount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case STORAGE_PERMISSION:
                    requestStoragePermission();
                    break;
                case ERROR_401:
                    Toast.makeText(MainActivity.this, "Unauthorized, please login", Toast.LENGTH_SHORT).show();
                    gotoLogin();
                    break;
                case ERROR_429:
                    Toast.makeText(MainActivity.this, "429 error, please login again", Toast.LENGTH_SHORT).show();
                    gotoLogin();
                    break;
            }
        }
    };

    private void gotoLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
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
