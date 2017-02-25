package com.nutrition.express.download;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nutrition.express.R;
import com.nutrition.express.common.CommonExoPlayerView;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.common.ExoPlayerInstance;
import com.nutrition.express.model.data.bean.LocalVideo;
import com.nutrition.express.util.FileUtils;
import com.nutrition.express.util.PreferencesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huang on 2/17/17.
 */

public class VideoFragment extends Fragment {
    private static final String SHOW_USER_VIDEO = "SUV";
    private RecyclerView recyclerView;
    private CommonRVAdapter adapter;
    private ExoPlayerInstance playerInstance;

    private List<Object> userVideo;
    private List<Object> allVideo;
    private List<Object> videoList = new ArrayList<>();
    private boolean showUserVideo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        playerInstance = ExoPlayerInstance.getInstance();
        showUserVideo = PreferencesUtils.getBoolean(SHOW_USER_VIDEO, false);
        if (showUserVideo) {
            initVideoDataUser();
        } else {
            initVideoDataAll();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && playerInstance != null) {
            playerInstance.pausePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        playerInstance.resumePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
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
                .setData(videoList)
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_download_video, menu);
        MenuItem menuItem = menu.findItem(R.id.show_user_video);
        if (showUserVideo) {
            menuItem.setChecked(true);
        } else {
            menuItem.setChecked(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_video) {
            showDeleteDialog();
            return true;
        } else if (item.getItemId() == R.id.show_user_video) {
            item.setChecked(!item.isChecked());
            showUserVideo = item.isChecked();
            if (showUserVideo) {
                initVideoDataUser();
            } else {
                initVideoDataAll();
            }
            adapter.notifyDataSetChanged();
            PreferencesUtils.putBoolean(SHOW_USER_VIDEO, showUserVideo);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initVideoDataUser() {
        if (userVideo == null) {
            userVideo = new ArrayList<>();
            File userVideoDir = FileUtils.getVideoDir();
            if (userVideoDir.isDirectory()) {
                getVideoFile(userVideoDir, userVideo);
                sortPhotoData(userVideo);
            }
        }
        videoList.clear();
        videoList.addAll(userVideo);
    }

    private void initVideoDataAll() {
        if (allVideo == null) {
            allVideo = new ArrayList<>();
            File publicVideoDir = FileUtils.getPublicVideoDir();
            if (publicVideoDir.isDirectory()) {
                getVideoFile(publicVideoDir, allVideo);
                sortPhotoData(allVideo);
            }
        }
        videoList.clear();
        videoList.addAll(allVideo);
    }

    private void getVideoFile(File dir, List<Object> list) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                getVideoFile(file, list);
            } else {
                if (file.getName().endsWith(".mp4")) {
                    list.add(new LocalVideo(file));
                }
            }
        }
    }

    private void sortPhotoData(List<Object> videos) {
        Collections.sort(videos, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                long x = ((LocalVideo) o1).getFile().lastModified();
                long y = ((LocalVideo) o2).getFile().lastModified();
                return (x < y) ? 1 : ((x == y) ? 0 : -1);
            }
        });
    }
    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtils.deleteFile(FileUtils.getPublicVideoDir());
                onAllVideosDeleted();
            }
        });
        builder.setNegativeButton(R.string.pic_cancel, null);
        builder.setTitle(R.string.download_video_delete_title);
        builder.show();
    }


    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    public void onAllVideosDeleted() {
        adapter.resetData(null, false);
    }

    public void onVideoDeleted(int pos) {
        adapter.remove(pos);
    }

    private class VideoViewHolder extends CommonViewHolder<LocalVideo>
            implements View.OnClickListener, View.OnLongClickListener {
        private CommonExoPlayerView playerView;

        private LocalVideo video;

        private VideoViewHolder(View itemView) {
            super(itemView);
            playerView = (CommonExoPlayerView) itemView;
            playerView.setPlayerInstance(playerInstance);
            playerView.setOnClickListener(this);
            playerView.setOnLongClickListener(this);
        }

        @Override
        public void bindView(LocalVideo localVideo) {
            video = localVideo;
            playerView.bindVideo(localVideo);
        }

        @Override
        public void onClick(View v) {
            if (playerView.isConnected()) {
                playerView.show();
            } else {
                playerView.connect();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setPositiveButton(R.string.delete_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FileUtils.deleteFile(video.getFile());
                    onVideoDeleted(getAdapterPosition());
                }
            });
            builder.setNegativeButton(R.string.pic_cancel, null);
            builder.setTitle(R.string.download_delete_title);
            builder.show();
            return true;
        }
    }

}
