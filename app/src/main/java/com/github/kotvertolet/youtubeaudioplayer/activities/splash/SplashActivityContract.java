package com.github.kotvertolet.youtubeaudioplayer.activities.splash;

import com.github.kotvertolet.youtubeaudioplayer.activities.BasePresenter;
import com.github.kotvertolet.youtubeaudioplayer.activities.BaseView;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.LinkedList;
import java.util.Map;

public interface SplashActivityContract {

    interface View extends BaseView<SplashActivityContract.Presenter> {

        void onRecommendationsReady(Map<String, LinkedList<YoutubeSongDto>> recommendations);

        void invokeNoConnectionDialog();
    }

    interface Presenter extends BasePresenter {

        void loadYoutubeRecommendations();

        void loadRecents(Map<String, LinkedList<YoutubeSongDto>> recommendationsMap);
    }
}
