package com.nutrition.express.application;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.nutrition.express.R;
import com.nutrition.express.util.FileUtils;

import java.io.File;

/**
 * Created by huang on 4/25/16.
 */
public class SystemDownload {
    public static long downloadVideo(Context context, String url) {
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (url != null) {
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            File dst = FileUtils.createVideoFile(uri);
            if (dst.exists()) {
                Toast.makeText(context, R.string.video_exist, Toast.LENGTH_SHORT).show();
            } else {
                request.setDestinationUri(Uri.fromFile(dst));
                request.allowScanningByMediaScanner();
                long id = manager.enqueue(request);
                Log.d("download id", "-->" + id);
                return id;
            }
        }
        return -1;
    }
}
