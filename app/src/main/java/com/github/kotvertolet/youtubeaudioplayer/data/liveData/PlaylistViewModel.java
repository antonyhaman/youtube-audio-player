package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;

import java.util.ArrayList;
import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    private static PlaylistViewModel INSTANCE;
    private MutableLiveData<List<PlaylistDto>> data;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        INSTANCE = this;
    }

    public static PlaylistViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaylistViewModel(App.getInstance());
        }
        return INSTANCE;
    }

    public MutableLiveData<List<PlaylistDto>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(new ArrayList<>());
        }
        return data;
    }
}
