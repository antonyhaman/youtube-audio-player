package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class Snippet {
    @SerializedName("publishedAt")
    var publishedAt: String? = null

    @SerializedName("localized")
    var localized: Localized? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("thumbnails")
    var thumbnails: Thumbnails? = null

    @SerializedName("channelId")
    var channelId: String? = null

    @SerializedName("categoryId")
    var categoryId: String? = null

    @SerializedName("channelTitle")
    var channelTitle: String? = null

    @SerializedName("tags")
    var tags: List<String>? = null

    @SerializedName("liveBroadcastContent")
    var liveBroadcastContent: String? = null

    //TODO: Only for playlist
    @SerializedName("resourceId")
    var resourceId: ResourceId? = null

    override fun toString(): String {
        return "Snippet{" +
                "publishedAt = '" + publishedAt + '\'' +
                ",localized = '" + localized + '\'' +
                ",description = '" + description + '\'' +
                ",title = '" + title + '\'' +
                ",thumbnails = '" + thumbnails + '\'' +
                ",channelId = '" + channelId + '\'' +
                ",categoryId = '" + categoryId + '\'' +
                ",channelTitle = '" + channelTitle + '\'' +
                ",tags = '" + tags + '\'' +
                ",liveBroadcastContent = '" + liveBroadcastContent + '\'' +
                "}"
    }
}