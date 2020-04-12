package com.github.kotvertolet.youtubeaudioplayer.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.custom.AsyncTaskResult;
import com.github.kotvertolet.youtubeaudioplayer.data.dataSource.RemoteDataSource;
import com.github.kotvertolet.youtubeaudioplayer.data.models.YoutubeSearchResult;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.ContentDetails;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.Snippet;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.Statistics;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.VideoDataItem;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.SnippetItem;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.YoutubeApiSearchResponse;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class VideoSearchAsyncTask extends AsyncTask<String, Void, AsyncTaskResult<YoutubeSearchResult>> {

    private WeakReference<MainActivityContract.Presenter> presenter;
    private WeakReference<MainActivityContract.View> view;
    private CommonUtils commonUtils;
    private RemoteDataSource remoteDataSource;

    public VideoSearchAsyncTask(MainActivityContract.Presenter presenter, WeakReference<MainActivityContract.View> view, CommonUtils commonUtils) {
        this.presenter = new WeakReference<>(presenter);
        this.view = view;
        this.commonUtils = commonUtils;
        remoteDataSource = RemoteDataSource.getInstance();
    }

    @Override
    protected AsyncTaskResult<YoutubeSearchResult> doInBackground(String... strings) {
        AsyncTaskResult<YoutubeSearchResult> taskResult;
        YoutubeApiSearchResponse searchResponse;
        Response<YoutubeApiSearchResponse> rawResponse;
        String query = strings[0];
        try {
            // strings.length > 1 means that strings contain nextPageToken
            if (strings.length > 1) {
                rawResponse = remoteDataSource.searchYoutubeNextPage(query, strings[1]);
            }
            else {
                rawResponse = remoteDataSource.searchYoutubeFirstPage(query);
            }
            if (rawResponse != null && rawResponse.isSuccessful()) {
                searchResponse = rawResponse.body();
                ArrayList<String> idsList = new ArrayList<>();
                for (SnippetItem item : searchResponse.getItems()) {
                    idsList.add(item.getId().getVideoId());
                }
                List<YoutubeSongDto> ytVideoData = new ArrayList<>();
                YoutubeVideoListResponse videosListResponse;
                videosListResponse = remoteDataSource.getBasicVideoInfo(TextUtils.join(",", idsList)).body();

                for (VideoDataItem item : videosListResponse.getItems()) {
                    Snippet snippet = item.getSnippet();
                    ContentDetails contentDetails = item.getContentDetails();
                    Statistics stats = item.getStatistics();
                    String duration = commonUtils.parseISO8601time(contentDetails.getDuration());
                    String viewCount = commonUtils.formatYtViewsAndLikesString(stats.getViewCount());
                    String likeCount = commonUtils.formatYtViewsAndLikesString(stats.getLikeCount());
                    String dislikeCount = commonUtils.formatYtViewsAndLikesString(stats.getDislikeCount());
                    ytVideoData.add(new YoutubeSongDto(item.getId(), snippet.getTitle(),
                            snippet.getChannelTitle(), duration, 0, snippet.getThumbnails().getHigh().getUrl(),
                            viewCount, likeCount, dislikeCount));
                }
                taskResult = new AsyncTaskResult<>(new YoutubeSearchResult(ytVideoData, query, searchResponse.getNextPageToken()));
            } else {
                throw new Exception(rawResponse.errorBody().string());
            }
        } catch (Exception e) {
            taskResult = new AsyncTaskResult<>(e);
        }
        return taskResult;

    }

    @Override
    protected void onPostExecute(AsyncTaskResult<YoutubeSearchResult> taskResult) {
        Exception exception = taskResult.getError();
        if (exception != null) {
            presenter.get().handleException(exception);
        } else view.get().showSearchResults(taskResult.getResult());
    }
}