package com.nutrition.express.blogposts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nutrition.express.R;
import com.nutrition.express.application.SystemDownload;
import com.nutrition.express.common.CommonExoPlayerView;
import com.nutrition.express.common.ExoPlayerInstance;
import com.nutrition.express.model.data.bean.OnlineVideo;
import com.nutrition.express.model.data.bean.VideoPostsItem;

/**
 * Created by huang on 2/23/17.
 */

public class VideoPhotoPostVH extends PhotoPostVH<VideoPostsItem> {
    private CommonExoPlayerView playerView;
    private ImageView downloadView;

    private OnlineVideo onlineVideo;

    public VideoPhotoPostVH(View itemView, ExoPlayerInstance playerInstance) {
        super(itemView);
        playerView = (CommonExoPlayerView) itemView.findViewById(R.id.post_video);
        playerView.setPlayerInstance(playerInstance);
        playerView.setOnClickListener(this);
        downloadView = (ImageView) itemView.findViewById(R.id.post_download);
        downloadView.setOnClickListener(this);
    }

    @Override
    public void bindView(VideoPostsItem videoPostsItem) {
        onlineVideo = videoPostsItem.getOnlineVideo(); //set this before call super.bindView();
        super.bindView(videoPostsItem);

        if (TextUtils.isEmpty(postsItem.getVideo_url())) {
            downloadView.setVisibility(View.GONE);
        } else {
            downloadView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setVideoContent() {
        playerView.bindVideo(onlineVideo);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post_video) {
            if (TextUtils.isEmpty(postsItem.getVideo_type())) {
                return;
            }
            switch (postsItem.getVideo_type()) {
                case "tumblr":
                    if (playerView.isConnected()) {
                        playerView.show();
                    } else {
                        playerView.connect();
                    }
                    break;
                default:
                    //case "vine","youtube","instagram".
                    if (TextUtils.isEmpty(postsItem.getPermalink_url())) {
                        break;
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(postsItem.getPermalink_url()));
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                    }
                    break;
            }
        } else if (v.getId() == R.id.post_download) {
            SystemDownload.downloadVideo(context, postsItem.getVideo_url());
            Toast.makeText(context, R.string.download_start, Toast.LENGTH_SHORT).show();
        } else {
            super.onClick(v);
        }
    }
}
