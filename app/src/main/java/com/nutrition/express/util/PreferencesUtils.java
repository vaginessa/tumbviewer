package com.nutrition.express.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nutrition.express.application.ExpressApplication;

/**
 * Created by huang on 9/23/16.
 */

public class PreferencesUtils {
    public static void putDefaultString(String key, String value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaultString(String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        return prefs.getString(key, "");
    }

}
