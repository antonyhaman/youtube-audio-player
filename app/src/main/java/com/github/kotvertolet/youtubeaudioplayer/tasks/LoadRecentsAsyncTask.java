package com.github.kotvertolet.youtubeaudioplayer.tasks;

import android.os.AsyncTask;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.activities.splash.SplashActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.db.dao.YoutubeSongDao;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;

import java.util.LinkedList;
import java.util.Map;

public class LoadRecentsAsyncTask extends AsyncTask<Map<String, LinkedList<YoutubeSongDto>>, Void, Map<String, LinkedList<YoutubeSongDto>>> {

    private SplashActivityContract.View view;

    public LoadRecentsAsyncTask(SplashActivityContract.View view) {
        this.view = view;
    }

    @SafeVarargs
    @Override
    protected final Map<String, LinkedList<YoutubeSongDto>> doInBackground(Map<String, LinkedList<YoutubeSongDto>>... maps) {
        Map<String, LinkedList<YoutubeSongDto>> recommendationsMap = maps[0];
        YoutubeSongDao youtubeSongDao = App.getInstance().getDatabase().youtubeSongDao();
        LinkedList<YoutubeSongDto> youtubeSongsFromDb =
                new LinkedList<>(youtubeSongDao.getLastPlayed(20));
        if (youtubeSongsFromDb.size() > 0) {
            recommendationsMap.put(Constants.RECOMMENDATIONS_RECENT, youtubeSongsFromDb);
        }
        return recommendationsMap;
    }

    @Override
    protected void onPostExecute(Map<String, LinkedList<YoutubeSongDto>> recommendationsMap) {
        view.onRecommendationsReady(recommendationsMap);
        super.onPostExecute(recommendationsMap);
    }
}
