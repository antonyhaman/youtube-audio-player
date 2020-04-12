package com.github.kotvertolet.youtubeaudioplayer.network.api;

import com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list.YoutubeVideoListResponse;
import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.YoutubeApiSearchResponse;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YoutubeApi {


    @GET("videos")
    Call<YoutubeVideoListResponse> getVideoInfo(@Query("key") String key, @Query("part") String part, @Query("id") String videoId);

    @GET("videos")
    Observable<YoutubeVideoListResponse> getVideoInfoRx(@Query("key") String key, @Query("part") String part,
                                                        @Query("id") String videoId, @Query("maxResults") String maxResults);

    @GET("videos")
    Call<YoutubeApiSearchResponse> getVideoInfo(@Query("key") String key, @Query("part") String part,
                                                @Query("chart") String chart, @Query("videoCategoryId") String videoCategoryId,
                                                @Query("maxResults") int maxResults);

    @GET("videos")
    Call<YoutubeApiSearchResponse> getVideoInfo(@Query("key") String key, @Query("part") String part,
                                                @Query("chart") String chart, @Query("videoCategoryId") String videoCategoryId,
                                                @Query("regionCode") String countryCode, @Query("maxResults") int maxResults);

    @GET("search")
    Call<YoutubeApiSearchResponse> getSearchResultsFirstPage(@Query("key") String apiKey, @Query("part") String part,
                                                             @Query("q") String query, @Query("maxResults") int maxResults,
                                                             @Query("type") String type, @Query("order") String order,
                                                             @Query("pageToken") String pageToken);

    @GET("search")
    Call<YoutubeApiSearchResponse> getSearchResultsFirstPage(@Query("key") String apiKey, @Query("part") String part,
                                                             @Query("q") String query, @Query("maxResults") int maxResults,
                                                             @Query("type") String type, @Query("order") String order);

    @GET("search")
    Call<YoutubeApiSearchResponse> getSearchResultsNextPage(@Query("key") String apiKey, @Query("part") String part,
                                                            @Query("q") String query, @Query("maxResults") int maxResults,
                                                            @Query("type") String type, @Query("order") String order,
                                                            @Query("nextPageToken") String nextPageToken);


    @GET("search")
    Observable<YoutubeApiSearchResponse> getSearchResultsRx(@Query("key") String apiKey, @Query("part") String part,
                                                            @Query("q") String query, @Query("maxResults") int maxResults,
                                                            @Query("type") String type, @Query("order") String order);

    @GET("playlistItems")
    Maybe<YoutubeVideoListResponse> getPlaylistRx(@Query("key") String apiKey, @Query("part") String part,
                                                  @Query("playlistId") String playlistId, @Query("maxResults") String maxResults);
}
