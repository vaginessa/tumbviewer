package com.nutrition.express.downloadservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.nutrition.express.R;
import com.nutrition.express.download.DownloadManagerActivity;
import com.nutrition.express.main.MainActivity;

import java.util.List;

import static com.nutrition.express.downloadservice.Dispatcher.TRANSFER_COMPLETE;
import static com.nutrition.express.downloadservice.Dispatcher.TRANSFER_FAILED;
import static com.nutrition.express.downloadservice.Dispatcher.TRANSFER_LIST;
import static com.nutrition.express.downloadservice.Dispatcher.TRANSFER_PROGRESS;

/**
 * Created by huang on 4/7/17.
 */

public class DownloadService extends Service {
    public static final String DOWNLOAD_REQUEST = "url";
    private final int notification_id = 1;
    private Dispatcher dispatcher;

    private NotificationCompat.Builder builder;
    private NotificationManager manager;

    private IBinder localBinder;

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        ResultHandler mainHandler = new ResultHandler(Looper.getMainLooper());
        dispatcher = new Dispatcher(mainHandler);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null) {
            TransferRequest transferRequest = intent.getParcelableExtra(DOWNLOAD_REQUEST);
            if (transferRequest != null && !TextUtils.isEmpty(transferRequest.getVideoUrl())) {
                dispatcher.dispatchSubmit(transferRequest);
            }
        }
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
//        builder.setContentTitle("Downloading: " + successHit + "/" + total);
        builder.setContentText(text);
        builder.setProgress(100, progress, progress <= 0);

        manager.notify(notification_id, builder.build());
    }

    private void showDoneNotification() {
        ensureNotificationBuilder();
        builder.setSmallIcon(R.mipmap.ic_done_all_white);
        builder.setContentTitle("Download complete");
//        builder.setContentText(successHit + "/" + total + " success, " + failureHit + " fail");
        builder.setProgress(0, 0, false);

        manager.notify(notification_id, builder.build());
    }

    public class LocalBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    public void getDownloadList(DownloadListener listener) {
        dispatcher.dispatchList(listener);
    }

    public void cancelDownloadTarget(TransferRequest request) {
    }

    public void startDownloadTarget(TransferRequest request) {
        dispatcher.dispatchSubmit(request);
    }

    public void getDownloadState(TransferRequest request, DownloadProgress progress) {
        dispatcher.dispatchState(new StateAction(request, progress));
    }

    static class StateAction {
        TransferRequest request;
        DownloadProgress progress;

        StateAction(TransferRequest request, DownloadProgress progress) {
            this.request = request;
            this.progress = progress;
        }
    }

    private static class ResultHandler extends Handler {
        ResultHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRANSFER_PROGRESS:
                    TransferAction action = (TransferAction) msg.obj;
                    action.transferProgress();
                    break;
                case TRANSFER_COMPLETE:
                    TransferAction action1 = (TransferAction) msg.obj;
                    action1.transferFinish();
                    break;
                case TRANSFER_FAILED:
                    TransferAction action2 = (TransferAction) msg.obj;
                    action2.transferFailed();
                    break;
                case TRANSFER_LIST:
                    TransferList list = (TransferList) msg.obj;
                    list.listener.onDownloadList(list.requestList);
                    break;
            }
        }
    }

    public interface DownloadListener {
        void onDownloadList(List<TransferRequest> requests);
    }

    public interface DownloadProgress extends ProgressListener {
        void onDownloadFailed();
        void onDownloadFinish();
    }
}
