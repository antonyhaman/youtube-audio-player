package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch;

import com.google.gson.annotations.SerializedName;

public class PageInfo {

    @SerializedName("totalResults")
    private int totalResults;

    @SerializedName("resultsPerPage")
    private int resultsPerPage;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    @Override
    public String toString() {
        return
                "PageInfo{" +
                        "totalResults = '" + totalResults + '\'' +
                        ",resultsPerPage = '" + resultsPerPage + '\'' +
                        "}";
    }
}