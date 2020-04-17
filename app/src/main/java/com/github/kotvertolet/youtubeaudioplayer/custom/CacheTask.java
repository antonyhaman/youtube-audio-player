package com.github.kotvertolet.youtubeaudioplayer.custom;

import android.net.Uri;
import android.util.Log;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.AudioStreamsUtils;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CacheTask implements Runnable {

    private final String TAG = getClass().getSimpleName();
    private AtomicBoolean running;
    private AtomicBoolean stopCaching;
    private YoutubeSongDto songDto;
    private Uri uri;
    private SimpleCache cache;
    private DataSource dataSource;
    private AudioStreamsUtils audioStreamsUtils;

    public CacheTask(YoutubeSongDto songDto, Uri uri, SimpleCache cache, DataSource dataSource) {
        running = new AtomicBoolean(false);
        stopCaching = new AtomicBoolean(false);
        this.songDto = songDto;
        this.uri = uri;
        this.cache = cache;
        this.dataSource = dataSource;
        this.audioStreamsUtils = new AudioStreamsUtils();
    }

    @Override
    public void run() {
        running.set(true);
        while (running.get()) {
            try {
                DataSpec dataSpec = new DataSpec(uri);
                CacheUtil.getCached(dataSpec, cache, null);
                //TODO: Add progress listener here
                CacheUtil.cache(dataSpec, cache, null, dataSource, null, stopCaching);
            } catch (IOException e) {
                e.printStackTrace();
                running.set(false);
                return;
            } catch (InterruptedException e) {
                Log.i(TAG, "Thread stopped, video id: " + songDto.getVideoId());
                running.set(false);
                return;
            }
        }
    }

    public void start() {
        if (!running.get()) {
            Thread thread = new Thread(this);
            thread.start();
            Log.i(TAG, String.format("Task for caching video with video id '%s' started", songDto.getVideoId()));
        } else {
            Log.e(TAG, String.format("Task for caching video with video id '%s' is already started", songDto.getVideoId()));
        }
    }

    public void stop() {
        if (running.get()) {
            stopCaching.set(true);
            running.set(false);
        } else {
            Log.e(TAG, String.format("Task for caching video with video id '%s' is already stopped or not started", songDto.getVideoId()));
        }
    }

    public boolean isCached() {
        return audioStreamsUtils.isSongFullyCached(uri);
    }
}
