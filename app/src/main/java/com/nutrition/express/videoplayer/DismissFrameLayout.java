package com.nutrition.express.videoplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.nutrition.express.util.SwipeGestureDetector;

/**
 * Created by huang on 2/15/17.
 */

public class DismissFrameLayout extends FrameLayout {
    private SwipeGestureDetector swipeGestureDetector;

    public DismissFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public DismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public DismissFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        swipeGestureDetector = new SwipeGestureDetector(getContext(),
                new SwipeGestureDetector.OnSwipeGestureListener() {
                    @Override
                    public void onSwipeTopBottom(float deltaY) {
                        Log.d("onSwipeTopBottom", "" + deltaY);
                        ViewCompat.offsetTopAndBottom(DismissFrameLayout.this, (int) deltaY);
                    }

                    @Override
                    public void onSwipeLeftRight(float deltaX) {
                        Log.d("onSwipeLeftRight", "" + deltaX);
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeGestureDetector.onTouchEvent(event);
    }
}
