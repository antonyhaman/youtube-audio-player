package com.github.kotvertolet.youtubeaudioplayer.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.adapters.PlaylistEditorAdapter;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.AllPlaylistsAndSongsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.db.AppDatabase;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistSongDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.fragments.dialogs.PlaylistCreationDialog;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jakewharton.rxbinding.view.RxView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PlaylistEditingFragment extends Fragment {

    public final static String TAG = "PLAYLIST_EDITOR_DIALOG";
    private AppDatabase db;
    private WeakReference<Context> context;
    private YoutubeSongDto songForPlaylist;
    private FloatingActionButton fabOk;
    private FloatingActionButton fabMenu;
    //private FloatingActionButton fabAddToPlaylist;
    private FloatingActionButton fabCreatePlaylist;
    private FloatingActionButton fabRemovePlaylist;
    private CompositeDisposable disposables;
    private PlaylistEditorAdapter playlistAdapter;

    private boolean isFabOpen = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("Playlists");
        return inflater.inflate(R.layout.layout_playlist_editing_fragment, container, false);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fabOk = view.findViewById(R.id.fab_ok);
        fabMenu = view.findViewById(R.id.fab_menu);
        //fabAddToPlaylist = view.findViewById(R.id.fab_add_to_playlist);
        fabCreatePlaylist = view.findViewById(R.id.fab_create_playlist);
        fabRemovePlaylist = view.findViewById(R.id.fab_remove_playlist);
        fabMenu.setOnClickListener(v -> {
            if (!isFabOpen) showFABMenu();
            else closeFABMenu();
        });
//        rlAddNewPlaylistBlock = v.findViewById(R.id.rl_add_new_playlist_block);
//        rlRemovePlaylistBlock = v.findViewById(R.id.rl_remove_playlist_block);
        db = App.getInstance().getDatabase();
        disposables = new CompositeDisposable();
        songForPlaylist = getArguments().getParcelable(Constants.BUNDLE_NEW_SONG_FOR_PLAYLIST);
        RecyclerView rvPlaylists = view.findViewById(R.id.rl_playlists);
        playlistAdapter = new PlaylistEditorAdapter(context);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(context.get()));
        rvPlaylists.setAdapter(playlistAdapter);
        rvPlaylists.addItemDecoration(new DividerItemDecoration(context.get(), LinearLayoutManager.VERTICAL));

        // Getting playlists with songs, put them into live data and then observe changes
        disposables.add(db.playlistDao().getAllRx().observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io()).subscribe(playlistDtos -> {
                    List<PlaylistWithSongs> playlistDataList = new ArrayList<>();
                    for (PlaylistDto playlistDto : playlistDtos) {
                        List<YoutubeSongDto> youtubeSongDtos = db.youtubeSongDao()
                                .getSongsByPlaylistId(playlistDto.getPlaylistId());
                        playlistDataList.add(new PlaylistWithSongs(playlistDto, youtubeSongDtos));

                    }
                    AllPlaylistsAndSongsViewModel.getInstance().getData().postValue(playlistDataList);
                }));

        // Observe playlist live data and update UI
        AllPlaylistsAndSongsViewModel.getInstance().getData()
                .observe(this, playlistDtos ->
                        disposables.add(Observable.just(playlistDtos)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(data -> playlistAdapter.replaceData(data))));
    }

    @Override
    public void onStart() {
        RxView.clicks(fabOk).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(oVoid -> {
            boolean[] playlistItemsCheckboxState = playlistAdapter.getPlaylistItemsCheckboxState();
            List<PlaylistWithSongs> playlistWithSongsLiveData = AllPlaylistsAndSongsViewModel.getInstance().getData().getValue();
            PlaylistWithSongs playlistToAddNewSong;

            for (int i = 0; i < playlistItemsCheckboxState.length; i++) {
                if (playlistItemsCheckboxState[i]) {
                    playlistToAddNewSong = playlistWithSongsLiveData.get(i);
                    long playlistId = playlistToAddNewSong.getPlaylist().getPlaylistId();
                    PlaylistSongDto newPlaylistSong = new PlaylistSongDto(playlistId, songForPlaylist.getVideoId());

                    AsyncTask.execute(() -> {
                        // Check if song is already in DB
                        YoutubeSongDto songFromDb = db.youtubeSongDao().getByVideoId(songForPlaylist.getVideoId());
                        if (songFromDb == null) {
                            // Saving song in the db
                            newPlaylistSong.setId(db.youtubeSongDao().insert(songForPlaylist));
                        }
                        // Saving song with relation to the playlist
                        db.playlistSongsDao().insert(newPlaylistSong);
                    });
                    getActivity().onBackPressed();
                }
            }
        });

        RxView.clicks(fabCreatePlaylist).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(oVoid -> {
                    PlaylistCreationDialog dialog = new PlaylistCreationDialog();
                    dialog.show(((AppCompatActivity) context.get()).getSupportFragmentManager(), "PLAYLIST_CREATION_DIALOG");
                });

        RxView.clicks(fabRemovePlaylist).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(oVoid -> {
                    boolean[] playlistItemsCheckboxState = playlistAdapter.getPlaylistItemsCheckboxState();
                    int size = playlistItemsCheckboxState.length;
                    for (int i = 0; i < size; i++) {
                        if (playlistItemsCheckboxState[i]) {
                            AllPlaylistsAndSongsViewModel playlistViewModel = AllPlaylistsAndSongsViewModel.getInstance();
                            List<PlaylistWithSongs> playlistDtos = playlistViewModel.getData().getValue();
                            long playlistId = playlistDtos.get(i).getPlaylist().getPlaylistId();
                            //Remove playlist from the db
                            playlistItemsCheckboxState[i] = false;
                            AsyncTask.execute(() -> {
                                db.playlistDao().deleteById(playlistId);
                                db.playlistSongsDao().deleteAllByPlaylistId(playlistId);
                            });
                        }
                    }
                });
        super.onStart();
    }

    // Copypasted https://mobikul.com/expandable-floating-action-button-fab-menu/
    private void showFABMenu() {
        isFabOpen = true;
        fabCreatePlaylist.animate().translationY(-getResources().getDimension(R.dimen.standard_70));
        //fabCreateEditPlaylist.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
        fabRemovePlaylist.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
    }

    private void closeFABMenu() {
        isFabOpen = false;
        fabCreatePlaylist.animate().translationY(0);
        //fabCreateEditPlaylist.animate().translationY(0);
        fabRemovePlaylist.animate().translationY(0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = new WeakReference<>(context);
        super.onAttach(context);
    }
}
