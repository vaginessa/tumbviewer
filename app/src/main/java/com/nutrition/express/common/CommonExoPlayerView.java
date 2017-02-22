package com.nutrition.express.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.nutrition.express.R;
import com.nutrition.express.download.LocalVideo;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by huang on 2/20/17.
 */

public class CommonExoPlayerView extends FrameLayout {

    public static final int DEFAULT_SHOW_TIMEOUT_MS = 3000;

    private static final int PROGRESS_BAR_MAX = 1000;

    private ComponentListener componentListener;
    private TextureView videoView;
    private ImageView playView;
    private SimpleDraweeView thumbnailView;
    private LinearLayout controlLayout;
    private TextView time;
    private TextView timeCurrent;
    private SeekBar progressBar;


    private StringBuilder formatBuilder;
    private Formatter formatter;
    private Timeline.Window currentWindow;

    private ExoPlayerInstance playerInstance;
    private SimpleExoPlayer player;
    private Uri uri;

    private boolean isAttachedToWindow;
    private boolean dragging;
    private boolean isConnected;
    private int showTimeoutMs;
    private long hideAtMs;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            Log.d("run", "update progress action");
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
            Log.d("run", "hide action");
        }
    };


    public CommonExoPlayerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CommonExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public CommonExoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;

        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        currentWindow = new Timeline.Window();
        componentListener = new ComponentListener();

        LayoutInflater.from(context).inflate(R.layout.item_video_control, this);
        controlLayout = (LinearLayout) findViewById(R.id.video_control_layout);
        time = (TextView) findViewById(R.id.time);
        timeCurrent = (TextView) findViewById(R.id.time_current);
        progressBar = (SeekBar) findViewById(R.id.video_controller_progress);
        progressBar.setOnSeekBarChangeListener(componentListener);
        progressBar.setMax(PROGRESS_BAR_MAX);

        videoView = new TextureView(context);
        FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(videoParams);

        thumbnailView = new SimpleDraweeView(context);
        FrameLayout.LayoutParams thumbParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources())
                .setPlaceholderImage(R.color.loading_color)
                .build();
        thumbnailView.setHierarchy(hierarchy);
        thumbnailView.setLayoutParams(thumbParams);

        playView = new ImageView(context);
        FrameLayout.LayoutParams playParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playParams.gravity = Gravity.CENTER;
        playView.setLayoutParams(playParams);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                context.getResources().getDisplayMetrics());
        playView.setPadding(padding, padding, padding, padding);
        playView.setOnClickListener(componentListener);

//        setOnClickListener(componentListener);

        addView(videoView, 0);
        addView(thumbnailView, 1);
        addView(playView, 2);
    }

    /**
     * should re-init every state
     * @param video
     */
    public void bindLocalVideo(LocalVideo video) {
        this.uri = video.getUri();
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(video.getWidth(), video.getHeight());
        }
        params.width = video.getWidth();
        params.height = video.getHeight();
        setLayoutParams(params);
        thumbnailView.setImageURI(video.getUri());
        thumbnailView.setVisibility(VISIBLE);
        hide();
        disconnect();
    }

    public void setPlayerInstance(ExoPlayerInstance playerInstance) {
        this.playerInstance = playerInstance;
    }

    public void connect() {
        player = playerInstance.getPlayer();
        player.setVideoTextureView(videoView);
        player.addListener(componentListener);
        player.setVideoListener(componentListener);
        playerInstance.prepare(uri);
        isConnected = true;
    }

    public void disconnect() {
        if (player != null) {
            if (player.getPlayWhenReady()) { //pause
                player.setPlayWhenReady(false);
            }
            player.removeListener(componentListener);
            player.setVideoListener(null);
            player = null;
        }
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Shows the controller
     */
    public void show() {
        if (!isControllerVisible()) {
            controlLayout.setVisibility(VISIBLE);
            playView.setVisibility(VISIBLE);
            updateAll();
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        hideAfterTimeout();
    }

    /**
     * Hides the controller.
     */
    public void hide() {
        if (isControllerVisible()) {
            controlLayout.setVisibility(GONE);
            playView.setVisibility(GONE);
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    /**
     *
     * @return true if controller is visible.
     */
    public boolean isControllerVisible() {
        return controlLayout.getVisibility() == VISIBLE;
    }

    private void hideAfterTimeout() {
        removeCallbacks(hideAction);
        if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow) {
                postDelayed(hideAction, showTimeoutMs);
            }
        } else {
            hideAtMs = C.TIME_UNSET;
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateProgress();
    }

    private void updatePlayPauseButton() {
        if (!isControllerVisible() || !isAttachedToWindow) {
            return;
        }
        boolean playing = player != null && player.getPlayWhenReady()
                && player.getPlaybackState() == ExoPlayer.STATE_READY;
        String contentDescription = getResources().getString(
                playing ? R.string.exo_controls_pause_description : R.string.exo_controls_play_description);
        playView.setContentDescription(contentDescription);
        playView.setImageResource(
                playing ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play);
    }

    private void updateProgress() {
        if (!isControllerVisible() || !isAttachedToWindow) {
            return;
        }
        long duration = player == null ? 0 : player.getDuration();
        long position = player == null ? 0 : player.getCurrentPosition();
        time.setText(stringForTime(duration));
        if (!dragging) {
            timeCurrent.setText(stringForTime(position));
        }
        if (!dragging) {
            progressBar.setProgress(progressBarValue(position));
        }
        long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
        progressBar.setSecondaryProgress(progressBarValue(bufferedPosition));
        // Remove scheduled updates.
        removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private String stringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    private int progressBarValue(long position) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET || duration == 0 ? 0
                : (int) ((position * PROGRESS_BAR_MAX) / duration);
    }

    private long positionValue(int progress) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET ? 0 : ((duration * progress) / PROGRESS_BAR_MAX);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        Log.d("onAttachedToWindow", "true");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        thumbnailView.setVisibility(VISIBLE);
        hide();
        disconnect();
        Log.d("onDetachedFromWindow", "true");
    }

    private final class ComponentListener implements ExoPlayer.EventListener,
            SimpleExoPlayer.VideoListener, SeekBar.OnSeekBarChangeListener, OnClickListener {

        //Override SimpleExoPlayer.VideoListener
        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame() {
            thumbnailView.setVisibility(GONE);
        }

        //Override OnClickListener
        @Override
        public void onClick(View v) {
            if (v == playView && player != null) {
                if (player.getPlaybackState() == ExoPlayer.STATE_ENDED) {
                    player.seekTo(0);
                    player.setPlayWhenReady(true);
                } else {
                    player.setPlayWhenReady(!player.getPlayWhenReady());
                }
            }
        }

        //Override SeekBar.OnSeekBarChangeListener
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                timeCurrent.setText(stringForTime(positionValue(progress)));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeCallbacks(hideAction);
            dragging = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;
            player.seekTo(positionValue(seekBar.getProgress()));
            hideAfterTimeout();
        }

        //Override ExoPlayer.EventListener
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.d("onPlayerStateChanged", playWhenReady + "-" + playbackState);
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }
    }

}
