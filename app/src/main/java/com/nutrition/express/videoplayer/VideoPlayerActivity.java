package com.nutrition.express.videoplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nutrition.express.R;

/**
 * Created by huang on 11/9/16.
 */

public class VideoPlayerActivity extends AppCompatActivity implements ExoPlayer.EventListener {
    private Timeline.Window window;
    private Handler mainHandler;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private Uri uri;
    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    private int playerWindow;
    private long playerPosition;
    private boolean isTimelineStatic;
    private boolean autoPlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uri = intent.getParcelableExtra("uri");
        playerPosition = intent.getLongExtra("position", C.TIME_UNSET);
        playerWindow = intent.getIntExtra("windowIndex", 0);
        if (playerPosition != C.TIME_UNSET) {
            isTimelineStatic = true;
            autoPlay = true;
        }
        boolean rotation = intent.getBooleanExtra("rotation", false);
        if (rotation) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
        setContentView(R.layout.activity_video_player);
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, BANDWIDTH_METER,
                new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "Tumblr"), BANDWIDTH_METER));
        window = new Timeline.Window();
        mainHandler = new Handler();
        playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initPlayer() {
        if (player == null) {
            TrackSelection.Factory factory = new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
            TrackSelector selector = new DefaultTrackSelector(factory);

            player = ExoPlayerFactory.newSimpleInstance(this, selector, new DefaultLoadControl());
            player.addListener(this);
            playerView.setPlayer(player);
            if (isTimelineStatic) {
                if (playerPosition == C.TIME_UNSET) {
                    player.seekToDefaultPosition(playerWindow);
                } else {
                    player.seekTo(playerWindow, playerPosition);
                }
            }
            prepare(uri);
        }
    }

    private void prepare(Uri uri) {
        MediaSource source = new ExtractorMediaSource(uri,
                mediaDataSourceFactory, new DefaultExtractorsFactory(), mainHandler, null);
        player.prepare(source, !isTimelineStatic, !isTimelineStatic);
        if (autoPlay) {
            player.setPlayWhenReady(true);
            autoPlay = false;
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playerWindow = player.getCurrentWindowIndex();
            playerPosition = C.TIME_UNSET;
            Timeline timeline = player.getCurrentTimeline();
            if (timeline != null && timeline.getWindow(playerWindow, window).isSeekable) {
                playerPosition = player.getCurrentPosition();
            }
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        isTimelineStatic = timeline != null && timeline.getWindowCount() > 0 &&
                !timeline.getWindow(timeline.getWindowCount() - 1, window).isDynamic;
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
}
