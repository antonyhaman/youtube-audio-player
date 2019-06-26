package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsViewModel extends AndroidViewModel {

    private static SearchResultsViewModel INSTANCE;
    private MutableLiveData<List<YoutubeSongDto>> data;

    public SearchResultsViewModel(@NonNull Application application) {
        super(application);
        INSTANCE = this;
    }

    public static SearchResultsViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchResultsViewModel(App.getInstance());
        }
        return INSTANCE;
    }

    public MutableLiveData<List<YoutubeSongDto>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(new ArrayList<>());
        }
        return data;
    }
}
