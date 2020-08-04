package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class Statistics {
    @SerializedName("dislikeCount")
    var dislikeCount: String? = null

    @SerializedName("likeCount")
    var likeCount: String? = null

    @SerializedName("viewCount")
    var viewCount: String? = null

    @SerializedName("favoriteCount")
    var favoriteCount: String? = null

    @SerializedName("commentCount")
    var commentCount: String? = null

    override fun toString(): String {
        return "Statistics{" +
                ",dislikeCount = '" + dislikeCount + '\'' +
                ",likeCount = '" + likeCount + '\'' +
                ",viewCount = '" + viewCount + '\'' +
                ",favoriteCount = '" + favoriteCount + '\'' +
                ",commentCount = '" + commentCount + '\'' +
                "}"
    }
}