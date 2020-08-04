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
        youtubeApiKeys.add("AIzaSyA1dZ5u9JC9j8HpzBVNw8BwrNLDTDkqS3I");
        youtubeApiKeys.add("AIzaSyAUA1FFr5YKMcy_zjSDdGZFfIlKugxVgSM");
        youtubeApiKeys.add("AIzaSyAAXQD3_VR0dnLSHSYKOWUBHss9m27oBcc");
        youtubeApiKeys.add("AIzaSyCuCljT7hV44ZIoqXfilkztwdq1ZomhLFw");
        youtubeApiKeys.add("AIzaSyBnNjcvvkAKbifj3Yw2nmrzMJUALoNHxXM");
        youtubeApiKeys.add("AIzaSyBlOh7WHHYbelElphSoUAvCahpjo_zQom0");
        youtubeApiKeys.add("AIzaSyBp_5QruvEa4PiIWb788N-5_GuSCW_j7pc");
        youtubeApiKeys.add("AIzaSyDpTO_uR7-11s1FF9xQoru7f1i-M6dZiME");
        youtubeApiKeys.add("AIzaSyDFdPe9ROdCxClxT1cPQXSyYh705Z9N6FU");
        youtubeApiKeys.add("AIzaSyCHxTxxmay9TD7izuLBQYrOGSrupura_tQ");
        youtubeApiKeys.add("AIzaSyABcQuDwbl-3g-TxTgm4KBLACqpTAvGMg8");
        youtubeApiKeys.add("AIzaSyAhqowApJizByAN-iXRJLIeXV21U_F1Fnk");
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
