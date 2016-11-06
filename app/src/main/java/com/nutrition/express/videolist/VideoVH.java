package com.nutrition.express.videolist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;


/**
 * Created by huang on 11/2/16.
 */

public class VideoVH extends CommonViewHolder<PostsItem> implements View.OnClickListener {
    private SimpleDraweeView draweeView;
    private TextView blogName, blogTime, blogCaption, noteCount;
    private ImageView likeIV;
    private String videoUrl, imageUrl;
    private int defaultWidth;

    public VideoVH(View itemView) {
        super(itemView);
        draweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpleDraweeView);
        if (draweeView != null) {
            draweeView.setOnClickListener(this);
        }
        blogName = (TextView) itemView.findViewById(R.id.blog_name);
        blogName.setOnClickListener(this);
        blogCaption = (TextView) itemView.findViewById(R.id.blog_caption);
        blogTime = (TextView) itemView.findViewById(R.id.blog_time);
        likeIV = (ImageView) itemView.findViewById(R.id.blog_like);
        noteCount = (TextView) itemView.findViewById(R.id.note_count);

        defaultWidth = ExpressApplication.width;
    }

    @Override
    public void bindView(PostsItem postsItem) {
        videoUrl = postsItem.getVideo_url();
        imageUrl = postsItem.getThumbnail_url();
        String url = postsItem.getThumbnail_url();
        ViewGroup.LayoutParams params = draweeView.getLayoutParams();
        params.width = defaultWidth;
        if (postsItem.getThumbnail_width() > 0 &&
                postsItem.getThumbnail_width() > postsItem.getThumbnail_height()) {
            params.height = params.width *
                    postsItem.getThumbnail_height() / postsItem.getThumbnail_width();
            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        } else {
            params.height = defaultWidth;
            draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        }
        draweeView.setLayoutParams(params);
        draweeView.setImageURI(url != null ? Uri.parse(url) : Uri.EMPTY);
        blogName.setText(postsItem.getBlog_name());
        if (TextUtils.isEmpty(postsItem.getCaption())) {
            blogCaption.setVisibility(View.VISIBLE);
            blogCaption.setText(Html.fromHtml(postsItem.getCaption()));
        } else {
            blogCaption.setVisibility(View.GONE);
        }
        blogTime.setText(DateUtils.getRelativeTimeSpanString(postsItem.getTimestamp() * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        if (postsItem.isLiked()) {
            likeIV.setSelected(true);
        } else {
            likeIV.setSelected(false);
        }
        noteCount.setText(itemView.getContext().getString(R.string.note_count_description,
                postsItem.getNote_count(), formatTime(postsItem.getDuration())));
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
        if (v.getId() == R.id.simpleDraweeView) {
            openBottomSheet();
        } else if (v.getId() == R.id.blog_name) {
            openBlog(blogName.getText().toString());
        }
    }

    private void openBottomSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("image_url", imageUrl);
        bundle.putString("video_url", videoUrl);
        VideoBottomSheet bottomSheet = new VideoBottomSheet();
        bottomSheet.setArguments(bundle);
        bottomSheet.show(((FragmentActivity) itemView.getContext()).getSupportFragmentManager(),
                bottomSheet.getTag());
    }

    private void openBlog(String blogName) {
        Intent intent = new Intent(itemView.getContext(), VideoListActivity.class);
        intent.putExtra("blog_name", blogName);
        itemView.getContext().startActivity(intent);
    }

}
