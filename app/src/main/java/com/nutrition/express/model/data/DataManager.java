package com.nutrition.express.model.data;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.reflect.TypeToken;
import com.nutrition.express.application.Constants;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.model.data.bean.TumblrApp;
import com.nutrition.express.model.helper.LocalPersistenceHelper;
import com.nutrition.express.model.rest.bean.UserInfoItem;
import com.nutrition.express.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 10/18/16.
 */

public class DataManager {
    private static final String TUMBLR_APP = "tumblr_app";
    private String token;
    private String secret;
    private List<TumblrApp> tumblrAppList;
    private TumblrApp using;
    private UserInfoItem users;

    private static class Holder {
        private static DataManager holder = new DataManager();
    }

    public static DataManager getInstance() {
        return Holder.holder;
    }

    private DataManager() {
        token = PreferencesUtils.getString("access_token");
        secret = PreferencesUtils.getString("access_secret");
        tumblrAppList = LocalPersistenceHelper.getShortContent(TUMBLR_APP,
                new TypeToken<ArrayList<TumblrApp>>(){}.getType());
        if (tumblrAppList == null || tumblrAppList.size() == 0) {
            tumblrAppList = new ArrayList<>();
            using = new TumblrApp(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
            using.setUsing(true);
            tumblrAppList.add(using);
        } else {
            for (TumblrApp app : tumblrAppList) {
                if (app.isUsing()) {
                    using = app;
                }
            }
            if (using == null) {
                using = tumblrAppList.get(0);
                using.setUsing(true);
            }
        }
    }

    public String getToken() {
        return token;
    }

    public String getSecret() {
        return secret;
    }

    public void loginSuccess(String token, String secret) {
        this.token = token;
        this.secret = secret;
        PreferencesUtils.putString("access_token", token);
        PreferencesUtils.putString("access_secret", secret);
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret);
    }

    public void logout() {
        token = null;
        secret = null;
        PreferencesUtils.putString("access_token", null);
        PreferencesUtils.putString("access_secret", null);
        clearCookies();
    }

    private void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManager =
                    CookieSyncManager.createInstance(ExpressApplication.getApplication());
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.startSync();
            cookieSyncManager.sync();
        }
    }

    public TumblrApp getUsingTumblrApp() {
//        if (using.isOutOfLimit()) {
//            nextUsing();
//        }
        return using;
    }

    public boolean setUsingTumblrApp(int index) {
        if (index < tumblrAppList.size() && !tumblrAppList.get(index).isUsing()) {
            using.setUsing(false);
            using = tumblrAppList.get(index);
            using.setUsing(true);
            LocalPersistenceHelper.storeShortContent(TUMBLR_APP, tumblrAppList);
            return true;
        }
        return false;
    }

    public List<TumblrApp> getTumblrAppList() {
        return tumblrAppList;
    }

    public void addTumblrApp(String key, String secret) {
        if (using != null) {
            using.setUsing(false);
        }
        using = new TumblrApp(key, secret);
        using.setUsing(true);
        tumblrAppList.add(using);
        LocalPersistenceHelper.storeShortContent(TUMBLR_APP, tumblrAppList);
    }

    public void updateTumblrAppInfo(String dayLimit, String dayRemaining, String dayReset,
                                    String hourLimit, String hourRemaining, String hourReset) {
        using.setDayLimit(dayLimit);
        using.setDayRemaining(dayRemaining);
        using.setDayReset(dayReset);
        using.setHourLimit(hourLimit);
        using.setHourRemaining(hourRemaining);
        using.setHourReset(hourReset);
//        if (using.isOutOfLimit()) {
//            nextUsing();
//        }
    }

    /**
     * switch to other app need login again
     */
    private void nextUsing() {
        for (TumblrApp app : tumblrAppList) {
            if (!app.isOutOfLimit()) {
                using.setUsing(false);
                using = app;
                using.setUsing(true);
            }
        }
    }

    public UserInfoItem getUsers() {
        return users;
    }

    public void setUsers(UserInfoItem users) {
        this.users = users;
    }

}
