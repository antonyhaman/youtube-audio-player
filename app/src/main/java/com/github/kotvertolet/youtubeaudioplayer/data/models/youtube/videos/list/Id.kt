package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class Id {
    @SerializedName("kind")
    var kind: String? = null

    @SerializedName("videoId")
    var videoId: String

    constructor(videoId: String) {
        this.videoId = videoId
    }

    constructor(kind: String?, videoId: String) {
        this.kind = kind
        this.videoId = videoId
    }

    override fun toString(): String {
        return "Id{" +
                "kind = '" + kind + '\'' +
                ",videoId = '" + videoId + '\'' +
                "}"
    }
}