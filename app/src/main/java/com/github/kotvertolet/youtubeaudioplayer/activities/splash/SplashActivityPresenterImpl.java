package com.github.kotvertolet.youtubeaudioplayer.activities.splash;

import android.content.DialogInterface;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.custom.exceptions.UserFriendly;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.tasks.LoadRecentsAsyncTask;
import com.github.kotvertolet.youtubeaudioplayer.utilities.YoutubeRecommendationsFetchUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;

import java.util.LinkedList;
import java.util.Map;

public class SplashActivityPresenterImpl implements SplashActivityContract.Presenter {

    private CommonUtils utils;
    private SplashActivityContract.View view;

    public SplashActivityPresenterImpl(SplashActivityContract.View view, CommonUtils commonUtils) {
        view.setPresenter(this);
        this.view = view;
        utils = commonUtils;
    }

    @Override
    public <T extends UserFriendly> void handleException(T exception) {
        DialogInterface.OnClickListener listener = (dialog, which) -> dialog.dismiss();
        utils.createAlertDialog(R.string.error, exception.getUserErrorMessage(), true,
                R.string.button_ok, listener, 0, null);
    }

    //TODO: Implement it
    @Override
    public void handleException(Exception exception) {

    }

    @Override
    public void loadYoutubeRecommendations() {
        if (utils.isNetworkAvailable()) {
            new YoutubeRecommendationsFetchUtils(utils, this).fetchYoutubeRecommendations();
        } else {
            view.invokeNoConnectionDialog();
        }
    }

    @Override
    public void loadRecents(Map<String, LinkedList<YoutubeSongDto>> recommendationsMap) {
        new LoadRecentsAsyncTask(view).execute(recommendationsMap);
    }
}
