package com.github.kotvertolet.youtubeaudioplayer.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivity;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.custom.AsyncTaskResult;
import com.github.kotvertolet.youtubeaudioplayer.custom.exceptions.UserFriendly;
import com.github.kotvertolet.youtubeaudioplayer.custom.exceptions.UserFriendlyException;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.AudioStreamsUtils;
import com.github.kotvertolet.youtubejextractor.models.AdaptiveAudioStream;
import com.github.kotvertolet.youtubejextractor.models.StreamItem;
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData;

import java.lang.ref.WeakReference;

public class AudioStreamExtractionAsyncTask extends AsyncTask<String, Void, AsyncTaskResult<YoutubeSongDto>> {

    private final static String TAG = AudioStreamExtractionAsyncTask.class.getSimpleName();
    private WeakReference<MainActivityContract.Presenter> presenter;
    private WeakReference<MainActivityContract.View> view;
    private AudioStreamsUtils audioStreamsUtils;
    private YoutubeSongDto model;
    private AudioStreamExtractionAsyncTask.Callback callback;

    public AudioStreamExtractionAsyncTask(MainActivityContract.Presenter presenter,
                                          WeakReference<MainActivityContract.View> view,
                                          AudioStreamsUtils audioStreamsUtils, YoutubeSongDto model,
                                          AudioStreamExtractionAsyncTask.Callback callback) {
        this.presenter = new WeakReference<>(presenter);
        this.view = view;
        this.audioStreamsUtils = audioStreamsUtils;
        this.model = model;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        view.get().showLoadingIndicator(true);
        super.onPreExecute();
    }

    @Override
    protected AsyncTaskResult<YoutubeSongDto> doInBackground(String... params) {
        AsyncTaskResult<YoutubeSongDto> taskResult;
        try {
            Log.i(TAG, "Extracting data for video id: " + params[0]);
            YoutubeVideoData youtubeVideoData = audioStreamsUtils.extractYoutubeVideoData(params[0]);
            StreamItem streamItem = audioStreamsUtils.getAudioStreamForVideo(youtubeVideoData, ((MainActivity) view.get()));
            model.setStreamUrl(streamItem.getUrl());
            model.setDurationInSeconds(Integer.parseInt(youtubeVideoData.getVideoDetails().getLengthSeconds()));
            model.setLastPlayedTimestamp(System.currentTimeMillis());
            long id = App.getInstance().getDatabase().youtubeSongDao().insert(model);
            model.setId(id);
            taskResult = new AsyncTaskResult<>(model);
        } catch (UserFriendlyException e) {
            taskResult = new AsyncTaskResult<>(e);
        }
        return taskResult;
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<YoutubeSongDto> taskResult) {
        view.get().showLoadingIndicator(false);

        Exception exception = taskResult.getError();
        if (exception != null) {
            if (exception.getClass().isInstance(UserFriendly.class)) {
                UserFriendly ex = (UserFriendly) exception;
                presenter.get().handleException(ex);
            } else presenter.get().handleException(exception);
        } else {
            callback.call(taskResult);
        }
    }

    public interface Callback {
        void call(AsyncTaskResult<YoutubeSongDto> taskResult);
    }
}
