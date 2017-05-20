package com.nutrition.express.downloadservice;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.google.gson.reflect.TypeToken;
import com.nutrition.express.model.helper.LocalPersistenceHelper;
import com.nutrition.express.model.rest.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by huang on 4/15/17.
 */

class Dispatcher {
    static final String DOWNLOAD_LIST = "download_list";
    static final int TRANSFER_STATE = 0;
    static final int TRANSFER_SUBMIT = 1;
    static final int TRANSFER_COMPLETE = 2;
    static final int TRANSFER_CANCEL = 3;
    static final int TRANSFER_FAILED = 4;
    static final int TRANSFER_PROGRESS = 5;
    static final int TRANSFER_LIST = 6;

    final OkHttpClient okHttpClient;
    final DispatcherThread dispatcherThread;
    final ExecutorService service;
    final Handler handler;
    final Handler mainThreadHandler;
    final Map<TransferRequest, TransferAction> transferMap;
    final Map<TransferRequest, TransferAction> failedActions;

    private boolean isRestored = false;

    Dispatcher(Handler mainThreadHandler) {
        this.okHttpClient = RestClient.getInstance().getOkHttpClient();
        this.service = new ThreadPoolExecutor(2, 4, 1L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>());
        this.mainThreadHandler = mainThreadHandler;
        this.dispatcherThread = new DispatcherThread();
        this.dispatcherThread.start();
        this.handler = new DispatcherHandler(dispatcherThread.getLooper(), this);
        transferMap = new LinkedHashMap<>();
        failedActions = new HashMap<>();
    }

    void shutdown() {
        service.shutdown();
        dispatcherThread.quit();
    }

    void dispatchSubmit(TransferRequest request) {
        handler.sendMessage(handler.obtainMessage(TRANSFER_SUBMIT, request));
    }

    void dispatchState(DownloadService.StateAction stateAction) {
        handler.sendMessage(handler.obtainMessage(TRANSFER_STATE, stateAction));
    }

    void dispatchList(DownloadService.DownloadListener listener) {
        handler.sendMessage(handler.obtainMessage(TRANSFER_LIST, listener));
    }

    void dispatchComplete(TransferAction action) {
        handler.sendMessage(handler.obtainMessage(TRANSFER_COMPLETE, action));
    }

    void dispatchFailed(TransferAction action) {
        handler.sendMessage(handler.obtainMessage(TRANSFER_FAILED, action));
    }

    void dispatchProgress(TransferAction action) {
        handler.sendMessage(handler.obtainMessage(TRANSFER_PROGRESS, action));
    }

    private void performList(DownloadService.DownloadListener listener) {
        restoreState();
        List<TransferRequest> requests = new ArrayList<>(transferMap.keySet());
        TransferList list = new TransferList(listener, requests);
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TRANSFER_LIST, list));
    }

    private void performState(DownloadService.StateAction action) {
        TransferAction transferAction = transferMap.get(action.request);
        if (transferAction != null) {
            transferAction.setProgressRef(action.progress);
        }
    }

    private void performSubmit(TransferRequest request) {
        if (service.isShutdown()) {
            return;
        }
        restoreState();
        TransferAction action = transferMap.get(request);
        if (action == null) {
            action = new TransferAction(request);
            transferMap.put(request, action);
            saveState();
        }
        Transfer transfer = new Transfer(this, action, okHttpClient);
        service.submit(transfer);
    }

    private void performComplete(TransferAction action) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TRANSFER_COMPLETE, action));
        transferMap.remove(action.request);
        saveState();
    }

    private void performFailed(TransferAction action) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TRANSFER_FAILED, action));
        failedActions.put(action.request, action);
        saveState();
    }

    private void performCancel(TransferAction action) {

    }

    private void performProgress(TransferAction action) {
        mainThreadHandler.sendMessage(mainThreadHandler.obtainMessage(TRANSFER_PROGRESS, action));
    }

    private void saveState() {
        ArrayList<TransferRequest> list = new ArrayList<>(transferMap.keySet());
        LocalPersistenceHelper.storeShortContent(DOWNLOAD_LIST, list);
    }

    private void restoreState() {
        if (!isRestored) {
            ArrayList<TransferRequest> list = LocalPersistenceHelper.getShortContent(DOWNLOAD_LIST,
                    new TypeToken<ArrayList<TransferRequest>>(){}.getType());
            if (list != null) {
                for (TransferRequest request : list) {
                    transferMap.put(request, new TransferAction(request));
                }
            }
            isRestored = true;
        }
    }

    private static class DispatcherHandler extends Handler {
        private final Dispatcher dispatcher;

        DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRANSFER_STATE:
                    dispatcher.performState((DownloadService.StateAction) msg.obj);
                    break;
                case TRANSFER_LIST:
                    dispatcher.performList((DownloadService.DownloadListener) msg.obj);
                    break;
                case TRANSFER_SUBMIT:
                    dispatcher.performSubmit((TransferRequest) msg.obj);
                    break;
                case TRANSFER_COMPLETE:
                    dispatcher.performComplete((TransferAction) msg.obj);
                    break;
                case TRANSFER_CANCEL:
                    dispatcher.performCancel((TransferAction) msg.obj);
                    break;
                case TRANSFER_FAILED:
                    dispatcher.performFailed((TransferAction) msg.obj);
                    break;
                case TRANSFER_PROGRESS:
                    dispatcher.performProgress((TransferAction) msg.obj);
                    break;
            }
        }
    }

    private static class DispatcherThread extends HandlerThread {
        DispatcherThread() {
            super("download.service.dispatcher");
        }
    }
}
