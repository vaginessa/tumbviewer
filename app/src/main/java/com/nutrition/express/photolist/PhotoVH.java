package com.nutrition.express.photolist;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.nutrition.express.util.Utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 10/26/16.
 */

public class PhotoVH extends CommonViewHolder<PostsItem> implements View.OnClickListener {
    private TextView nameTV, timeTV, captionTV;
    private FlexboxLayout contentLayout;
    private LinearLayout trailLayout;
    private ArrayList<SimpleDraweeView> cacheView;
    private int dividerWidth;
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private ArrayList<String> photos = new ArrayList<>();

    public PhotoVH(View itemView) {
        super(itemView);
        nameTV = (TextView) itemView.findViewById(R.id.blog_name);
        timeTV = (TextView) itemView.findViewById(R.id.blog_time);
        captionTV = (TextView) itemView.findViewById(R.id.blog_caption);
        contentLayout = (FlexboxLayout) itemView.findViewById(R.id.photo_content);
        trailLayout = (LinearLayout) itemView.findViewById(R.id.photo_trail);
        cacheView = new ArrayList<>();
        dividerWidth = (int) Utils.dp2Pixels(itemView.getContext(), 4);
    }

    @Override
    public void bindView(PostsItem postsItem) {
        nameTV.setText(postsItem.getBlog_name());
        timeTV.setText(DateUtils.getRelativeTimeSpanString(postsItem.getTimestamp() * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        captionTV.setText(Html.fromHtml(postsItem.getCaption()));
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
                //以第一个大小为准
                if (index >= size) {
                    return;
                }
                info = postsItem.getPhotos().get(index).getOriginal_size();
                w = calWidth(count);
                h = w * info.getHeight() / info.getWidth();
                Log.d(TAG, "bindView: " + w);
                for (int j = 0; j < count; j++) {
                    if (index >= size) {
                        return;
                    }
                    addSimpleDraweeView(cacheView.get(index), w, h, calWidth(count));
                    setUri(cacheView.get(index),
                            postsItem.getPhotos().get(index).getOriginal_size().getUrl());
                    index++;
                }
            }
        } else {
            PhotoItem.PhotoInfo info;
            for (int i = 0; i < size; i++) {
                info = postsItem.getPhotos().get(i).getOriginal_size();
                addSimpleDraweeView(cacheView.get(i), info.getWidth(), info.getHeight(),
                        ExpressApplication.width);
                setUri(cacheView.get(i), info.getUrl());
            }
        }
    }

    @Override
    public void onClick(View v) {
        Integer tag = (Integer) v.getTag();
        Intent intent = new Intent(itemView.getContext(), ImageViewerActivity.class);
        intent.putExtra("selected_index", tag.intValue());
        intent.putStringArrayListExtra("image_urls", photos);
        itemView.getContext().startActivity(intent);
    }

    private void createPhotoView(int count) {
        while (count > cacheView.size()) {
            cacheView.add(createSimpleDraweeView());
        }
    }

    private int calWidth(int count) {
        return (ExpressApplication.width - (count -1) * dividerWidth) / count;
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

    private void addSimpleDraweeView(SimpleDraweeView view, int w, int h, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(w, h);
        }
        params.width = width;
        params.height = params.width * h / w;
        view.setLayoutParams(params);
        contentLayout.addView(view);
    }

    private void setUri(SimpleDraweeView view, String url) {
        Uri uri = url == null ? Uri.EMPTY : Uri.parse(url);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(view.getController())
                .setTapToRetryEnabled(true)
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .build();
        view.setController(controller);
    }

}
