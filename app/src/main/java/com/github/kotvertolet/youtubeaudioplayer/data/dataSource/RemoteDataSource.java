package com.github.kotvertolet.youtubeaudioplayer.data.dataSource;

import com.github.kotvertolet.youtubeaudioplayer.data.models.SearchSuggestionsResponse;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.YoutubeApiSearchResponse;
import com.github.kotvertolet.youtubeaudioplayer.network.SearchSuggestionsNetwork;
import com.github.kotvertolet.youtubeaudioplayer.network.YoutubeApiNetwork;
import com.github.kotvertolet.youtubeaudioplayer.utilities.YoutubeApiKeysProvider;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;

import java.io.IOException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYLIST_MOST_VIEWED_CHANNEL_ID;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYLIST_NEW_MUSIC_THIS_WEEK_CHANNEL_ID;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYLIST_TOP_TRACKS_CHANNEL_ID;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_PART_CONTENT_DETAILS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_PART_SNIPPET;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_PART_STATISTICS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_PLAYLIST_MAX_RESULTS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_SUGGESTIONS_DS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_SUGGESTIONS_OUTPUT;

public class RemoteDataSource {

    private static RemoteDataSource instance;
    private YoutubeApiNetwork youtubeApiNetwork;
    private SearchSuggestionsNetwork searchSuggestionsNetwork;

    private RemoteDataSource() {
        youtubeApiNetwork = YoutubeApiNetwork.getInstance();
        searchSuggestionsNetwork = SearchSuggestionsNetwork.getInstance();
    }

    public static synchronized RemoteDataSource getInstance() {
        if (instance == null) {
            instance = new RemoteDataSource();
            return instance;
        } else return instance;
    }

    public Response<YoutubeApiSearchResponse> searchYoutubeFirstPage(String query) throws IOException {
        return youtubeApiNetwork.getSearchResultsForFirstPage(query);
    }

    public Response<YoutubeApiSearchResponse> searchYoutubeNextPage(String query, String nextPageToken) throws IOException {
        return youtubeApiNetwork.getSearchResultsForNextPage(query, nextPageToken);
    }

    public Observable<YoutubeApiSearchResponse> searchForVideoRx(String videoId) {
        return youtubeApiNetwork.getSearchResultsRx(videoId);
    }

    public Response<YoutubeVideoListResponse> getVideoInfo(String key, String part, String videoId) throws IOException {
        return youtubeApiNetwork.getVideoInfo(key, part, videoId);
    }

    public Observable<YoutubeVideoListResponse> getVideoInfoRx(String key, String part, String videoId, String maxResults) {
        return youtubeApiNetwork.getVideoInfoRx(key, part, videoId, maxResults);
    }

    public Response<YoutubeApiSearchResponse> getVideoInfo(String key, String part, String chart, String videoCategoryId, Integer maxResults) throws IOException {
        return youtubeApiNetwork.getVideoInfo(key, part, chart, videoCategoryId, maxResults);
    }

    public Response<YoutubeApiSearchResponse> getVideoInfo(String key, String part, String chart, String videoCategoryId, String countryCode, Integer maxResults) throws IOException {
        return youtubeApiNetwork.getVideoInfo(key, part, chart, videoCategoryId, countryCode, maxResults);
    }

    public Maybe<YoutubeVideoListResponse> getPlaylistRx(String apiKey, String part, String playlistId, String maxResults) {
        return youtubeApiNetwork.getPlaylistRx(apiKey, part, playlistId, maxResults);
    }

    public Response<ResponseBody> getSuggestions(String query) throws IOException {
        return searchSuggestionsNetwork.getSuggestions(QUERY_SUGGESTIONS_OUTPUT, QUERY_SUGGESTIONS_DS, query);
    }

    public Observable<SearchSuggestionsResponse> getSuggestionsRx(String query) {
        return searchSuggestionsNetwork.getSuggestionsRx(QUERY_SUGGESTIONS_OUTPUT, QUERY_SUGGESTIONS_DS, query);
    }

    public Maybe<YoutubeVideoListResponse> getTopTracksPlaylist() {
        return getPlaylistRx(PLAYLIST_TOP_TRACKS_CHANNEL_ID);
    }

    public Maybe<YoutubeVideoListResponse> getMostViewedPlaylist() {
        return getPlaylistRx(PLAYLIST_MOST_VIEWED_CHANNEL_ID);
    }

    public Maybe<YoutubeVideoListResponse> getNewMusicThisWeekPlaylist() {
        return getPlaylistRx(PLAYLIST_NEW_MUSIC_THIS_WEEK_CHANNEL_ID);
    }

    public Response<YoutubeVideoListResponse> getBasicVideoInfo(String videoId) throws IOException {
        return youtubeApiNetwork.getVideoInfo(YoutubeApiKeysProvider.getInstance().getKey(),
                String.format("%s,%s,%s", QUERY_PART_SNIPPET, QUERY_PART_CONTENT_DETAILS, QUERY_PART_STATISTICS), videoId);
    }

    public Observable<YoutubeVideoListResponse> getBasicVideoInfoRx(String videoId) {
        return youtubeApiNetwork.getVideoInfoRx(YoutubeApiKeysProvider.getInstance().getKey(),
                String.format("%s,%s,%s", QUERY_PART_SNIPPET, QUERY_PART_CONTENT_DETAILS, QUERY_PART_STATISTICS), videoId, QUERY_PLAYLIST_MAX_RESULTS);
    }

    private Maybe<YoutubeVideoListResponse> getPlaylistRx(String channelId) {
        return youtubeApiNetwork.getPlaylistRx(YoutubeApiKeysProvider.getInstance().getKey(), QUERY_PART_SNIPPET, channelId, QUERY_PLAYLIST_MAX_RESULTS);
    }


}
