package com.nutrition.express.download;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nutrition.express.R;
import com.nutrition.express.common.CommonExoPlayerView;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.common.ExoPlayerInstance;
import com.nutrition.express.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 2/17/17.
 */

public class VideoFragment extends Fragment {
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;
    private ExoPlayerInstance playerInstance;
    private CommonExoPlayerView currentPlayView;

    private List<Object> videoInfos;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        playerInstance = new ExoPlayerInstance(context);
        videoInfos = new ArrayList<>();
        File videoDir = FileUtils.getVideoDir();
        File[] files = videoDir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : videoDir.listFiles()) {
                if (file.getName().endsWith(".mp4")) {
                    videoInfos.add(new LocalVideo(file));
                }
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        playerInstance.releasePlayer();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_video, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = CommonRVAdapter.newBuilder()
                .addItemType(LocalVideo.class, R.layout.item_download_video,
                        new CommonRVAdapter.CreateViewHolder() {
                            @Override
                            public CommonViewHolder createVH(View view) {
                                return new VideoViewHolder(view);
                            }
                        })
                .setData(videoInfos)
                .build();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Fresco.getImagePipeline().resume();
                } else {
                    Fresco.getImagePipeline().pause();
                }
            }
        });
        return view;
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    private class VideoViewHolder extends CommonViewHolder<LocalVideo> implements View.OnClickListener {
        private CommonExoPlayerView playerView;

        private VideoViewHolder(View itemView) {
            super(itemView);
            playerView = (CommonExoPlayerView) itemView;
            playerView.setPlayerInstance(playerInstance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void bindView(LocalVideo localVideo) {
            playerView.bindLocalVideo(localVideo);
        }

        @Override
        public void onClick(View v) {
            if (playerView == currentPlayView && playerView.isConnected()) {
                playerView.show();
            } else {
                if (currentPlayView != null) {
                    currentPlayView.disconnect();
                }
                playerView.connect();
                currentPlayView = playerView;
            }
        }
    }

}
