package com.egrobots.shagarah.managers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.egrobots.shagarah.R;

import java.io.File;
import java.util.UUID;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.Util;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;

import static androidx.media3.common.Player.STATE_ENDED;
import static androidx.media3.common.Player.STATE_READY;

public class ExoPlayerVideoManager {

    private ExoPlayer exoPlayer;
    private PlayerView playerView;
    private boolean playWhenReady = true;
    private int currentItem = 0;
    private long playbackPosition = 0L;
    private boolean isRepeatEnabled = false;
    private VideoManagerCallback videoManagerCallback;
    private Context context;
    private SimpleCache simpleCache;

    public void setExoPlayerCallback(VideoManagerCallback videoManagerCallback) {
        this.videoManagerCallback = videoManagerCallback;
    }

    public void stopPlayer() {
        if (this.exoPlayer != null) {
//            simpleCache.release();
            this.exoPlayer.stop();
            this.exoPlayer.seekTo(0, 0);
        }
    }

    public void pausePlayer(boolean seekToStart) {
        if (this.exoPlayer != null) {
            this.exoPlayer.pause();
            if (seekToStart) this.exoPlayer.seekTo(0,0);
        }
    }

    public void initializeAudioExoPlayer(Context context, String audioUri, boolean playWhenReady) {
        this.context = context;
        exoPlayer = new ExoPlayer.Builder(context).build();
//        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
//        simpleCache = new SimpleCache(new File(context.getCacheDir(), UUID.randomUUID().toString())
//                , evictor
//                , new StandaloneDatabaseProvider(context)
//        );
//        CacheDataSource.Factory cacheDataSource = new CacheDataSource.Factory();
//        cacheDataSource.setCache(simpleCache);
//        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)));
//        cacheDataSource.setUpstreamDataSourceFactory(defaultDataSourceFactory);
//        MediaSource mediaSource = null;
//        try {
            MediaItem mediaItem = MediaItem.fromUri(audioUri);
//            mediaSource = new ProgressiveMediaSource.Factory(cacheDataSource)
//                    .createMediaSource(mediaItem);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentItem, playbackPosition);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.prepare();
        if (playWhenReady) {
            exoPlayer.play();
        } else {
            exoPlayer.pause();
        }
    }

    public void initializeExoPlayer(Context context, String videoUri) {
        this.context = context;
        exoPlayer = new ExoPlayer.Builder(context).build();
        LeastRecentlyUsedCacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
        simpleCache = new SimpleCache(new File(context.getCacheDir(), UUID.randomUUID().toString())
                , evictor
                , new StandaloneDatabaseProvider(context)
        );
        CacheDataSource.Factory cacheDataSource = new CacheDataSource.Factory();
        cacheDataSource.setCache(simpleCache);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name)));
        cacheDataSource.setUpstreamDataSourceFactory(defaultDataSourceFactory);
        MediaSource mediaSource = null;
        try {
            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            mediaSource = new ProgressiveMediaSource.Factory(cacheDataSource)
                    .createMediaSource(mediaItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        exoPlayer.setMediaSource(mediaSource);
//        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentItem, playbackPosition);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.prepare();
        exoPlayer.pause();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case STATE_READY:
//                        videoManagerCallback.onPrepare();
                        Log.i("VIDEO READY", "onPlaybackStateChanged: STATE_READY");
                        break;
                    case STATE_ENDED:
                        Log.i("VIDEO ENDED", "onPlaybackStateChanged: STATE_ENDED");
                        break;
                    case Player.STATE_BUFFERING:
                        Log.i("VIDEO BUFFERING", "onPlaybackStateChanged: STATE_BUFFERING");
                        break;
                    case Player.STATE_IDLE:
                        Log.i("VIDEO IDLE", "onPlaybackStateChanged: STATE_IDLE");
                        break;
                }
            }

            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                int ratio = videoSize.width / videoSize.height;
                //vertical video
                if (ratio < 1) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                }
                //horizontal video
                else {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                }
            }
        });
    }

    public void initializePlayer(PlayerView playerView) {
        playerView.setPlayer(exoPlayer);
        playerView.hideController();
        this.playerView = playerView;
    }

    public void setCapturedImageToPlayer(Drawable image) {
        playerView.setDefaultArtwork(image);
    }

    public PlayerView getPlayerView() {
        return playerView;
    }

    public boolean isPlaying() {
        if (exoPlayer != null) {
            return exoPlayer.isPlaying();
        } else {
            return false;
        }
    }

    public void play() {
        if (exoPlayer != null) {
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }

    public void resumePlaying() {
        if (exoPlayer != null && !isPlaying()) {
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.getPlaybackState();
        }
    }

    public void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentItem = exoPlayer.getCurrentMediaItemIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
        }
        exoPlayer = null;
    }

    public interface VideoManagerCallback {
        void onPrepare();

        void onError(String msg);

        void onEnd();
    }
}
