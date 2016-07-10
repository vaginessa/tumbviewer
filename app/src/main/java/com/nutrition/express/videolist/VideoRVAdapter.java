package com.nutrition.express.videolist;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.application.MyApplication;
import com.nutrition.express.rest.bean.PostsItem;
import com.nutrition.express.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 4/24/16.
 */
public class VideoRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_NO_DATA = 0;
    private static final int TYPE_LOADING = 1;
    private static final int TYPE_POSTS = 2;

    private static final int LOADING = 10;
    private static final int LOADING_MORE = 11;
    private static final int LOADED_FINISHED = 12;
    private static final int LOADED_FAILED = 13;

    private int defaultWidth;
    private int count = 1;
    private int loadingState = LOADING;
    private List<PostsItem> list = new ArrayList<>();
    private Activity activity;
    private OnLoadListener loadListener;
    private View.OnClickListener onTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (loadingState == LOADED_FAILED) {
                loadListener.loadPostsAgain();
                loadingState = LOADING;
                notifyItemChanged(count - 1);
            }
        }
    };

    public VideoRVAdapter(Activity activity, OnLoadListener listener) {
        this.activity = activity;
        this.loadListener = listener;
        int pxOf16dp = (int) Utils.dp2Pixels(activity, 16);
        defaultWidth = MyApplication.width - 2 * pxOf16dp;
    }

    public void setPostsList(List<PostsItem> items, boolean loadedFinished) {
        int size = 0;
        if (items.size() > 0) {
            for (PostsItem item : items) {
                if (!TextUtils.isEmpty(item.getVideo_url())) {
                    list.add(item);
                    size++;
                }
            }
            count += size;
            notifyItemRangeInserted(count - size, size);
        } else {
            loadedFinished = true;
        }
        if (loadedFinished) {
            loadingState = LOADED_FINISHED;
            notifyItemChanged(count - 1);
        }
    }

    public void loadPostsFailed() {
        loadingState = LOADED_FAILED;
        notifyItemChanged(count - 1);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_POSTS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_video_1, parent, false);
                viewHolder = new VideoVH(view);
                break;
            case TYPE_NO_DATA:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_video_none, parent, false);
                viewHolder = new TextVH(view, onTextClick);
                break;
            case TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_video_loading, parent, false);
                viewHolder = new BaseVH(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_POSTS) {
            ((VideoVH) holder).bindView(list.get(position));
        } else if (type == TYPE_NO_DATA) {
            TextVH textVH = (TextVH) holder;
            if (loadingState == LOADED_FINISHED) {
                textVH.setText(activity.getString(R.string.no_video));
            } else {
                textVH.setText(activity.getString(R.string.load_failed));
            }
        } else if (type == TYPE_LOADING && count > 1) {
            loadListener.loadPostsNext();
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == count - 1) {
            if (loadingState == LOADING || loadingState == LOADING_MORE) {
                return TYPE_LOADING;
            } else {
                return TYPE_NO_DATA;
            }
        } else {
            return TYPE_POSTS;
        }
    }

    private class VideoVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SimpleDraweeView draweeView;
        private TextView blogName;
        private TextView noteCount;
        private String videoUrl;

        public VideoVH(View itemView) {
            super(itemView);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpleDraweeView);
            if (draweeView != null) {
                draweeView.setOnClickListener(this);
            }
            blogName = (TextView) itemView.findViewById(R.id.blog_name);
            blogName.setOnClickListener(this);
            noteCount = (TextView) itemView.findViewById(R.id.note_count);
        }

        public void bindView(PostsItem postsItem) {
            videoUrl = postsItem.getVideo_url();
            String url = postsItem.getThumbnail_url();
            ViewGroup.LayoutParams params = draweeView.getLayoutParams();
            params.width = defaultWidth;
            if (postsItem.getThumbnail_width() > 0 &&
                    postsItem.getThumbnail_width() > postsItem.getThumbnail_height()) {
                params.height = params.width *
                        postsItem.getThumbnail_height() / postsItem.getThumbnail_width();
            } else {
                params.height = defaultWidth;
            }
            draweeView.setLayoutParams(params);
            draweeView.setImageURI(url != null ? Uri.parse(url) : Uri.EMPTY);
            blogName.setText(postsItem.getBlog_name());
            noteCount.setText(postsItem.getNote_count() + "  热度");
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.simpleDraweeView) {
                openBottomSheet(videoUrl);
            } else if (v.getId() == R.id.blog_name) {
                loadListener.openBlog(blogName.getText().toString());
            }
        }

        private void openBottomSheet(String url) {
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            VideoBottomSheet bottomSheet = new VideoBottomSheet();
            bottomSheet.setArguments(bundle);
            bottomSheet.show(((AppCompatActivity) activity).getSupportFragmentManager(),
                    bottomSheet.getTag());
        }
    }

    private class BaseVH extends RecyclerView.ViewHolder {
        public BaseVH(View itemView) {
            super(itemView);
        }
    }

    private class TextVH extends BaseVH {
        private TextView textView;

        public TextVH(View itemView, View.OnClickListener listener) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.no_video);
            itemView.setOnClickListener(listener);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }

}
