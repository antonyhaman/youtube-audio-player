package com.github.kotvertolet.youtubeaudioplayer.custom;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CachingTasksManager {

    private final String TAG = getClass().getSimpleName();
    private ConcurrentHashMap<String, CacheTask> cacheTasksPool;
    private Handler evictHandler;
    private Runnable evictRunnable = new Runnable() {
        @Override
        public void run() {
            if (cacheTasksPool.size() > 0) {
                Set<String> videoIds = cacheTasksPool.keySet();
                for (String videoId : videoIds) {
                    CacheTask cacheTask = cacheTasksPool.get(videoId);

                    if (cacheTask.isCached()) {
                        cacheTask.stop();
                        cacheTasksPool.remove(videoId);
                        Log.i(TAG, "Cache task evicted, video id: " + videoId);
                    }
                }
                evictHandler.postDelayed(this, 30000);
            } else {
                evictHandler.removeCallbacksAndMessages(null);
                evictHandler = null;
            }
        }
    };

    public CachingTasksManager() {
        cacheTasksPool = new ConcurrentHashMap<>();
    }

    public void addTaskAndStart(YoutubeSongDto songDto, Uri uri, SimpleCache cache, DataSource dataSource) {
        String videoId = songDto.getVideoId();
        if (!cacheTasksPool.containsKey(videoId)) {
            CacheTask cacheTask = new CacheTask(songDto, uri, cache, dataSource);
            cacheTask.start();
            cacheTasksPool.put(videoId, cacheTask);
            startCacheTasksEvictor();
        } else {
            Log.i(TAG, "Task for caching song with video id: '%s' \n is already added to pool");
        }
    }

    public boolean hasTask(String videoId) {
        return cacheTasksPool.containsKey(videoId);
    }

    private void startCacheTasksEvictor() {
        if (evictHandler == null) {
            evictHandler = new Handler();
            evictHandler.post(evictRunnable);
        }
    }

    public void stopAllTasks() {
        evictHandler.removeCallbacksAndMessages(null);
        if (cacheTasksPool.size() > 0) {
            Set<String> videoIdsSet = cacheTasksPool.keySet();
            for (String videoId : videoIdsSet) {
                CacheTask cacheTask = cacheTasksPool.get(videoId);
                cacheTask.stop();
            }
            Log.i(TAG, "All caching tasks stopped");
        }
    }
}
