package com.nutrition.express.util;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by huang on 2/14/17.
 */

public class SwipeGestureDetector {
    public static final int Direction_top_bottom = 1;
    public static final int Direction_bottom_top = 2;
    public static final int Direction_left_right = 4;
    public static final int Direction_right_left = 8;

    private OnSwipeGestureListener listener;
    private int direction;
    private int touchSlop;
    private float initialMotionX, initialMotionY;
    private float lastMotionX, lastMotionY;
    private boolean isBeingDragged;

    public SwipeGestureDetector(Context context, OnSwipeGestureListener listener) {
        this.listener = listener;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        float x = event.getRawX();
        float y = event.getRawY();

//        Log.d("onTouchEvent", "action-" + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialMotionX = lastMotionX = x;
                initialMotionY = lastMotionY = y;
                Log.d("onTouchEvent", "init motion:" + lastMotionX + "-" + lastMotionY);
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = x - lastMotionX;
                final float deltaY = y - lastMotionY;
                Log.d("onTouchEvent", "move motion x:" + x + "-" + y);
                if (!isBeingDragged) {
                    final float xDiff = Math.abs(deltaX);
                    final float yDiff = Math.abs(deltaY);
                    if (xDiff > touchSlop && xDiff > yDiff) {
                        isBeingDragged = true;
                        //direction horizon
                        direction = Direction_left_right;
                    } else if (yDiff > touchSlop && yDiff > xDiff) {
                        isBeingDragged = true;
                        //direction vertical
                        direction = Direction_top_bottom;
                    }
                }
                if (isBeingDragged) {
                    if (direction == Direction_left_right) {
                        listener.onSwipeLeftRight(deltaX);
                        lastMotionX = x;
                    } else if (direction == Direction_top_bottom) {
                        listener.onSwipeTopBottom(deltaY);
                        lastMotionY = y;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                reset();
                break;
            case MotionEvent.ACTION_CANCEL:
                reset();
                break;
        }
        return true;
    }

    private void reset() {
        isBeingDragged = false;
    }

    public interface OnSwipeGestureListener {
        void onSwipeTopBottom(float deltaY);
        void onSwipeLeftRight(float deltaX);
    }

}
