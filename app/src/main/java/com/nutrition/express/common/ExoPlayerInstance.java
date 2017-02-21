package com.nutrition.express.common;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by huang on 2/17/17.
 */

public class ExoPlayerInstance {
    private Context context;
    private final Handler mainHandler;
    private final DataSource.Factory mediaDataSourceFactory;
    private final DefaultExtractorsFactory defaultExtractorsFactory;
    private final TrackSelector trackSelector;
    private final DefaultLoadControl defaultLoadControl;

    private SimpleExoPlayer player;


    public ExoPlayerInstance(Context context) {
        this.context = context;
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        mainHandler = new Handler();
        defaultExtractorsFactory = new DefaultExtractorsFactory();
        mediaDataSourceFactory = new DefaultDataSourceFactory(context, defaultBandwidthMeter,
                new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "Tumblr"), defaultBandwidthMeter));
        TrackSelection.Factory factory = new AdaptiveVideoTrackSelection.Factory(defaultBandwidthMeter);
        trackSelector = new DefaultTrackSelector(factory);
        defaultLoadControl = new DefaultLoadControl();
    }

    private void initPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, defaultLoadControl);
    }

    public SimpleExoPlayer getPlayer() {
        if (player == null) {
            initPlayer();
        }
        return player;
    }

    public void prepare(Uri uri) {
        if (player == null) {
            initPlayer();
        }
        MediaSource source = new ExtractorMediaSource(uri,
                mediaDataSourceFactory, defaultExtractorsFactory, mainHandler, null);
        player.prepare(source);
        player.setPlayWhenReady(true);
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    public void stopPlayer() {
        if (player != null) {
            player.stop();
        }
    }

}
