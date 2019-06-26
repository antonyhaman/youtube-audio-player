package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeApiSearchResponse {

    @SerializedName("regionCode")
    private String regionCode;

    @SerializedName("kind")
    private String kind;

    //TODO: Pagination
    @SerializedName("nextPageToken")
    private String nextPageToken;

    @SerializedName("pageInfo")
    private PageInfo pageInfo;

    @SerializedName("etag")
    private String etag;

    @SerializedName("items")
    private List<SnippetItem> items;

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public List<SnippetItem> getItems() {
        return items;
    }

    public void setItems(List<SnippetItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return
                "YoutubeApiSearchResponse{" +
                        "regionCode = '" + regionCode + '\'' +
                        ",kind = '" + kind + '\'' +
                        ",nextPageToken = '" + nextPageToken + '\'' +
                        ",pageInfo = '" + pageInfo + '\'' +
                        ",etag = '" + etag + '\'' +
                        ",items = '" + items + '\'' +
                        "}";
    }
}