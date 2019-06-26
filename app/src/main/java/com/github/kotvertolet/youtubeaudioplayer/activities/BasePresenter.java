package com.github.kotvertolet.youtubeaudioplayer.activities;

import com.github.kotvertolet.youtubeaudioplayer.custom.exceptions.UserFriendly;

public interface BasePresenter {

    <T extends UserFriendly> void handleException(T exception);

    void handleException(Exception exception);
}
