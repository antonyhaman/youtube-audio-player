package com.github.kotvertolet.youtubeaudioplayer.data.models.youtubeSearch;

import com.google.gson.annotations.SerializedName;

public class SnippetItem {

    @SerializedName("snippet")
    private Snippet snippet;

    @SerializedName("kind")
    private String kind;

    @SerializedName("etag")
    private String etag;

    @SerializedName("id")
    private Id id;

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return
                "SnippetItem{" +
                        "snippet = '" + snippet + '\'' +
                        ",kind = '" + kind + '\'' +
                        ",etag = '" + etag + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}