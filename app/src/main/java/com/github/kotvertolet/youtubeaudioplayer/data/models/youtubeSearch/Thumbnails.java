package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch;

import com.google.gson.annotations.SerializedName;

public class Thumbnails {

    @SerializedName("standard")
    private Standard standard;

    @SerializedName("default")
    private JsonMemberDefault jsonMemberDefault;

    @SerializedName("high")
    private High high;

    @SerializedName("maxres")
    private Maxres maxres;

    @SerializedName("medium")
    private Medium medium;

    public Standard getStandard() {
        return standard;
    }

    public void setStandard(Standard standard) {
        this.standard = standard;
    }

    public JsonMemberDefault getJsonMemberDefault() {
        return jsonMemberDefault;
    }

    public void setJsonMemberDefault(JsonMemberDefault jsonMemberDefault) {
        this.jsonMemberDefault = jsonMemberDefault;
    }

    public High getHigh() {
        return high;
    }

    public void setHigh(High high) {
        this.high = high;
    }

    public Maxres getMaxres() {
        return maxres;
    }

    public void setMaxres(Maxres maxres) {
        this.maxres = maxres;
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    @Override
    public String toString() {
        return
                "Thumbnails{" +
                        "standard = '" + standard + '\'' +
                        ",default = '" + jsonMemberDefault + '\'' +
                        ",high = '" + high + '\'' +
                        ",maxres = '" + maxres + '\'' +
                        ",medium = '" + medium + '\'' +
                        "}";
    }
}