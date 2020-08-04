package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list

import com.google.gson.annotations.SerializedName

class PageInfo {
    @SerializedName("totalResults")
    var totalResults = 0

    @SerializedName("resultsPerPage")
    var resultsPerPage = 0

    override fun toString(): String {
        return "PageInfo{" +
                "totalResults = '" + totalResults + '\'' +
                ",resultsPerPage = '" + resultsPerPage + '\'' +
                "}"
    }
}