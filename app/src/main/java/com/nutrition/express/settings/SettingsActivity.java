package com.nutrition.express.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nutrition.express.R;
import com.nutrition.express.login.LoginActivity;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.data.bean.TumblrApp;
import com.nutrition.express.register.RegisterActivity;

import java.util.List;

/**
 * Created by huang on 11/11/16.
 */

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.settings_register).setOnClickListener(this);
        findViewById(R.id.settings_clear_cache).setOnClickListener(this);
        findViewById(R.id.settings_tumblr_apps).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_clear_cache:
                showClearCacheDialog();
                break;
            case R.id.settings_tumblr_apps:
                showTumblrApps();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_logout:
                showLogoutDialog();
                return true;
            case R.id.settings_tumblr_limit:
                showTumblrLimitInfo();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.settings_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataManager.getInstance().logout();
                gotoLogin();
            }
        });
        builder.setNegativeButton(R.string.pic_cancel, null);
        builder.setTitle(R.string.settings_logout_title);
        builder.show();
    }

    private void showClearCacheDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.settings_clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearFrescoDiskCache();
            }
        });
        builder.setNegativeButton(R.string.pic_cancel, null);
        builder.setTitle(R.string.settings_clear_cache);
        builder.show();
    }

    private void showTumblrLimitInfo() {
        TumblrApp app = DataManager.getInstance().getUsingTumblrApp();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.settings_limit_info,
                app.getDayLimit(),
                app.getDayRemaining(),
                app.getHourLimit(),
                app.getHourRemaining()
        ));
        builder.show();
    }

    private void showTumblrApps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<TumblrApp> list = DataManager.getInstance().getTumblrAppList();
        String[] keys = new String[list.size()];
        int checkedItem = 0;
        for (int i = 0; i < keys.length; i++) {
            keys[i] = list.get(i).getApiKey();
            if (list.get(i).isUsing()) {
                checkedItem = i;
            }
        }
        builder.setSingleChoiceItems(keys, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (DataManager.getInstance().setUsingTumblrApp(which)) {
                    DataManager.getInstance().logout();
                    gotoLogin();
                }
            }
        });
        builder.show();
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void clearFrescoDiskCache() {
        Fresco.getImagePipeline().clearDiskCaches();
        Toast.makeText(this, R.string.settings_clear_ok, Toast.LENGTH_SHORT).show();
    }

}
