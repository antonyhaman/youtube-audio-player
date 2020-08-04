package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class VideoDataItem {
    @SerializedName("snippet")
    var snippet: Snippet? = null

    @SerializedName("kind")
    var kind: String? = null

    @SerializedName("etag")
    var etag: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("contentDetails")
    var contentDetails: ContentDetails? = null

    @SerializedName("statistics")
    var statistics: Statistics? = null


    override fun toString(): String {
        return "VideoDataItem{" +
                "snippet = '" + snippet + '\'' +
                ",kind = '" + kind + '\'' +
                ",etag = '" + etag + '\'' +
                ",id = '" + id + '\'' +
                ",contentDetails = '" + contentDetails + '\'' +
                "}"
    }
}