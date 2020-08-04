package com.github.kotvertolet.youtubeaudioplayer.data

import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto

class PlaylistWithSongs {
    var playlist: PlaylistDto? = null
    var songs: List<YoutubeSongDto>? = null

    constructor()
    constructor(playlist: PlaylistDto?, songs: List<YoutubeSongDto>?) {
        this.playlist = playlist
        this.songs = songs
    }

}