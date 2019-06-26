package com.github.kotvertolet.youtubeaudioplayer.data;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.List;

public class PlaylistData {

    private PlaylistDto playlistDto;
    private List<YoutubeSongDto> youtubeSongDtoList;

    public PlaylistData() {
    }

    public PlaylistData(PlaylistDto playlistDto, List<YoutubeSongDto> youtubeSongDtoList) {
        this.playlistDto = playlistDto;
        this.youtubeSongDtoList = youtubeSongDtoList;
    }

    public PlaylistDto getPlaylistDto() {
        return playlistDto;
    }

    public void setPlaylistDto(PlaylistDto playlistDto) {
        this.playlistDto = playlistDto;
    }

    public List<YoutubeSongDto> getSongs() {
        return youtubeSongDtoList;
    }

    public void setYoutubeSongDtoList(List<YoutubeSongDto> youtubeSongDtoList) {
        this.youtubeSongDtoList = youtubeSongDtoList;
    }
}
