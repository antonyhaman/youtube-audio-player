package com.github.kotvertolet.youtubeaudioplayer.data.models;

public class SearchSuggestionsResponse {

    private String[] suggestions;

    public SearchSuggestionsResponse(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }
}
