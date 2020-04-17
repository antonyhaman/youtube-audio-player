package com.github.kotvertolet.youtubeaudioplayer;

import android.content.SharedPreferences;

import com.github.kotvertolet.youtubeaudioplayer.custom.CachingTasksManager;
import com.github.kotvertolet.youtubeaudioplayer.db.AppDatabase;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil;
import com.google.android.exoplayer2.offline.DefaultDownloadIndex;
import com.google.android.exoplayer2.offline.DefaultDownloaderFactory;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Log;

import java.io.File;
import java.io.IOException;

import androidx.multidex.MultiDexApplication;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.USER_AGENT;

public class App extends MultiDexApplication {

    public static final String DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel";
    private static final String TAG = "DemoApplication";
    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private static App instance;
    private AppDatabase database;
    private SharedPreferences sharedPreferences;
    private SimpleCache playerCache;
    private CachingTasksManager cachingTasksManager;
    private DownloadNotificationHelper downloadNotificationHelper;
    private DatabaseProvider databaseProvider;
    private File downloadDirectory;
    private Cache downloadCache;
    private DownloadManager downloadManager;

    public static synchronized App getInstance() {
        return instance;
    }

    public static void setInstance(App instance) {
        App.instance = instance;
    }

    protected static CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DataSource.Factory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSource.Factory(),null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
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

    public DownloadNotificationHelper getDownloadNotificationHelper() {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper =
                    new DownloadNotificationHelper(this, DOWNLOAD_NOTIFICATION_CHANNEL_ID);
        }
        return downloadNotificationHelper;
    }

    private SimpleCache prepareCache() {
        File cacheFolder = new File(App.getInstance().getCacheDir(), "media");
        int cacheSize = sharedPreferences.getInt(Constants.PREFERENCE_CACHE_SIZE, 250);
        LeastRecentlyUsedCacheEvictor cacheEvictor = new LeastRecentlyUsedCacheEvictor(cacheSize * 1000000);
        return new SimpleCache(cacheFolder, cacheEvictor);
    }

    private synchronized void initDownloadManager() {
        if (downloadManager == null) {
            DefaultDownloadIndex downloadIndex = new DefaultDownloadIndex(getDatabaseProvider());
            upgradeActionFile(
                    DOWNLOAD_ACTION_FILE, downloadIndex, false);
            upgradeActionFile(
                    DOWNLOAD_TRACKER_ACTION_FILE, downloadIndex, true);
            DownloaderConstructorHelper downloaderConstructorHelper =
                    new DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory());
            downloadManager =
                    new DownloadManager(
                            this, downloadIndex, new DefaultDownloaderFactory(downloaderConstructorHelper));
        }
    }

    /**
     * Returns a {@link HttpDataSource.Factory}.
     */
    public HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory(USER_AGENT);
    }

    private void upgradeActionFile(
            String fileName, DefaultDownloadIndex downloadIndex, boolean addNewDownloadsAsCompleted) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                    new File(getDownloadDirectory(), fileName),null,
                    downloadIndex, true, addNewDownloadsAsCompleted);
        } catch (IOException e) {
            Log.e(TAG, "Failed to upgrade action file: " + fileName, e);
        }
    }

    private DatabaseProvider getDatabaseProvider() {
        if (databaseProvider == null) {
            databaseProvider = new ExoDatabaseProvider(this);
        }
        return databaseProvider;
    }

    public DownloadManager getDownloadManager() {
        initDownloadManager();
        return downloadManager;
    }

    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = getFilesDir();
            }
        }
        return downloadDirectory;
    }

    protected synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache =
                    new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor(), getDatabaseProvider());
        }
        return downloadCache;
    }
}
