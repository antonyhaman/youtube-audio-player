//package com.example.android.datafrominternet.fragments.dialogs;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.RelativeLayout;
//
//import com.example.android.datafrominternet.App;
//import com.example.android.datafrominternet.R;
//import com.example.android.datafrominternet.adapters.PlaylistEditorAdapter;
//import com.example.android.datafrominternet.data.PlaylistWithSongs;
//import com.example.android.datafrominternet.data.liveData.AllPlaylistsAndSongsViewModel;
//import com.example.android.datafrominternet.db.AppDatabase;
//import com.example.android.datafrominternet.db.dto.PlaylistDto;
//import com.example.android.datafrominternet.db.dto.PlaylistSongDto;
//import com.example.android.datafrominternet.db.dto.YoutubeSongDto;
//import com.example.android.datafrominternet.utilities.common.Constants;
//import com.jakewharton.rxbinding.view.RxView;
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.DialogFragment;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;
//
//public class PlaylistEditorDialogFragment extends DialogFragment {
//
//    public final static String TAG = "PLAYLIST_EDITOR_DIALOG";
//    private AppDatabase db;
//    private WeakReference<Context> context;
//    private YoutubeSongDto songForPlaylist;
//    private Button btOk;
//    private RelativeLayout rlAddNewPlaylistBlock;
//    private RelativeLayout rlRemovePlaylistBlock;
//    private CompositeDisposable disposables;
//    private PlaylistEditorAdapter playlistAdapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getDialog().setTitle("Playlists");
//        getDialog().setCancelable(true);
//        View v = inflater.inflate(R.layout.layout_playlist_edit_dialog, container);
//        btOk = v.findViewById(R.id.bt_playlist_picker_ok);
//        rlAddNewPlaylistBlock = v.findViewById(R.id.rl_add_new_playlist_block);
//        rlRemovePlaylistBlock = v.findViewById(R.id.rl_remove_playlist_block);
//        db = App.getInstance().getDatabase();
//        disposables = new CompositeDisposable();
//        return v;
//    }
//
//    @SuppressLint("CheckResult")
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        songForPlaylist = getArguments().getParcelable(Constants.BUNDLE_NEW_SONG_FOR_PLAYLIST);
//        RecyclerView rvPlaylists = view.findViewById(R.id.rl_playlists);
//        playlistAdapter = new PlaylistEditorAdapter();
//        rvPlaylists.setLayoutManager(new LinearLayoutManager(context.get()));
//        rvPlaylists.setAdapter(playlistAdapter);
//        rvPlaylists.addItemDecoration(new DividerItemDecoration(context.get(), LinearLayoutManager.VERTICAL));
//
//        // Getting playlists with songs, put them into live data and then observe changes
//        disposables.add(db.playlistDao().getAllRx().observeOn(Schedulers.newThread())
//                .subscribeOn(Schedulers.io()).subscribe(playlistDtos -> {
//                    List<PlaylistWithSongs> playlistDataList = new ArrayList<>();
//                    for (PlaylistDto playlistDto : playlistDtos) {
//                        List<YoutubeSongDto> youtubeSongDtos = db.youtubeSongDao()
//                                .getSongsByPlaylistId(playlistDto.getPlaylistId());
//                        playlistDataList.add(new PlaylistWithSongs(playlistDto, youtubeSongDtos));
//
//                    }
//                    AllPlaylistsAndSongsViewModel.getInstance().getPlaylist().postValue(playlistDataList);
//                }));
//
//        // Observe playlist live data and update UI
//        AllPlaylistsAndSongsViewModel.getInstance().getPlaylist()
//                .observe(this, playlistDtos ->
//                        disposables.add(Observable.just(playlistDtos)
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(data -> playlistAdapter.replaceData(data))));
//    }
//
//    @Override
//    public void onStart() {
//        RxView.clicks(btOk).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(oVoid -> {
//            boolean[] playlistItemsCheckboxState = playlistAdapter.getPlaylistItemsCheckboxState();
//            List<PlaylistWithSongs> playlistWithSongsLiveData = AllPlaylistsAndSongsViewModel.getInstance().getPlaylist().getValue();
//            PlaylistWithSongs playlistToAddNewSong;
//            for (int i = 0; i < playlistItemsCheckboxState.length; i++) {
//                if (playlistItemsCheckboxState[i]) {
//                    playlistToAddNewSong = playlistWithSongsLiveData.get(i);
//                    long playlistId = playlistToAddNewSong.getPlaylist().getPlaylistId();
//                    PlaylistSongDto newPlaylistSong = new PlaylistSongDto(playlistId, songForPlaylist.getVideoId());
//
//                    AsyncTask.execute(() -> {
//                        // Check if song is already in DB
//                        YoutubeSongDto songFromDb = db.youtubeSongDao().getByVideoId(songForPlaylist.getVideoId());
//                        if (songFromDb == null) {
//                            // Saving song in the db
//                            newPlaylistSong.setId(db.youtubeSongDao().insert(songForPlaylist));
//                        }
//                        // Saving song with relation to the playlist
//                        db.playlistSongsDao().insert(newPlaylistSong);
//                    });
//                }
//
//            }
//            getDialog().dismiss();
//        });
//
//        RxView.clicks(rlAddNewPlaylistBlock).throttleFirst(500, TimeUnit.MILLISECONDS)
//                .subscribe(oVoid -> {
//                    PlaylistCreationDialog dialog = new PlaylistCreationDialog();
//                    dialog.show(((AppCompatActivity) context.get()).getSupportFragmentManager(), "PLAYLIST_CREATION_DIALOG");
//                });
//
//        RxView.clicks(rlRemovePlaylistBlock).throttleFirst(500, TimeUnit.MILLISECONDS)
//                .subscribe(oVoid -> {
//                    boolean[] playlistItemsCheckboxState = playlistAdapter.getPlaylistItemsCheckboxState();
//                    int size = playlistItemsCheckboxState.length;
//                    for (int i = 0; i < size; i++) {
//                        if (playlistItemsCheckboxState[i]) {
//                            AllPlaylistsAndSongsViewModel playlistViewModel = AllPlaylistsAndSongsViewModel.getInstance();
//                            List<PlaylistWithSongs> playlistDtos = playlistViewModel.getPlaylist().getValue();
//                            long playlistId = playlistDtos.get(i).getPlaylist().getPlaylistId();
//                            //Remove playlist from the db
//                            AsyncTask.execute(() -> {
//                                db.playlistDao().deleteById(playlistId);
//                                db.playlistSongsDao().deleteAllByPlaylistId(playlistId);
//                            });
//                            playlistItemsCheckboxState[i] = false;
//                        }
//                    }
//                });
//        super.onStart();
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        this.context = new WeakReference<>(context);
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        disposables.dispose();
//    }
//}
