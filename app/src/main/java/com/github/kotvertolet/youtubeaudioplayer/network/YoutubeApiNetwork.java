package com.github.kotvertolet.youtubeaudioplayer.network;

import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.Id;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.YoutubeApiSearchResponse;
import com.github.kotvertolet.youtubeaudioplayer.network.api.YoutubeApi;
import com.github.kotvertolet.youtubeaudioplayer.utilities.YoutubeApiKeysProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_ORDER_RELEVANCE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_PART_SNIPPET;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_SEARCH_MAX_RESULTS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.QUERY_TYPE_VIDEO;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.YOUTUBE_API_URL;


public class YoutubeApiNetwork {

    private static YoutubeApiNetwork instance;
    private YoutubeApi youtubeApi;

    private YoutubeApiNetwork() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Id.class, new DateTimeDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YOUTUBE_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        youtubeApi = retrofit.create(YoutubeApi.class);
    }

    public static synchronized YoutubeApiNetwork getInstance() {
        if (instance == null) {
            instance = new YoutubeApiNetwork();
            return instance;
        } else return instance;
    }

    public Response<YoutubeApiSearchResponse> getSearchResultsForFirstPage(String query) throws IOException {
        return youtubeApi.getSearchResultsFirstPage(YoutubeApiKeysProvider.getInstance().getKey(), QUERY_PART_SNIPPET, query, QUERY_SEARCH_MAX_RESULTS, QUERY_TYPE_VIDEO, QUERY_ORDER_RELEVANCE).execute();
    }

    public Response<YoutubeApiSearchResponse> getSearchResultsForNextPage(String query, String nextPakeToken) throws IOException {
        return youtubeApi.getSearchResultsFirstPage(YoutubeApiKeysProvider.getInstance().getKey(), QUERY_PART_SNIPPET, query, QUERY_SEARCH_MAX_RESULTS, QUERY_TYPE_VIDEO, QUERY_ORDER_RELEVANCE, nextPakeToken).execute();
    }


    public Observable<YoutubeApiSearchResponse> getSearchResultsRx(String query) {
        return youtubeApi.getSearchResultsRx(YoutubeApiKeysProvider.getInstance().getKey(), QUERY_PART_SNIPPET, query, QUERY_SEARCH_MAX_RESULTS, QUERY_TYPE_VIDEO, QUERY_ORDER_RELEVANCE);
    }

    public void getSearchResultsForFirstPage(String query, String pageToken, Callback<YoutubeApiSearchResponse> callback) {
        youtubeApi.getSearchResultsFirstPage(YoutubeApiKeysProvider.getInstance().getKey(), QUERY_PART_SNIPPET, query, QUERY_SEARCH_MAX_RESULTS, QUERY_TYPE_VIDEO, QUERY_ORDER_RELEVANCE, pageToken).enqueue(callback);
    }


    public Response<YoutubeVideoListResponse> getVideoInfo(String key, String part, String videoId) throws IOException {
        return youtubeApi.getVideoInfo(key, part, videoId).execute();
    }

    public void getVideoInfo(String key, String part, String videoId, Callback<YoutubeVideoListResponse> callback) {
        youtubeApi.getVideoInfo(key, part, videoId).enqueue(callback);
    }

    public Observable<YoutubeVideoListResponse> getVideoInfoRx(String key, String part, String videoId, String maxResult) {
        return youtubeApi.getVideoInfoRx(key, part, videoId, maxResult);
    }

    public Response<YoutubeApiSearchResponse> getVideoInfo(String key, String part, String chart, String videoCategoryId, int maxResults) throws IOException {
        return youtubeApi.getVideoInfo(key, part, chart, videoCategoryId, maxResults).execute();
    }

    public Response<YoutubeApiSearchResponse> getVideoInfo(String key, String part, String chart, String videoCategoryId, String countryCode, int maxResults) throws IOException {
        return youtubeApi.getVideoInfo(key, part, chart, videoCategoryId, countryCode, maxResults).execute();
    }

    public Maybe<YoutubeVideoListResponse> getPlaylistRx(String apiKey, String part, String playlistId, String maxResults) {
        return youtubeApi.getPlaylistRx(apiKey, part, playlistId, maxResults);
    }

    private class DateTimeDeserializer implements JsonDeserializer<Id> {
        public Id deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String rawId = json.getAsJsonPrimitive().getAsString();
                return new Id(rawId);
            } catch (IllegalStateException e) {
                String rawKind = json.getAsJsonObject().get("kind").getAsString();
                String rawId = json.getAsJsonObject().get("videoId").getAsString();
                return new Id(rawKind, rawId);
            }
        }
    }
}
