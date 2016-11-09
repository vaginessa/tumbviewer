package com.nutrition.express.videoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nutrition.express.R;

/**
 * Created by huang on 11/9/16.
 */

public class VideoPlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        SimpleExoPlayerView playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "视频不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Uri uri = Uri.parse(url);
        init();
        playerView.setPlayer(player);
        prepare(uri);
    }

    private void init() {
        Handler handler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory factory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector selector = new DefaultTrackSelector(handler, factory);

        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, selector, loadControl);
    }

    private void prepare(Uri uri) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory factory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Tumblr"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource source = new ExtractorMediaSource(uri,
                factory, extractorsFactory, null, null);
        player.prepare(source);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }
}
