package com.github.kotvertolet.youtubeaudioplayer.data.models.youtube.videos.list;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Snippet {

    @SerializedName("publishedAt")
    private String publishedAt;

    @SerializedName("localized")
    private Localized localized;

    @SerializedName("description")
    private String description;

    @SerializedName("title")
    private String title;

    @SerializedName("thumbnails")
    private Thumbnails thumbnails;

    @SerializedName("channelId")
    private String channelId;

    @SerializedName("categoryId")
    private String categoryId;

    @SerializedName("channelTitle")
    private String channelTitle;

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("liveBroadcastContent")
    private String liveBroadcastContent;

    //TODO: Only for playlist
    @SerializedName("resourceId")
    private ResourceId resourceId;

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Localized getLocalized() {
        return localized;
    }

    public void setLocalized(Localized localized) {
        this.localized = localized;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getLiveBroadcastContent() {
        return liveBroadcastContent;
    }

    public void setLiveBroadcastContent(String liveBroadcastContent) {
        this.liveBroadcastContent = liveBroadcastContent;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return
                "Snippet{" +
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
                        "}";
    }
}