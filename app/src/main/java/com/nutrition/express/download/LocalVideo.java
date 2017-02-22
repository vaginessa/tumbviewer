package com.nutrition.express.download;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import com.nutrition.express.application.ExpressApplication;

import java.io.File;

/**
 * Created by huang on 2/17/17.
 */

public class LocalVideo {
    private File file;
    private Uri uri;
    private int width;
    private int height;

    public LocalVideo(File file) {
        this.file = file;
        uri = Uri.fromFile(file);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getPath());
        int videoWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        width = ExpressApplication.width;
        height = width * videoHeight / videoWidth;
        retriever.release();
        Log.d("LocalVideo", videoWidth + "-" + videoHeight + ":" + width + "-" + height);
    }

    public File getFile() {
        return file;
    }

    public Uri getUri() {
        return uri;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
