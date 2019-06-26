package com.github.kotvertolet.youtubeaudioplayer.data;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.List;

public class PlaylistWithSongs {

    private PlaylistDto playlist;
    private List<YoutubeSongDto> songs;

    public PlaylistWithSongs() {
    }

    public PlaylistWithSongs(PlaylistDto playlist, List<YoutubeSongDto> songs) {
        this.playlist = playlist;
        this.songs = songs;
    }

    public PlaylistDto getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistDto playlist) {
        this.playlist = playlist;
    }

    public List<YoutubeSongDto> getSongs() {
        return songs;
    }

    public void setSongs(List<YoutubeSongDto> songs) {
        this.songs = songs;
    }
}
