package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeVideoInfo;

import com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch.PageInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoInfoResponse {

    @SerializedName("kind")
    private String kind;

    @SerializedName("pageInfo")
    private PageInfo pageInfo;

    @SerializedName("etag")
    private String etag;

    @SerializedName("items")
    private List<VideoInfoItem> items;

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

    public List<VideoInfoItem> getItems() {
        return items;
    }

    public void setItems(List<VideoInfoItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return
                "VideoInfoResponse{" +
                        "kind = '" + kind + '\'' +
                        ",pageInfo = '" + pageInfo + '\'' +
                        ",etag = '" + etag + '\'' +
                        ",items = '" + items + '\'' +
                        "}";
    }
}