package com.nutrition.express.imageviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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
import com.nutrition.express.util.FileUtils;
import com.nutrition.express.util.FrescoUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by huang on 1/21/16.
 */
public class ImageViewerActivity extends AppCompatActivity {
    private final String ACTION = "SAVE_IMAGE";
    private ViewPager viewPager;
    private LinearLayout indicator;
    private ImageView mImageViews[];
    private MenuItem saveItem;
    private List<Uri> photoUris;
    private int savedCount, failureCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (LinearLayout) findViewById(R.id.indicator_container);
        int selectedIndex = getIntent().getIntExtra("selected_index", 0);
        List<String> photoUrls = getIntent().getStringArrayListExtra("image_urls");
        convert2Uri(photoUrls);
        if (photoUris != null) {
            viewPager.setAdapter(new ViewImageAdapter(photoUris));
            if (photoUris.size() > 1) {
                viewPager.addOnPageChangeListener(pageChangeListener);
                setIndicator(photoUris.size());
                if (selectedIndex < photoUris.size()) {
                    viewPager.setCurrentItem(selectedIndex);
                }
            }
        } else {
            Toast.makeText(this, R.string.pic_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(ACTION));
    }

    private void convert2Uri(List<String> urls) {
        photoUris = new ArrayList<>(urls.size());
        for (String url : urls) {
            if (!TextUtils.isEmpty(url)) {
                photoUris.add(Uri.parse(url));
            }
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
            if (FileUtils.imageSaved(photoUris.get(position))) {
                saveItem.setTitle(R.string.pic_saved);
            } else {
                saveItem.setTitle(R.string.pic_save);
            }
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
        private List<Uri> uris;
        private LinkedList<ZoomableDraweeView> viewCache = new LinkedList<>();

        public ViewImageAdapter(List<Uri> uris) {
            this.uris = uris;
        }

        @Override
        public int getCount() {
            return uris.size();
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
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(draweeView.getController())
                    .setAutoPlayAnimations(true)
                    .setUri(uris.get(position))
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                savedCount++;
                Uri uri = intent.getParcelableExtra("uri");
                if (photoUris.get(viewPager.getCurrentItem()).equals(uri)) {
                    saveItem.setTitle(R.string.pic_saved);
                }
            } else {
                failureCount++;
            }
            if (savedCount + failureCount == photoUris.size()) {
                if (failureCount > 0) {
                    Toast.makeText(ImageViewerActivity.this, R.string.pic_saved_failure,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageViewerActivity.this, R.string.pic_saved,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        saveItem = menu.findItem(R.id.save);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                FrescoUtils.save(photoUris.get(viewPager.getCurrentItem()), ACTION);
                break;
            case R.id.save_all:
                FrescoUtils.saveAll(photoUris, ACTION);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
