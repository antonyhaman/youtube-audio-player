package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;

import java.util.Collections;
import java.util.List;

public class AllPlaylistsAndSongsViewModel extends AndroidViewModel {

    private static AllPlaylistsAndSongsViewModel INSTANCE;
    private MutableLiveData<List<PlaylistWithSongs>> data;

    public AllPlaylistsAndSongsViewModel(@NonNull Application application) {
        super(application);
        INSTANCE = this;
    }

    public static AllPlaylistsAndSongsViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AllPlaylistsAndSongsViewModel(App.getInstance());
        }
        return INSTANCE;
    }

    public MutableLiveData<List<PlaylistWithSongs>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(Collections.EMPTY_LIST);
        }
        return data;
    }
}
