package com.github.kotvertolet.youtubeaudioplayer.data.liveData;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.data.models.YoutubeSearchResult;

public class SearchResultsViewModel extends AndroidViewModel {

    private static SearchResultsViewModel INSTANCE;
    private MutableLiveData<YoutubeSearchResult> data;

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

    public MutableLiveData<YoutubeSearchResult> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            data.setValue(new YoutubeSearchResult(null, null, null));
        }
        return data;
    }
}
