package com.github.kotvertolet.youtubeaudioplayer.utilities;

import android.content.SharedPreferences;

import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.PlaylistsWithSongsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import androidx.lifecycle.MutableLiveData;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PREFERENCE_REPEAT;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PREFERENCE_SHUFFLE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.REPEAT_MODE_REPEAT_ALL;

public class PlaylistWrapper {
    private MutableLiveData<PlaylistWithSongs> playlist;
    private MutableLiveData<Integer> playlistPosition;
    private SharedPreferences sharedPreferences;

    public PlaylistWrapper(SharedPreferences sharedPreferences) {
        PlaylistsWithSongsViewModel playlistsWithSongsViewModel = PlaylistsWithSongsViewModel.getInstance();
        playlist = playlistsWithSongsViewModel.getPlaylist();
        playlistPosition = playlistsWithSongsViewModel.getPlaylistPosition();
        this.sharedPreferences = sharedPreferences;
    }

    public YoutubeSongDto getCurrentSong() {
        return playlist.getValue().getSongs().get(playlistPosition.getValue());
    }

    public YoutubeSongDto getNextSong() {
        if (sharedPreferences.getBoolean(PREFERENCE_SHUFFLE, false)) {
            return getRandomSong();
        } else {
            int newPosition = incrementPlaylistPosition(1);
            int lastSongIndex = playlist.getValue().getSongs().size() - 1;
            if (newPosition > lastSongIndex) {
                if (sharedPreferences.getInt(PREFERENCE_REPEAT, 0) == REPEAT_MODE_REPEAT_ALL) {
                    // Resetting playlist position
                    playlistPosition.setValue(0);
                    return playlist.getValue().getSongs().get(0);
                } else {
                    // Playlist is ended and repeat is off
                    return null;
                }
            } else return playlist.getValue().getSongs().get(newPosition);
        }
    }

    public YoutubeSongDto getPreviousSong() {
        int newPosition = incrementPlaylistPosition(-1);
        if (newPosition < 0) {
            int lastSongIndex = playlist.getValue().getSongs().size() - 1;
            playlistPosition.setValue(lastSongIndex);
            return playlist.getValue().getSongs().get(lastSongIndex);
        } else return playlist.getValue().getSongs().get(newPosition);
    }

    public YoutubeSongDto getSongByPosition(int position) {
        playlistPosition.setValue(position);
        return playlist.getValue().getSongs().get(position);
    }

    public YoutubeSongDto getRandomSong() {
        int randomPosition;
        do randomPosition = getRandomPlaylistPosition();
        while (randomPosition == playlistPosition.getValue());
        playlistPosition.setValue(randomPosition);
        return playlist.getValue().getSongs().get(randomPosition);
    }

    private int getRandomPlaylistPosition() {
        return (int) (0 + Math.random() * (playlist.getValue().getSongs().size() - 1));
    }

    private int incrementPlaylistPosition(int increment) {
        int newPosition = playlistPosition.getValue() + increment;
        playlistPosition.setValue(newPosition);
        return newPosition;
    }
}
