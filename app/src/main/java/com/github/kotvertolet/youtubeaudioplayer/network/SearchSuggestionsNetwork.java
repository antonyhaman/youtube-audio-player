package com.github.kotvertolet.youtubeaudioplayer.network;

import com.github.kotvertolet.youtubeaudioplayer.data.models.SearchSuggestionsResponse;
import com.github.kotvertolet.youtubeaudioplayer.network.api.SearchSuggestionsApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.GOOGLE_SEARCH_SUGGESTIONS;


public class SearchSuggestionsNetwork {

    private static SearchSuggestionsNetwork instance;
    private SearchSuggestionsApi suggestionsApi;
    private Retrofit retrofit;

    private SearchSuggestionsNetwork() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SearchSuggestionsResponse.class, new SuggestionsDeserializer())
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_SEARCH_SUGGESTIONS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        suggestionsApi = retrofit.create(SearchSuggestionsApi.class);
    }

    public static synchronized SearchSuggestionsNetwork getInstance() {
        if (instance == null) {
            instance = new SearchSuggestionsNetwork();
            return instance;
        } else return instance;
    }

    /**
     * Method gets the suggestion for the query provided
     *
     * @param output format of the output, for json should be 'firefox'
     * @param ds     restricts the search to the particular site (for youtube it's 'yt')
     * @param query  search query
     * @return
     */
    public Observable<SearchSuggestionsResponse> getSuggestionsRx(String output, String ds, String query) {
        return suggestionsApi.getSuggestionsRx(output, ds, query);
    }

    public Response<ResponseBody> getSuggestions(String output, String ds, String query) throws IOException {
        return suggestionsApi.getSuggestions(output, ds, query).execute();
    }

    private class SuggestionsDeserializer implements JsonDeserializer<SearchSuggestionsResponse> {
        public SearchSuggestionsResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String rawSt = json.toString();
            String[] suggestions = rawSt.replaceAll("\\[(.*?)\\[", "")
                    .replaceAll("]", "")
                    .replaceAll("\"", "")
                    .split(",");
            return new SearchSuggestionsResponse(suggestions);
        }
    }
}
