package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class RegionRestriction {
    @SerializedName("allowed")
    var allowed: List<String>? = null

    override fun toString(): String {
        return "RegionRestriction{" +
                "allowed = '" + allowed + '\'' +
                "}"
    }
}