package com.github.kotvertolet.youtubeaudioplayer.tasks;

import android.os.AsyncTask;

import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.AllPlaylistsAndSongsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.db.AppDatabase;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.ArrayList;
import java.util.List;

public class LoadPlaylistsAsyncTask extends AsyncTask<Void, Void, List<PlaylistWithSongs>> {

    private AppDatabase db;

    public LoadPlaylistsAsyncTask(AppDatabase database) {
        db = database;
    }

    @Override
    protected List<PlaylistWithSongs> doInBackground(Void... voids) {
        List<PlaylistDto> playlistDtos = db.playlistDao().getAll();

        List<PlaylistWithSongs> playlistDataList = new ArrayList<>();
        for (PlaylistDto playlistDto : playlistDtos) {
            List<YoutubeSongDto> youtubeSongDtos = db.youtubeSongDao().getSongsByPlaylistId(playlistDto.getPlaylistId());
            playlistDataList.add(new PlaylistWithSongs(playlistDto, youtubeSongDtos));

        }
        return playlistDataList;
    }

    @Override
    protected void onPostExecute(List<PlaylistWithSongs> playlistData) {
        super.onPostExecute(playlistData);
        AllPlaylistsAndSongsViewModel.getInstance().getData().setValue(playlistData);
    }
}
