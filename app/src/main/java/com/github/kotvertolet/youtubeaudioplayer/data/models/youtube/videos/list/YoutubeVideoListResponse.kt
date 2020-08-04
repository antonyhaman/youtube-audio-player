package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class YoutubeVideoListResponse {

    @SerializedName("nextPageToken")
    var nextPageToken: String? = null

    @SerializedName("kind")
    var kind: String? = null

    @SerializedName("pageInfo")
    var pageInfo: PageInfo? = null

    @SerializedName("etag")
    var etag: String? = null

    @SerializedName("items")
    var items: List<VideoDataItem>? = null

    override fun toString(): String {
        return "YoutubeVideoListResponse{" +
                "kind = '" + kind + '\'' +
                ",pageInfo = '" + pageInfo + '\'' +
                ",etag = '" + etag + '\'' +
                ",items = '" + items + '\'' +
                "}"
    }
}