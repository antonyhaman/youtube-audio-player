package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list;

import com.google.gson.annotations.SerializedName;

public class Statistics {

    @SerializedName("dislikeCount")
    private String dislikeCount;

    @SerializedName("likeCount")
    private String likeCount;

    @SerializedName("viewCount")
    private String viewCount;

    @SerializedName("favoriteCount")
    private String favoriteCount;

    @SerializedName("commentCount")
    private String commentCount;

    public String getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return
                "Statistics{" +
                        ",dislikeCount = '" + dislikeCount + '\'' +
                        ",likeCount = '" + likeCount + '\'' +
                        ",viewCount = '" + viewCount + '\'' +
                        ",favoriteCount = '" + favoriteCount + '\'' +
                        ",commentCount = '" + commentCount + '\'' +
                        "}";
    }
}