package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegionRestriction {

    @SerializedName("allowed")
    private List<String> allowed;

    public List<String> getAllowed() {
        return allowed;
    }

    public void setAllowed(List<String> allowed) {
        this.allowed = allowed;
    }

    @Override
    public String toString() {
        return
                "RegionRestriction{" +
                        "allowed = '" + allowed + '\'' +
                        "}";
    }
}