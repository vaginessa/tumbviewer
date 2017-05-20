package com.nutrition.express.downloadservice;

import android.support.annotation.MainThread;

import java.lang.ref.WeakReference;

/**
 * Created by huang on 4/15/17.
 */

class TransferAction {
    TransferRequest request;
    long bytesRead;
    long contentLength;
    boolean done;
    private WeakReference<DownloadService.DownloadProgress> progressRef;

    TransferAction(TransferRequest request) {
        this.request = request;
    }

    void setProgressRef(DownloadService.DownloadProgress progress) {
        progressRef = new WeakReference<>(progress);
    }

    @MainThread
    void transferProgress() {
        if (progressRef != null) {
            DownloadService.DownloadProgress progress = progressRef.get();
            if (progress != null) {
                progress.update(bytesRead, contentLength, done);
            }
        }
    }

    @MainThread
    void transferFinish() {
        if (progressRef != null) {
            DownloadService.DownloadProgress progress = progressRef.get();
            if (progress != null) {
                progress.onDownloadFinish();
            }
        }
    }

    @MainThread
    void transferFailed() {
        if (progressRef != null) {
            DownloadService.DownloadProgress progress = progressRef.get();
            if (progress != null) {
                progress.onDownloadFailed();
            }
        }
    }
}
