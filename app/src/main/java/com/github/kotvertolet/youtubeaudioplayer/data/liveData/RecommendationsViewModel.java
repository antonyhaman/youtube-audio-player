package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class RecommendationsViewModel extends AndroidViewModel {

    private static RecommendationsViewModel INSTANCE;
    private MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> data;

    public RecommendationsViewModel(@NonNull Application application) {
        super(application);
        INSTANCE = this;
    }

    public static RecommendationsViewModel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RecommendationsViewModel(App.getInstance());
        }
        return INSTANCE;
    }

    public MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(new HashMap<>());
        }
        return data;
    }
}
