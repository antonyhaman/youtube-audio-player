package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeVideoListResponse {

    @SerializedName("nextPageToken")
    private String nextPageToken;

    @SerializedName("kind")
    private String kind;

    @SerializedName("pageInfo")
    private PageInfo pageInfo;

    @SerializedName("etag")
    private String etag;

    @SerializedName("items")
    private List<VideoDataItem> items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public List<VideoDataItem> getItems() {
        return items;
    }

    public void setItems(List<VideoDataItem> items) {
        this.items = items;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @Override
    public String toString() {
        return
                "YoutubeVideoListResponse{" +
                        "kind = '" + kind + '\'' +
                        ",pageInfo = '" + pageInfo + '\'' +
                        ",etag = '" + etag + '\'' +
                        ",items = '" + items + '\'' +
                        "}";
    }
}