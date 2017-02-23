package com.nutrition.express.model.data.bean;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.BaseVideoBean;

import java.io.File;

/**
 * Created by huang on 2/17/17.
 */

public class LocalVideo extends BaseVideoBean {
    private File file;

    public LocalVideo(File file) {
        this.file = file;
        sourceUri = Uri.fromFile(file);
        thumbnailUri = sourceUri;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getPath());
        int videoWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        width = ExpressApplication.width;
        height = width * videoHeight / videoWidth;
        retriever.release();
//        Log.d("LocalVideo", videoWidth + "-" + videoHeight + ":" + width + "-" + height);
    }

    public File getFile() {
        return file;
    }

}
