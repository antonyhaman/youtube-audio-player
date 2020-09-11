package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class ContentDetails {
    @SerializedName("duration")
    var duration: String? = null

    @SerializedName("licensedContent")
    var isLicensedContent = false

    @SerializedName("caption")
    var caption: String? = null

    @SerializedName("definition")
    var definition: String? = null

    @SerializedName("regionRestriction")
    var regionRestriction: RegionRestriction? = null

    @SerializedName("projection")
    var projection: String? = null

    @SerializedName("dimension")
    var dimension: String? = null

    override fun toString(): String {
        return "ContentDetails{" +
                "duration = '" + duration + '\'' +
                ",licensedContent = '" + isLicensedContent + '\'' +
                ",caption = '" + caption + '\'' +
                ",definition = '" + definition + '\'' +
                ",regionRestriction = '" + regionRestriction + '\'' +
                ",projection = '" + projection + '\'' +
                ",dimension = '" + dimension + '\'' +
                "}"
    }
}