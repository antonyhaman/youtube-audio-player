package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.ArrayList;
import java.util.List;

public class PlaybackQueueViewModel extends AndroidViewModel {

    private static PlaybackQueueViewModel INSTANCE;
    private MutableLiveData<List<YoutubeSongDto>> songs;
    private MutableLiveData<Integer> position;

    public PlaybackQueueViewModel(@NonNull Application application) {
        super(application);
        INSTANCE = this;
    }

    public static PlaybackQueueViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaybackQueueViewModel(App.getInstance());
        }
        return INSTANCE;
    }

    @NonNull
    public MutableLiveData<List<YoutubeSongDto>> getSongs() {
        if (songs == null) {
            songs = new MutableLiveData<>();
            songs.setValue(new ArrayList<>());
        }
        return songs;
    }

    @NonNull
    public MutableLiveData<Integer> getPosition() {
        if (position == null) {
            position = new MutableLiveData<>();
            position.setValue(-1);
        }
        return position;
    }
}
