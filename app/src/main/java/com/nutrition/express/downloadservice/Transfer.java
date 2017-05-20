package com.nutrition.express.downloadservice;

import android.util.Log;

import com.nutrition.express.BuildConfig;
import com.nutrition.express.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by huang on 4/15/17.
 */

class Transfer implements Runnable, ProgressListener {
    private static final AtomicInteger nextId = new AtomicInteger();

    private int id;
    private TransferAction action;
    private OkHttpClient okHttpClient;
    private Dispatcher dispatcher;
    private long progressStep;
    private long nextTarget;

    Transfer(Dispatcher dispatcher, TransferAction action, OkHttpClient okHttpClient) {
        this.id = nextId.incrementAndGet();
        this.dispatcher = dispatcher;
        this.action = action;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public void run() {
        boolean success = download(action.request.videoUrl);
        if (success) {
            dispatcher.dispatchComplete(action);
        } else {
            dispatcher.dispatchFailed(action);
        }
    }

    private boolean download(String url) {
        File videoFile = FileUtils.createVideoFile(url);
        BufferedSink sink = null;
        ProgressResponseBody responseBody = null;
        try {
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            if (videoFile.isFile() && videoFile.length() > 0) {
                builder.addHeader("Range", "bytes=" + videoFile.length() + "-");
            }
            Request request = builder.build();
            Response response = okHttpClient.newCall(request).execute();
            responseBody = new ProgressResponseBody(response.body(), this);

            if (response.isSuccessful()) {
                action.contentLength = responseBody.contentLength();
                progressStep = responseBody.contentLength() / 100;
                nextTarget = progressStep;
                update(0, responseBody.contentLength(), false);
                if (videoFile.length() < responseBody.contentLength()) {
                    sink = Okio.buffer(Okio.appendingSink(videoFile));
                } else {
                    sink = Okio.buffer(Okio.sink(videoFile));
                }
                sink.writeAll(responseBody.source());
                return true;
            } else {
                //treat http response code 416(Range Not Satisfiable) as download success.
                if (response.code() == 416) {
                    return true;
                }
                if (BuildConfig.DEBUG) {
                    Log.e("DownloadService", "response: " + response.code() + "-" + response.message());
                }
            }
        } catch (IOException | NullPointerException | IllegalArgumentException e) {
            if (BuildConfig.DEBUG) {
                Log.e("DownloadService", "onHandleIntent: ", e);
            }
        } finally {
            try {
                if (sink != null) {
                    sink.close();
                }
                if (responseBody != null) {
                    responseBody.close();
                }
            } catch (IOException e) {

            }
        }
        return false;
    }

    @Override
    public void update(long bytesRead, long contentLength, boolean done) {
        if (bytesRead > nextTarget || done) {
            action.bytesRead = bytesRead;
            action.done = done;
            dispatcher.dispatchProgress(action);
            nextTarget += progressStep;
            if (BuildConfig.DEBUG) {
                Log.d("update", id + "/" + bytesRead + "/" + contentLength);
            }
        }
    }

}
