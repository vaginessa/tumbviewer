package com.nutrition.express.imageviewer;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeTransition;
import com.nutrition.express.R;
import com.nutrition.express.imageviewer.zoomable.ZoomableDraweeView;

/**
 * Created by huang on 2/22/17.
 */

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.FIT_CENTER));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.FIT_CENTER));
        }
        setContentView(R.layout.activity_photo_view);

        Uri uri = getIntent().getParcelableExtra("photo_source");
        String transitionName = getIntent().getStringExtra("transition_name");

        final ZoomableDraweeView draweeView = (ZoomableDraweeView) findViewById(R.id.photoView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            draweeView.setTransitionName(transitionName);
        }
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setPlaceholderImage(R.color.divider_color)
                .build();
        draweeView.setHierarchy(hierarchy);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(draweeView.getController())
                .setAutoPlayAnimations(true)
                .setUri(uri)
                .build();
        draweeView.setController(controller);
        draweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    draweeView.reset();
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });
    }
}
