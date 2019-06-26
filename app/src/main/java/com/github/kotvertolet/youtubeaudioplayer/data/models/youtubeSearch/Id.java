package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch;

import com.google.gson.annotations.SerializedName;

public class Id {

    @SerializedName("kind")
    private String kind;

    @SerializedName("videoId")
    private String videoId;

    public Id(String videoId) {
        this.videoId = videoId;
    }

    public Id(String kind, String videoId) {
        this.kind = kind;
        this.videoId = videoId;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return
                "Id{" +
                        "kind = '" + kind + '\'' +
                        ",videoId = '" + videoId + '\'' +
                        "}";
    }
}