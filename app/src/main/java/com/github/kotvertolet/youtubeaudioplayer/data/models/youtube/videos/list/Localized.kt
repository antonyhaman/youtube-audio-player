package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class Localized {
    @SerializedName("description")
    var description: String? = null

    @SerializedName("title")
    var title: String? = null

    override fun toString(): String {
        return "Localized{" +
                "description = '" + description + '\'' +
                ",title = '" + title + '\'' +
                "}"
    }
}