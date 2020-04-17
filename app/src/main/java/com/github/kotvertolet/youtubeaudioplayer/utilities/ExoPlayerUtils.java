package com.github.kotvertolet.youtubeaudioplayer.utilities;

import android.net.Uri;
import android.os.Handler;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.custom.CachingTasksManager;
import com.github.kotvertolet.youtubeaudioplayer.data.NetworkType;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.USER_AGENT;

public class ExoPlayerUtils {

    private AudioStreamsUtils audioStreamsUtils = new AudioStreamsUtils();
    private CachingTasksManager cacheTaskManager;
    private SimpleCache cache;
    private CommonUtils commonUtils;

    public ExoPlayerUtils() {
        App app = App.getInstance();
        cacheTaskManager = app.getCachingTasksManager();
        cache = app.getPlayerCache();
        commonUtils = new CommonUtils();

    }

    public MediaSource prepareMediaSource(YoutubeSongDto songDto) {
        Uri uri = Uri.parse(songDto.getStreamUrl());
        if (audioStreamsUtils.isSongFullyCached(songDto)) {
            return prepareCachedMediaSource(uri);
        } else {
            return prepareSimpleMediaSource(uri, songDto);
        }
    }

    public MediaSource prepareCachedMediaSource(Uri uri) {
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(cache,
                new DefaultHttpDataSourceFactory(USER_AGENT));
        return new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);
    }

    public MediaSource prepareSimpleMediaSource(Uri uri, YoutubeSongDto songDto) {
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(USER_AGENT);
        // Only audio shorter than 30 mins is allowed for caching
        if (songDto.getDurationInSeconds() < 1800 && !cacheTaskManager.hasTask(songDto.getVideoId())) {
            App app = App.getInstance();
            if (app.getSharedPreferences().getBoolean(Constants.PREFERENCE_RESTRICT_MOBILE_NETWORK_CACHING, true)) {
                if (commonUtils.getNetworkClass(app).equals(NetworkType.TYPE_WIFI)) {
                    cacheTaskManager.addTaskAndStart(songDto, uri, cache, dataSourceFactory.createDataSource());
                }
            } else
                cacheTaskManager.addTaskAndStart(songDto, uri, cache, dataSourceFactory.createDataSource());
        }
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    public void stopCacheTasks() {
        cacheTaskManager.stopAllTasks();
    }

    public void stopHandlers(Handler... handlers) {
        for (Handler handler : handlers) {
            if (handler != null) handler.removeCallbacksAndMessages(null);
        }
    }


}
