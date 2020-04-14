package com.github.kotvertolet.youtubeaudioplayer.db.dao;

import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistSongDto;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Flowable;

@Dao
public interface PlaylistSongsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(PlaylistSongDto playlistSongDto);

    @Update
    void update(PlaylistSongDto playlistSongDto);

    @Delete
    void delete(PlaylistSongDto playlistSongDto);

    @Query("DELETE FROM playlist_songs WHERE playlistId =:playlistId AND songId=:songId ")
    void deleteByPlaylistAndSongId(long playlistId, String songId);

    @Query("DELETE FROM playlist_songs WHERE playlistId =:playlistId")
    void deleteAllByPlaylistId(long playlistId);

//    @Query("SELECT * FROM PlaylistSongDto WHERE playlistId =:id")
//    Flowable<List<String>> getSongsIdsByPlaylistId(long id);

    @Query("SELECT * FROM playlist_songs")
    List<PlaylistSongDto> getAll();

    @Query("SELECT * FROM playlist_songs")
    Flowable<List<PlaylistSongDto>> getAllRx();

    @Query("SELECT songId FROM playlist_songs WHERE playlistId=:playlistId")
    String[] getAllVideoIdsByPlaylistId(long playlistId);
}
