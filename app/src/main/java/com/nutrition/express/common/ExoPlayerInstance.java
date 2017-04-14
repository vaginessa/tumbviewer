package com.nutrition.express.common;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nutrition.express.application.ExpressApplication;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

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

    private AudioManager am;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;

    private OnDisconnectListener onDisconnectListener;

    private boolean fullScreenMode = false;

    private static class Holder {
        private static ExoPlayerInstance holder = new ExoPlayerInstance();
    }

    public static ExoPlayerInstance getInstance() {
        return Holder.holder;
    }

    private ExoPlayerInstance() {
        this.context = ExpressApplication.getApplication();
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        mainHandler = new Handler();
        defaultExtractorsFactory = new DefaultExtractorsFactory();
        mediaDataSourceFactory = new DefaultDataSourceFactory(context, defaultBandwidthMeter,
                new OkHttpDataSourceFactory(
                        new OkHttpClient(), Util.getUserAgent(context, "Tumbviewer"),
                        defaultBandwidthMeter, CacheControl.FORCE_NETWORK));
        TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
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

    public void prepare(Uri uri, OnDisconnectListener onDisconnectListener) {
        if (player == null) {
            return;
        }
        requestAudioFocus();
        MediaSource source = new ExtractorMediaSource(uri,
                mediaDataSourceFactory, defaultExtractorsFactory, mainHandler, null);
        player.prepare(source);
        player.setPlayWhenReady(true);
        this.onDisconnectListener = onDisconnectListener;
    }

    void disconnectPrevious() {
        if (this.onDisconnectListener != null) {
            this.onDisconnectListener.onDisconnectListener();
            this.onDisconnectListener = null;
        }
    }

    public void releasePlayer() {
        if (player != null && !fullScreenMode) {
            disconnectPrevious();
            abandonAudioFocus();
            player.release();
            player = null;
        }
        fullScreenMode = false;
    }

    /***
     * if the player was released, this resumePlayer() method do the re-init job.
     */
    public void resumePlayer() {
        if (player == null) {
            initPlayer();
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    void stopPlayer() {
        if (player != null) {
            player.stop();
        }
    }

    private void requestAudioFocus() {
        if (am == null) {
            am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if (afChangeListener == null) {
            afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        pausePlayer();
                    }
                }
            };
        }
        am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    void abandonAudioFocus() {
        if (am != null) {
            am.abandonAudioFocus(afChangeListener);
        }
    }

    void startFullScreenMode() {
        fullScreenMode = true;
    }

    interface OnDisconnectListener {
        void onDisconnectListener();
    }

}
