package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list;

import com.google.gson.annotations.SerializedName;

public class VideoDataItem {

    @SerializedName("snippet")
    private Snippet snippet;

    @SerializedName("kind")
    private String kind;

    @SerializedName("etag")
    private String etag;

    @SerializedName("id")
    private String id;

    @SerializedName("contentDetails")
    private ContentDetails contentDetails;

    @SerializedName("statistics")
    private Statistics statistics;

//    @SerializedName("id")
//    private Id id;

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //    public Id getId() {
//        return id;
//    }
//
//    public void setId(Id id) {
//        this.id = id;
//    }

    @Override
    public String toString() {
        return
                "VideoDataItem{" +
                        "snippet = '" + snippet + '\'' +
                        ",kind = '" + kind + '\'' +
                        ",etag = '" + etag + '\'' +
                        ",id = '" + id + '\'' +
                        ",contentDetails = '" + contentDetails + '\'' +
                        "}";
    }
}