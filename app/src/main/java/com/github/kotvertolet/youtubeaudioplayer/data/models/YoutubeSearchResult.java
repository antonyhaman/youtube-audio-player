package com.github.kotvertolet.youtubeaudioplayer.data.models;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.List;

public class YoutubeSearchResult {

    private final List<YoutubeSongDto> songs;
    private final String query;
    private final String nextPageToken;

    public YoutubeSearchResult(List<YoutubeSongDto> songs, String query, String nextPageToken) {
        this.songs = songs;
        this.query = query;
        this.nextPageToken = nextPageToken;
    }

    public List<YoutubeSongDto> getSongs() {
        return songs;
    }

    public String getQuery() {
        return query;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
