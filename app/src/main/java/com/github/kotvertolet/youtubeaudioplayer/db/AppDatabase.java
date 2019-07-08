package com.github.kotvertolet.youtubeaudioplayer.db;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.github.kotvertolet.youtubeaudioplayer.db.dao.PlaylistDao;
import com.github.kotvertolet.youtubeaudioplayer.db.dao.PlaylistSongsDao;
import com.github.kotvertolet.youtubeaudioplayer.db.dao.YoutubeSongDao;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistSongDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

@Database(entities = {YoutubeSongDto.class, PlaylistDto.class, PlaylistSongDto.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "YtData.db")
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            ContentValues values = new ContentValues();
                            values.put("playlistId", 0);
                            values.put("playlistName", "New playlist");
                            db.insert("playlist_data", OnConflictStrategy.IGNORE, values);
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract YoutubeSongDao youtubeSongDao();

    public abstract PlaylistDao playlistDao();

    public abstract PlaylistSongsDao playlistSongsDao();

}