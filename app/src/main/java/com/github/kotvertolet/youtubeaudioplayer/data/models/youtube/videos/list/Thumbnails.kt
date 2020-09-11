package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class Thumbnails {
    @SerializedName("standard")
    var standard: Standard? = null

    @SerializedName("default")
    var jsonMemberDefault: JsonMemberDefault? = null

    @SerializedName("high")
    var high: High? = null

    @SerializedName("maxres")
    var maxres: Maxres? = null

    @SerializedName("medium")
    var medium: Medium? = null

    override fun toString(): String {
        return "Thumbnails{" +
                "standard = '" + standard + '\'' +
                ",default = '" + jsonMemberDefault + '\'' +
                ",high = '" + high + '\'' +
                ",maxres = '" + maxres + '\'' +
                ",medium = '" + medium + '\'' +
                "}"
    }
}