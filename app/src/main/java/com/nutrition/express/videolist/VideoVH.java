package com.nutrition.express.videolist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nutrition.express.R;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.model.rest.bean.PostsItem;
import com.nutrition.express.util.Utils;


/**
 * Created by huang on 11/2/16.
 */

public class VideoVH extends CommonViewHolder<PostsItem> implements View.OnClickListener {
    private SimpleDraweeView draweeView;
    private TextView blogName;
    private TextView noteCount;
    private String videoUrl;
    private int defaultWidth;

    public VideoVH(View itemView) {
        super(itemView);
        draweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpleDraweeView);
        if (draweeView != null) {
            draweeView.setOnClickListener(this);
        }
        blogName = (TextView) itemView.findViewById(R.id.blog_name);
        blogName.setOnClickListener(this);
        noteCount = (TextView) itemView.findViewById(R.id.note_count);

        int pxOf16dp = (int) Utils.dp2Pixels(itemView.getContext(), 16);
        defaultWidth = ExpressApplication.width - 2 * pxOf16dp;
    }

    @Override
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
            params.height = defaultWidth / 2;
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
            openBlog(blogName.getText().toString());
        }
    }

    private void openBottomSheet(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
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
