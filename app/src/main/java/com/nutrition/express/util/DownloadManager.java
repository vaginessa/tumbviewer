package com.nutrition.express.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.nutrition.express.BuildConfig;
import com.nutrition.express.R;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.model.event.EventPermission;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.functions.Consumer;
import zlc.season.rxdownload2.RxDownload;

/**
 * Created by huang on 7/4/17.
 */

public class DownloadManager {
    private static class Holder {
        private static DownloadManager holder = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return Holder.holder;
    }

    private RxDownload rxDownload;
    private Context context;

    private DownloadManager() {
        context = ExpressApplication.getApplication();
        rxDownload = RxDownload.getInstance(context);
        rxDownload.defaultSavePath(FileUtils.getVideoDir().getPath());
        rxDownload.maxDownloadNumber(3);
    }

    public void download(String url) {
        if (canWrite2Storage()) {
            rxDownload.serviceDownload(url)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            Toast.makeText(context, R.string.download_start, Toast.LENGTH_SHORT).show();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(context, R.string.reblog_failure, Toast.LENGTH_SHORT).show();
                            if (BuildConfig.DEBUG) {
                                Log.w("DownloadManager", "accept: ", throwable);
                            }
                        }
                    });
        }
    }

    private boolean canWrite2Storage() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            EventBus.getDefault().post(new EventPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            return false;
        }
        return true;
    }

    public RxDownload getRxDownload() {
        return rxDownload;
    }
}
