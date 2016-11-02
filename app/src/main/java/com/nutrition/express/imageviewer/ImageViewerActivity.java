package com.nutrition.express.imageviewer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.nutrition.express.R;
import com.nutrition.express.imageviewer.zoomable.ZoomableDraweeView;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by huang on 1/21/16.
 */
public class ImageViewerActivity extends AppCompatActivity {
    private LinearLayout indicator;
    private ImageView mImageViews[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (LinearLayout) findViewById(R.id.indicator_container);
        int selectedIndex = getIntent().getIntExtra("selected_index", 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra("image_urls");
        if (urls != null) {
            viewPager.setAdapter(new ViewImageAdapter(urls));
            if (urls.size() > 1) {
                viewPager.addOnPageChangeListener(pageChangeListener);
                setIndicator(urls.size());
                if (selectedIndex < urls.size()) {
                    viewPager.setCurrentItem(selectedIndex);
                }
            }
        } else {
            Toast.makeText(this, "找不到图片", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }


    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (ImageView imageView : mImageViews) {
                imageView.setImageResource(R.mipmap.radiobutton_default);
            }
            mImageViews[position].setImageResource(R.mipmap.radiobutton_select);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setIndicator(int imageCount) {
        indicator.removeAllViews();
        ImageView mImageView;
        float mScale = getResources().getDisplayMetrics().density;
        // 图片广告数量
         mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(this);
            // int imageParams = (int) (mScale * 10 + 0.5f);// XP与DP转换，适应不同分辨率
            int imagePadding = (int) (mScale * 2 + 0.5f);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mImageView.setPadding(imagePadding, imagePadding, imagePadding,
                    imagePadding);
            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setImageResource(R.mipmap.radiobutton_select);
            } else {
                mImageViews[i].setImageResource(R.mipmap.radiobutton_default);
            }
            if (imageCount > 1) {
                indicator.addView(mImageViews[i]);
            }
        }
    }

    private class ViewImageAdapter extends PagerAdapter implements View.OnClickListener {
        private ArrayList<String> urls;
        private LinkedList<ZoomableDraweeView> viewCache = new LinkedList<>();

        public ViewImageAdapter(ArrayList<String> urls) {
            this.urls = urls;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomableDraweeView draweeView;
            if (viewCache.size() == 0) {
                draweeView = new ZoomableDraweeView(ImageViewerActivity.this);
                draweeView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                        .setPlaceholderImage(R.color.loading_color)
                        .build();
                draweeView.setHierarchy(hierarchy);
            } else {
                draweeView = viewCache.removeFirst();
            }
            String url = urls.get(position);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(url == null ? Uri.EMPTY : Uri.parse(url))
                    .build();
            draweeView.setController(controller);
            draweeView.setOnClickListener(this);

            container.addView(draweeView);
            return draweeView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            viewCache.add((ZoomableDraweeView) object);
        }

        @Override
        public void onClick(View v) {
            ImageViewerActivity.this.finish();
        }
    }
}
