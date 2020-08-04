package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class Maxres {
    @SerializedName("width")
    var width = 0

    @SerializedName("url")
    var url: String? = null

    @SerializedName("height")
    var height = 0

    override fun toString(): String {
        return "Maxres{" +
                "width = '" + width + '\'' +
                ",url = '" + url + '\'' +
                ",height = '" + height + '\'' +
                "}"
    }
}