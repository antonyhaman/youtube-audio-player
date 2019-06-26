package com.github.kotvertolet.youtubeaudioplayer.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface YoutubeSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(YoutubeSongDto ytModelDto);

    @Update
    void update(YoutubeSongDto ytModelDto);

    @Delete
    void delete(YoutubeSongDto ytModelDto);

    @Query("DELETE FROM yt_data WHERE videoId=:videoId")
    void deleteById(String videoId);

    @Query("SELECT * FROM yt_data WHERE videoId =:id")
    YoutubeSongDto getByVideoId(String id);

    @Query("SELECT * FROM yt_data WHERE videoId =:id")
    Flowable<YoutubeSongDto> getByVideoIdRx(String id);

    @Query("SELECT * FROM yt_data")
    List<YoutubeSongDto> getAll();

    @Query("SELECT * FROM yt_data ORDER BY id DESC LIMIT :numberOfRows")
    List<YoutubeSongDto> getLastRows(int numberOfRows);

    @Query("SELECT * FROM yt_data WHERE lastPlayedTimestamp > 0 ORDER BY lastPlayedTimestamp DESC LIMIT :numberOfRows")
    List<YoutubeSongDto> getLastPlayed(int numberOfRows);

    @Query("SELECT * FROM yt_data WHERE lastPlayedTimestamp > 0 ORDER BY lastPlayedTimestamp DESC LIMIT :numberOfRows")
    Flowable<List<YoutubeSongDto>> getLastPlayedRx(int numberOfRows);

    @Query("SELECT * FROM yt_data")
    Flowable<List<YoutubeSongDto>> getAllRx();

    @Query("SELECT * FROM yt_data WHERE videoId IN (:videoIds)")
    List<YoutubeSongDto> getAllByVideoIds(String[] videoIds);

    @Query("SELECT * FROM yt_data WHERE videoId IN (:videoIds)")
    Flowable<List<YoutubeSongDto>> getAllByIdRx(String[] videoIds);

    @Transaction
    @Query("SELECT * FROM yt_data WHERE videoId IN (SELECT songId FROM playlist_songs WHERE playlistId=:playlistId)")
    List<YoutubeSongDto> getSongsByPlaylistId(long playlistId);

    @Transaction
    @Query("SELECT * FROM yt_data WHERE videoId IN (SELECT songId FROM playlist_songs WHERE playlistId=:playlistId)")
    Flowable<List<YoutubeSongDto>> getSongsByPlaylistIdRx(long playlistId);
}