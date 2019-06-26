package com.github.kotvertolet.youtubeaudioplayer.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface PlaylistDao {

    @Insert
    long insert(PlaylistDto playlistDto);

    @Update
    void update(PlaylistDto playlistDto);

    @Delete
    void delete(PlaylistDto playlistDto);

    @Query("DELETE FROM playlist_data WHERE playlistId = :playlistId")
    void deleteById(long playlistId);

    @Query("SELECT * FROM playlist_data WHERE playlistId =:id")
    PlaylistDto getById(long id);

    @Query("SELECT * FROM playlist_data WHERE playlistName =:playlistName")
    PlaylistDto getByName(String playlistName);

    @Query("SELECT * FROM playlist_data")
    List<PlaylistDto> getAll();

    @Query("SELECT * FROM playlist_data")
    Flowable<List<PlaylistDto>> getAllRx();

}


