package com.github.kotvertolet.youtubeaudioplayer.network.api;

import com.github.kotvertolet.youtubeaudioplayer.data.models.SearchSuggestionsResponse;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchSuggestionsApi {

    @GET("complete/search")
    Observable<SearchSuggestionsResponse> getSuggestionsRx(@Query("output") String output, @Query("ds") String ds, @Query("q") String query);

    @GET("complete/search")
    Call<ResponseBody> getSuggestions(@Query("output") String output, @Query("ds") String ds, @Query("q") String query);
}
