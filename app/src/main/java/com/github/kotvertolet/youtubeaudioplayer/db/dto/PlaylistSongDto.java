package com.github.kotvertolet.youtubeaudioplayer.db.dto;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist_songs")
public class PlaylistSongDto {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    private long id;
    private long playlistId;
    private String songId;

    public PlaylistSongDto(long playlistId, String songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}

