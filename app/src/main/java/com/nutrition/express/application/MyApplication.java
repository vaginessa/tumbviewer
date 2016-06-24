package com.nutrition.express.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nutrition.express.rest.RestClient;

/**
 * Created by huang on 4/25/16.
 */
public class MyApplication extends Application {
    public static int width;
    public static int height;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

        //init width and height
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        //init retrofit
        RestClient.getInstance().init(this);
    }
}
