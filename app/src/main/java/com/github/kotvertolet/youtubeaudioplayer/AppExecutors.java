package com.github.kotvertolet.youtubeaudioplayer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {

    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private final static ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
    private static AppExecutors INSTANCE;

    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppExecutors();
        }
        return INSTANCE;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
