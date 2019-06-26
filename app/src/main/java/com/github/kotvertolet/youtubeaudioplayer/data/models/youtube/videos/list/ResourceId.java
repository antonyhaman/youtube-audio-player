package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list;

import com.google.gson.annotations.SerializedName;

public class ResourceId {

    @SerializedName("kind")
    private String kind;

    @SerializedName("videoId")
    private String videoId;

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
                "ResourceId{" +
                        "kind = '" + kind + '\'' +
                        ",videoId = '" + videoId + '\'' +
                        "}";
    }
}