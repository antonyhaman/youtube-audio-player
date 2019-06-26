package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;

public class PlaylistsWithSongsViewModel extends AndroidViewModel {

    private static PlaylistsWithSongsViewModel INSTANCE;
    private MutableLiveData<PlaylistWithSongs> data;
    private MutableLiveData<Integer> position;

    public PlaylistsWithSongsViewModel(@NonNull Application application) {
        super(application);
        INSTANCE = this;
    }

    public static PlaylistsWithSongsViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaylistsWithSongsViewModel(App.getInstance());
        }
        return INSTANCE;
    }

    public MutableLiveData<PlaylistWithSongs> getPlaylist() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(new PlaylistWithSongs());
        }
        return data;
    }

    public MutableLiveData<Integer> getPlaylistPosition() {
        if (position == null) {
            position = new MutableLiveData<>();
            position.setValue(-1);
        }
        return position;
    }
}
