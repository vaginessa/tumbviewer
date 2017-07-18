package com.nutrition.express.download;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.common.ProgressCircle;
import com.nutrition.express.downloadservice.DownloadService;
import com.nutrition.express.downloadservice.TransferRequest;
import com.nutrition.express.util.DownloadManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadEvent;
import zlc.season.rxdownload2.entity.DownloadFlag;
import zlc.season.rxdownload2.entity.DownloadRecord;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by huang on 4/10/17.
 */

public class DownloadFragment extends Fragment {
    private DownloadService downloadService;
    private RxDownload rxDownload;
    private HashSet<Disposable> disposables = new HashSet<>();

    private CommonRVAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = CommonRVAdapter.newBuilder()
                .addItemType(TransferRequest.class, R.layout.item_download, new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new DownloadVH(view);
                    }
                })
                .addItemType(DownloadRecord.class, R.layout.item_download, new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new RxDownloadVH(view);
                    }
                })
                .build();
        recyclerView.setAdapter(adapter);

        getDownloadStatus();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    @Deprecated
    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @Deprecated
    public void setDownloadRecords(List<DownloadRecord> records) {
        List<Object> data = new ArrayList<>();
        rxDownload = DownloadManager.getInstance().getRxDownload();
        for (DownloadRecord record : records) {
            if (record.getFlag() == DownloadFlag.COMPLETED) {
                rxDownload.deleteServiceDownload(record.getUrl(), false);
            } else {
                data.add(record);
            }
        }
        if (adapter != null) {
            adapter.append(data.toArray(), false);
        }
    }

    private void getDownloadStatus() {
        rxDownload = DownloadManager.getInstance().getRxDownload();
        rxDownload.getTotalDownloadRecords()
                .subscribe(new Consumer<List<DownloadRecord>>() {
                    @Override
                    public void accept(List<DownloadRecord> downloadRecords) throws Exception {
                        setContentData(downloadRecords);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        setContentData(null);
                    }
                });
    }

    private void setContentData(List<DownloadRecord> records) {
        if (records != null) {
            List<Object> data = new ArrayList<>();
            for (DownloadRecord record : records) {
                if (record.getFlag() == DownloadFlag.COMPLETED) {
                    rxDownload.deleteServiceDownload(record.getUrl(), false);
                } else {
                    data.add(record);
                }
            }
            adapter.append(data.toArray(), false);
        } else {
            adapter.append(null, false);
        }
    }

    private class DownloadVH extends CommonViewHolder<TransferRequest>
            implements View.OnClickListener, DownloadService.DownloadProgress {
        private SimpleDraweeView thumbnailView;
        private TextView urlView;
        private ProgressCircle progressView;

        private TransferRequest request;

        DownloadVH(View itemView) {
            super(itemView);
            thumbnailView = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            progressView = (ProgressCircle) itemView.findViewById(R.id.progressCircle);
            urlView = (TextView) itemView.findViewById(R.id.url);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindView(TransferRequest request) {
            this.request = request;
            thumbnailView.setImageURI(request.getThumbnailUrl());
            urlView.setText(request.getVideoUrl());
            downloadService.getDownloadState(request, this);
        }

        @Override
        public void onClick(View v) {
            downloadService.startDownloadTarget(request);
        }

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            if (ViewCompat.isAttachedToWindow(progressView)) {
                progressView.setProgress(contentLength, bytesRead);
            }
        }

        @Override
        public void onDownloadFailed() {
        }

        @Override
        public void onDownloadFinish() {
            adapter.remove(getAdapterPosition());
        }
    }

    private class RxDownloadVH extends CommonViewHolder<DownloadRecord> implements View.OnClickListener {
        private SimpleDraweeView thumbnailView;
        private TextView urlView, progress;
        private ProgressCircle progressView;
        private Disposable disposable;
        private DownloadRecord record;
        private int status = 0;

        public RxDownloadVH(View itemView) {
            super(itemView);
            thumbnailView = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            progressView = (ProgressCircle) itemView.findViewById(R.id.progressCircle);
            progress = (TextView) itemView.findViewById(R.id.progress);
            urlView = (TextView) itemView.findViewById(R.id.url);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindView(final DownloadRecord downloadRecord) {
            this.record = downloadRecord;
            if (TextUtils.isEmpty(downloadRecord.getSaveName())) {
                String url = downloadRecord.getUrl();
                urlView.setText(url.substring(url.lastIndexOf("/") + 1));
            } else {
                urlView.setText(downloadRecord.getSaveName());
            }
            disposable = rxDownload.receiveDownloadStatus(downloadRecord.getUrl())
                    .subscribe(new Consumer<DownloadEvent>() {
                        @Override
                        public void accept(DownloadEvent downloadEvent) throws Exception {
                            Log.d("accept", "-" + downloadEvent.getFlag());
                            if (downloadEvent.getFlag() == DownloadFlag.FAILED) {
                                Log.w("Download", "accept: ", downloadEvent.getError());
                            }
                            disposables.add(disposable);
                            if (downloadEvent.getFlag() == DownloadFlag.STARTED) {
                                updateProgress(downloadEvent);
                            } else if (downloadEvent.getFlag() == DownloadFlag.COMPLETED) {
                                downloadComplete(getAdapterPosition());
                            } else if (downloadEvent.getFlag() == DownloadFlag.PAUSED) {

                            }
                            setStatus(downloadEvent);
                        }
                    });
        }

        private void setStatus(DownloadEvent event) {
            if (event.getFlag() == status) {
                return;
            }
            Uri uri;
            if (event.getFlag() == DownloadFlag.STARTED) {
                uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                        .path(String.valueOf(R.mipmap.ic_pause_circle_filled_black_24dp))
                        .build();
            } else if (event.getFlag() == DownloadFlag.FAILED) {
                uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                        .path(String.valueOf(R.mipmap.ic_failed))
                        .build();
            } else {
                uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                        .path(String.valueOf(R.mipmap.ic_play_circle_filled_black_24dp))
                        .build();
            }
            thumbnailView.setImageURI(uri);
        }

        @Override
        public void onClick(View view) {
            DownloadManager.getInstance().download(record.getUrl());
        }

        private void updateProgress(DownloadEvent event) {
            if (ViewCompat.isAttachedToWindow(progressView)) {
                DownloadStatus status = event.getDownloadStatus();
                progressView.setProgress(status.getTotalSize(), status.getDownloadSize());
                progress.setText(status.getFormatStatusString());
            } else {
                if (disposable != null) {
                    disposable.dispose();
                    disposables.remove(disposable);
                    disposable = null;
                }
            }
        }

        private void downloadComplete(int pos) {
            rxDownload.deleteServiceDownload(record.getUrl(), false);
            adapter.remove(pos);
        }
    }
}
