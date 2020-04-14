package com.github.kotvertolet.youtubeaudioplayer;

import android.app.Application;
import android.content.SharedPreferences;

import com.github.kotvertolet.youtubeaudioplayer.custom.CachingTasksManager;
import com.github.kotvertolet.youtubeaudioplayer.db.AppDatabase;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class App extends Application {

    private static App instance;
    private AppDatabase database;
    private SharedPreferences sharedPreferences;
    private SimpleCache playerCache;
    private CachingTasksManager cachingTasksManager;

    public static synchronized App getInstance() {
        return instance;
    }

    public static void setInstance(App instance) {
        App.instance = instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SimpleCache getPlayerCache() {
        return playerCache;
    }

    public CachingTasksManager getCachingTasksManager() {
        return cachingTasksManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        database = AppDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        playerCache = prepareCache();
        cachingTasksManager = new CachingTasksManager();
    }

    private SimpleCache prepareCache() {
        File cacheFolder = new File(App.getInstance().getCacheDir(), "media");
        int cacheSize = sharedPreferences.getInt(Constants.PREFERENCE_CACHE_SIZE, 250);
        LeastRecentlyUsedCacheEvictor cacheEvictor = new LeastRecentlyUsedCacheEvictor(cacheSize * 1000000);
        return new SimpleCache(cacheFolder, cacheEvictor);
    }
}
