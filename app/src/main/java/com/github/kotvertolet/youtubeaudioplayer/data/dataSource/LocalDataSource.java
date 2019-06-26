package com.github.kotvertolet.youtubeaudioplayer.data.dataSource;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.db.AppDatabase;
import com.github.kotvertolet.youtubeaudioplayer.db.dao.PlaylistDao;
import com.github.kotvertolet.youtubeaudioplayer.db.dao.PlaylistSongsDao;
import com.github.kotvertolet.youtubeaudioplayer.db.dao.YoutubeSongDao;

public class LocalDataSource {

    private LocalDataSource localDataSource;
    private PlaylistDao playlistDao;
    private PlaylistSongsDao playlistSongsDao;
    private YoutubeSongDao youtubeSongDao;

    private LocalDataSource() {
        AppDatabase appDatabase = App.getInstance().getDatabase();
        playlistDao = appDatabase.playlistDao();
        playlistSongsDao = appDatabase.playlistSongsDao();
        youtubeSongDao = appDatabase.youtubeSongDao();
    }


}
