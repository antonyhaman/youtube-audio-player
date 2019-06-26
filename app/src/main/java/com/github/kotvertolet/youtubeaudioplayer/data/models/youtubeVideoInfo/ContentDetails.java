package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeVideoInfo;

import com.google.gson.annotations.SerializedName;

public class ContentDetails {

    @SerializedName("duration")
    private String duration;

    @SerializedName("licensedContent")
    private boolean licensedContent;

    @SerializedName("caption")
    private String caption;

    @SerializedName("definition")
    private String definition;

    @SerializedName("projection")
    private String projection;

    @SerializedName("dimension")
    private String dimension;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isLicensedContent() {
        return licensedContent;
    }

    public void setLicensedContent(boolean licensedContent) {
        this.licensedContent = licensedContent;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getProjection() {
        return projection;
    }

    public void setProjection(String projection) {
        this.projection = projection;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return
                "ContentDetails{" +
                        "duration = '" + duration + '\'' +
                        ",licensedContent = '" + licensedContent + '\'' +
                        ",caption = '" + caption + '\'' +
                        ",definition = '" + definition + '\'' +
                        ",projection = '" + projection + '\'' +
                        ",dimension = '" + dimension + '\'' +
                        "}";
    }
}