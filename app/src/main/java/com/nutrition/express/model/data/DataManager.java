package com.nutrition.express.model.data;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.util.PreferencesUtils;

/**
 * Created by huang on 10/18/16.
 */

public class DataManager {
    private String token;
    private String secret;

    private static class Holder {
        private static DataManager holder = new DataManager();
    }

    public static DataManager getInstance() {
        return Holder.holder;
    }

    private DataManager() {
        token = PreferencesUtils.getDefaultString("access_token");
        secret = PreferencesUtils.getDefaultString("access_secret");
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
        PreferencesUtils.putDefaultString("access_token", token);
        PreferencesUtils.putDefaultString("access_secret", secret);
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(token) && !TextUtils.isEmpty(secret);
    }

    public void logout() {
        token = null;
        secret = null;
        PreferencesUtils.putDefaultString("access_token", null);
        PreferencesUtils.putDefaultString("access_secret", null);
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

}
