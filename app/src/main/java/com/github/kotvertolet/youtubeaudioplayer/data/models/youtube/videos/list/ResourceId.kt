package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class ResourceId {
    @SerializedName("kind")
    var kind: String? = null

    @SerializedName("videoId")
    var videoId: String? = null

    override fun toString(): String {
        return "ResourceId{" +
                "kind = '" + kind + '\'' +
                ",videoId = '" + videoId + '\'' +
                "}"
    }
}