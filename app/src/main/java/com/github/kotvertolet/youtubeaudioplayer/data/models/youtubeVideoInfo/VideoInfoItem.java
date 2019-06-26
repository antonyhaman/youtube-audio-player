package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeVideoInfo;

import com.google.gson.annotations.SerializedName;

public class VideoInfoItem {

    @SerializedName("kind")
    private String kind;

    @SerializedName("etag")
    private String etag;

    @SerializedName("id")
    private String id;

    @SerializedName("contentDetails")
    private ContentDetails contentDetails;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    @Override
    public String toString() {
        return
                "VideoInfoItem{" +
                        "kind = '" + kind + '\'' +
                        ",etag = '" + etag + '\'' +
                        ",id = '" + id + '\'' +
                        ",contentDetails = '" + contentDetails + '\'' +
                        "}";
    }
}