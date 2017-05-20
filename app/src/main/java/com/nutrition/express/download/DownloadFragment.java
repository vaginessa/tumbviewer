package com.nutrition.express.download;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.common.ProgressCircle;
import com.nutrition.express.downloadservice.DownloadService;
import com.nutrition.express.downloadservice.TransferRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 4/10/17.
 */

public class DownloadFragment extends Fragment implements DownloadService.DownloadListener {
    private List<Object> data = new ArrayList<>();
    private DownloadService downloadService;

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
                .setData(data)
                .build();
        recyclerView.setAdapter(adapter);

        this.downloadService.getDownloadList(this);
        return view;
    }

    @Override
    public void onDownloadList(List<TransferRequest> requests) {
        data.addAll(requests);
        adapter.notifyDataSetChanged();
    }

    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    private void cancelDownloadTarget(int pos) {
        if (downloadService != null) {
            downloadService.cancelDownloadTarget((TransferRequest) data.get(pos));
            data.remove(pos);
            adapter.notifyItemRemoved(pos);
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
//            cancelDownloadTarget(getAdapterPosition());
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
            data.remove(getAdapterPosition());
            adapter.notifyItemRemoved(getAdapterPosition());
        }
    }
}
