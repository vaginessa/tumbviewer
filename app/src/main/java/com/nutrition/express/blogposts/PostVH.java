package com.nutrition.express.blogposts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.nutrition.express.R;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.imageviewer.ImageViewerActivity;
import com.nutrition.express.model.rest.bean.PhotoItem;
import com.nutrition.express.model.rest.bean.PostsItem;
import com.nutrition.express.model.rest.bean.TrailItem;
import com.nutrition.express.useraction.LikePostContract;
import com.nutrition.express.useraction.LikePostPresenter;
import com.nutrition.express.util.FrescoUtils;
import com.nutrition.express.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 11/7/16.
 */

public class PostVH extends CommonViewHolder<PostsItem>
        implements View.OnClickListener, LikePostContract.View {
    private Context context;
    private SimpleDraweeView avatarView;
    private TextView nameView, timeView, sourceView, noteCountView;
    private ImageView likeView;
    private FlexboxLayout contentLayout;
    private LinearLayout trailLayout;
    private ArrayList<SimpleDraweeView> contentViewCache = new ArrayList<>();
    private ArrayList<RelativeLayout> trailViewCache = new ArrayList<>();
    private int dividerWidth;
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private ArrayList<String> photos = new ArrayList<>();
    private PostsItem postsItem;
    private LikePostPresenter presenter;

    public PostVH(View itemView) {
        super(itemView);
        context = itemView.getContext();
        avatarView = (SimpleDraweeView) itemView.findViewById(R.id.post_avatar);
        nameView = (TextView) itemView.findViewById(R.id.post_name);
        itemView.findViewById(R.id.post_header).setOnClickListener(this);
        timeView = (TextView) itemView.findViewById(R.id.post_time);
        sourceView = (TextView) itemView.findViewById(R.id.post_source);
        sourceView.setOnClickListener(this);
        contentLayout = (FlexboxLayout) itemView.findViewById(R.id.post_content);
        trailLayout = (LinearLayout) itemView.findViewById(R.id.post_trail);
        noteCountView = (TextView) itemView.findViewById(R.id.note_count);
        likeView = (ImageView) itemView.findViewById(R.id.post_like);
        likeView.setOnClickListener(this);
        dividerWidth = (int) Utils.dp2Pixels(context, 4);
    }

    @Override
    public void bindView(PostsItem postsItem) {
        this.postsItem = postsItem;
        FrescoUtils.setTumblrAvatarUri(avatarView, postsItem.getBlog_name(), 128);
        nameView.setText(postsItem.getBlog_name());
        timeView.setText(DateUtils.getRelativeTimeSpanString(postsItem.getTimestamp() * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        if (TextUtils.isEmpty(postsItem.getSource_title())) {
            sourceView.setVisibility(View.GONE);
        } else {
            sourceView.setText(context.getString(R.string.source_title, postsItem.getSource_title()));
            sourceView.setVisibility(View.VISIBLE);
        }
        switch (postsItem.getType()) {
            case "video":
                setVideoContent();
                noteCountView.setText(context.getString(
                        R.string.note_count_description,
                        postsItem.getNote_count(),
                        formatTime(postsItem.getDuration())));
                break;
            case "photo":
                setPhotoContent();
                noteCountView.setText(context.getString(
                        R.string.note_count,
                        postsItem.getNote_count()));
                break;
        }
        setTrailContent();
        if (postsItem.isLiked()) {
            likeView.setSelected(true);
        } else {
            likeView.setSelected(false);
        }
    }

    private void setPhotoContent() {
        contentLayout.removeAllViews();
        photos.clear();
        for (PhotoItem item: postsItem.getPhotos()) {
            photos.add(item.getOriginal_size().getUrl());
        }
        int size = postsItem.getPhotos().size();
        createPhotoView(size);
        String layout = postsItem.getPhotoset_layout();
        if (layout != null && TextUtils.isDigitsOnly(layout)) {
            Log.d(TAG, "bindView: " + layout);
            int index = 0, count, w, h;
            PhotoItem.PhotoInfo info;
            for (int i = 0; i < layout.length(); i++) {
                count = layout.charAt(i) - '0';
                if (index >= size) {
                    return;
                }
                //以第一个大小为准
                info = postsItem.getPhotos().get(index).getOriginal_size();
                w = calWidth(count);
                h = w * info.getHeight() / info.getWidth();
                Log.d(TAG, "bindView: " + w);
                for (int j = 0; j < count; j++) {
                    if (index >= size) {
                        return;
                    }
                    addSimpleDraweeView(contentViewCache.get(index), w, h);
                    setUri(contentViewCache.get(index),
                            postsItem.getPhotos().get(index).getOriginal_size().getUrl());
                    index++;
                }
            }
        } else {
            PhotoItem.PhotoInfo info;
            int w, h;
            for (int i = 0; i < size; i++) {
                info = postsItem.getPhotos().get(i).getOriginal_size();
                w = calWidth(1);
                h = w * info.getHeight() / info.getWidth();
                addSimpleDraweeView(contentViewCache.get(i), w, h);
                setUri(contentViewCache.get(i), info.getUrl());
            }
        }

    }

    private void setVideoContent() {
        contentLayout.removeAllViews();
        createPhotoView(1);
        SimpleDraweeView draweeView = contentViewCache.get(0);
        int w = calWidth(1);
        int h;
        if (postsItem.getThumbnail_width() > 0) {
            h = w * postsItem.getThumbnail_height() / postsItem.getThumbnail_width();
        } else {
            h = w / 2;
        }
        addSimpleDraweeView(draweeView, w, h);
        setUri(draweeView, postsItem.getThumbnail_url());
    }

    private void setTrailContent() {
        List<TrailItem> trails = postsItem.getTrail();
        if (trails != null && trails.size() > 0) {
            trailLayout.removeAllViews();
            createTrailView(trails.size());
            for (int i = 0; i < trails.size(); i++) {
                RelativeLayout layout = trailViewCache.get(i);
                SimpleDraweeView avatar = (SimpleDraweeView) layout.findViewById(R.id.trail_avatar);
                TextView name = (TextView) layout.findViewById(R.id.trail_name);
                TextView content = (TextView) layout.findViewById(R.id.trail_content);
                FrescoUtils.setTumblrAvatarUri(avatar, trails.get(i).getBlog().getName(), 128);
                name.setText(trails.get(i).getBlog().getName());
                content.setText(fromHtlmCompat(trails.get(i).getContent_raw()));
                layout.setTag(trails.get(i).getBlog().getName());
                trailLayout.addView(layout);
            }
            trailLayout.setVisibility(View.VISIBLE);
        } else {
            trailLayout.setVisibility(View.GONE);
        }
    }

    private Spanned fromHtlmCompat(String html) {
        if (html == null) {
            html = "...";
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT); // for 24 api and more
        } else {
            return Html.fromHtml(html); // or for older api
        }
    }

    private void createTrailView(int count) {
        while (count > trailViewCache.size()) {
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
                    .inflate(R.layout.item_trail, null);
            layout.setOnClickListener(this);
            trailViewCache.add(layout);
        }
    }

    private void createPhotoView(int count) {
        while (count > contentViewCache.size()) {
            contentViewCache.add(createSimpleDraweeView());
        }
    }

    private SimpleDraweeView createSimpleDraweeView() {
        SimpleDraweeView view = new SimpleDraweeView(itemView.getContext());
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(itemView.getContext().getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setPlaceholderImage(R.color.loading_color)
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(R.mipmap.ic_failed)
                .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER)
                .build();
        view.setHierarchy(hierarchy);
        view.setTag(atomicInteger.getAndIncrement());
        view.setOnClickListener(this);
        return view;
    }

    private int calWidth(int count) {
        return (ExpressApplication.width - (count -1) * dividerWidth) / count;
    }

    private void addSimpleDraweeView(SimpleDraweeView view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
        }
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
        contentLayout.addView(view);
    }

    private void setUri(SimpleDraweeView view, String url) {
        Uri uri = url == null ? Uri.EMPTY : Uri.parse(url);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(view.getController())
//                .setTapToRetryEnabled(true)
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .build();
        view.setController(controller);
    }

    private String formatTime(String duration) {
        try {
            long dur = Long.valueOf(duration);
            return DateUtils.formatElapsedTime(dur);
        } catch (NumberFormatException e) {
            return duration;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post_header) {
            openBlog(postsItem.getBlog_name());
        } else if (v.getId() == R.id.post_source) {
            openBlog(postsItem.getSource_title());
        } else if (v.getId() == R.id.trail_layout) {
            openBlog((String) v.getTag());
        } else if (v.getId() == R.id.post_like) {
            if (presenter == null) {
                presenter = new LikePostPresenter(this);
            }
            if (likeView.isSelected()) {
                presenter.unlike(postsItem.getId(), postsItem.getReblog_key());
            } else {
                presenter.like(postsItem.getId(), postsItem.getReblog_key());
            }
        } else {
            if ("video".equals(postsItem.getType())) {
                openBottomSheet();
            } else {
                Integer tag = (Integer) v.getTag();
                Intent intent = new Intent(context, ImageViewerActivity.class);
                intent.putExtra("selected_index", tag.intValue());
                intent.putStringArrayListExtra("image_urls", photos);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void onLike() {
        likeView.setSelected(true);
        postsItem.setLiked(true);
    }

    @Override
    public void onLikeFailure() {
    }

    @Override
    public void onUnlike() {
        likeView.setSelected(false);
        postsItem.setLiked(false);
    }

    @Override
    public void onUnlikeFailure() {
    }

    private void openBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("image_url", postsItem.getThumbnail_url());
        bundle.putString("video_url", postsItem.getVideo_url());
        PostBottomSheet bottomSheet = new PostBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(((FragmentActivity) itemView.getContext()).getSupportFragmentManager(),
                bottomSheet.getTag());
    }

    private void openBlog(String blogName) {
        Intent intent = new Intent(itemView.getContext(), PostListActivity.class);
        intent.putExtra("blog_name", blogName);
        itemView.getContext().startActivity(intent);
    }

}
