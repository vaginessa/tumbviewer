package com.nutrition.express.application;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * Created by huang on 5/16/16.
 */
public class DownloadedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                Log.d("data", intent.toString());
                break;
        }
    }
}
