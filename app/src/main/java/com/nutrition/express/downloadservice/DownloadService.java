package com.nutrition.express.downloadservice;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nutrition.express.BuildConfig;
import com.nutrition.express.R;
import com.nutrition.express.download.DownloadManagerActivity;
import com.nutrition.express.main.MainActivity;
import com.nutrition.express.model.helper.LocalPersistenceHelper;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import static com.nutrition.express.util.FileUtils.getVideoDir;

/**
 * Created by huang on 4/7/17.
 */

public class DownloadService extends IntentService {
    public static final String DOWNLOAD_URL = "url";
    public static final String DOWNLOAD_LIST = "download_list";
    private final int notification_id = 1;
    private OkHttpClient okHttpClient;
    private ConcurrentLinkedQueue<String> urlQueue;
    private int total;
    private int successHit;
    private int failureHit;

    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    private IBinder localBinder;

    public DownloadService() {
        super("Humblr.download.service");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        ensureDownloadQueue();
        if (intent != null) {
            String url = intent.getStringExtra(DOWNLOAD_URL);
            if (!TextUtils.isEmpty(url)) {
                //check if video exist
                File video = createVideo(url);
                if (video.isFile()) {
                    toastMessage(getString(R.string.video_exist));
                } else {
                    urlQueue.offer(url);
                    LocalPersistenceHelper.storeShortContent(DOWNLOAD_LIST, urlQueue);
                    total++;
                    toastMessage(getString(R.string.download_start));
                }
            }
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (localBinder == null) {
            localBinder = new LocalBinder();
        }
        return localBinder;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (urlQueue.isEmpty()) {
            return;
        }
        ensureOkHttpClient();
        boolean success;
        while (!urlQueue.isEmpty()) {
            success = download(urlQueue.poll());
            if (success) {
                successHit++;
            } else {
                failureHit++;
            }
            //update persistence data;
            LocalPersistenceHelper.storeShortContent(DOWNLOAD_LIST, urlQueue);
        }
        toastMessage(getString(R.string.download_complete));
        showDoneNotification();
        if (urlQueue.isEmpty()) {
            total = 0;
            successHit = 0;
            failureHit = 0;
        }
    }

    private boolean download(String url) {
        final File videoFile = createVideo(url);
        showNotificationProgress(videoFile.getName(), 0);
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
            responseBody = new ProgressResponseBody(response.body(),
                    new DownloadProgressListener(this));

            if (response.isSuccessful()) {
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

    private void ensureDownloadQueue() {
        if (urlQueue == null) {
            urlQueue = LocalPersistenceHelper.getShortContent(DOWNLOAD_LIST,
                    new TypeToken<ConcurrentLinkedQueue<String>>(){}.getType());
            if (urlQueue == null) {
                urlQueue = new ConcurrentLinkedQueue<>();
            }
            total = urlQueue.size();
        }
    }

    private void ensureOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().build();
        }
    }

    private File createVideo(String url) {
        File dir = getVideoDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name;
        int index = url.lastIndexOf("/");
        if (index > 0) {
            name = url.substring(url.lastIndexOf("/"));
        } else {
            name = url;
        }
        return new File(dir, name);
    }

    private void toastMessage(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Intent intent = new Intent(MainActivity.TOAST_MESSAGE);
        intent.putExtra(MainActivity.TOAST_MESSAGE, msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void ensureNotificationBuilder() {
        if (builder == null) {
            Intent intent = new Intent(this, DownloadManagerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
        }
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
    }

    private void showNotificationProgress(String text, int progress) {
        ensureNotificationBuilder();
        builder.setSmallIcon(R.mipmap.ic_file_download_white);
        builder.setContentTitle("Downloading: " + successHit + "/" + total);
        builder.setContentText(text);
        builder.setProgress(100, progress, progress <= 0);

        manager.notify(notification_id, builder.build());
    }

    private void showDoneNotification() {
        ensureNotificationBuilder();
        builder.setSmallIcon(R.mipmap.ic_done_all_white);
        builder.setContentTitle("Download complete");
        builder.setContentText(successHit + "/" + total + " success, " + failureHit + " fail");
        builder.setProgress(0, 0, false);

        manager.notify(notification_id, builder.build());
    }

    //https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java
    private static class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

    private static class DownloadProgressListener implements ProgressListener {
        private DownloadService service;
        private int lastProgress = -6;
        private String length = null;


        public DownloadProgressListener(DownloadService service) {
            this.service = service;
        }

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            int progress = (int) (bytesRead * 100.0f / contentLength);
            if (progress - lastProgress > 5) {
                lastProgress = progress;
                if (length == null) {
                    length = Formatter.formatShortFileSize(service, contentLength);
                }
                service.showNotificationProgress(length, progress);
            }
        }
    }

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public Queue<String> getDownloadQueue() {
        ensureDownloadQueue();
        return urlQueue;
    }

    public void cancelDownloadTarget(String url) {
        if (urlQueue.remove(url)) {
            total--;
        }
    }
}
