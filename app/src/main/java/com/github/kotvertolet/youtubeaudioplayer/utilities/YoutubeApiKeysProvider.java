package com.github.kotvertolet.youtubeaudioplayer.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class YoutubeApiKeysProvider {

    private static volatile YoutubeApiKeysProvider instance;
    private final List<String> youtubeApiKeys = new ArrayList<>();
    private Iterator<String> iter;

    private YoutubeApiKeysProvider() {
        if (youtubeApiKeys.size() == 0) {
            throw new Error("Add youtube data api keys first!");
        }
        iter = youtubeApiKeys.iterator();
        Collections.shuffle(youtubeApiKeys);
    }

    public static YoutubeApiKeysProvider getInstance() {
        YoutubeApiKeysProvider localInstance = instance;
        if (localInstance == null) {
            synchronized (YoutubeApiKeysProvider.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new YoutubeApiKeysProvider();
                }
            }
        }
        return localInstance;
    }

    public String getKey() {
        if (iter.hasNext()) {
            return iter.next();
        } else {
            iter = youtubeApiKeys.iterator();
            return getKey();
        }
    }

}
