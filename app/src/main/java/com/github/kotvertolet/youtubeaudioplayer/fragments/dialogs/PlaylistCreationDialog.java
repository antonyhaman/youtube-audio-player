package com.github.kotvertolet.youtubeaudioplayer.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.PlaylistViewModel;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PlaylistCreationDialog extends DialogFragment implements View.OnClickListener {

    private TextInputLayout tilPlaylistName;
    private Button btOk;
    private PlaylistViewModel playlistViewModel;
    private List<String> playlistNames;
    private CompositeDisposable compositeDisposable;
    private PlaylistDto playlistToEdit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_playlist_creation_dialog, container);
        btOk = v.findViewById(R.id.bt_playlist_creation_ok);
        btOk.setOnClickListener(this);
        tilPlaylistName = v.findViewById(R.id.til_playlist_name);
        playlistViewModel = PlaylistViewModel.getInstance();
        playlistNames = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            playlistToEdit = (PlaylistDto) bundle.getSerializable(Constants.BUNDLE_PLAYLISTS);
            tilPlaylistName.getEditText().setText(playlistToEdit.getPlaylistName());
        }
        compositeDisposable.add(App.getInstance().getDatabase().playlistDao().getAllRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playlistDtos -> {
                    for (PlaylistDto playlist : playlistDtos) {
                        playlistNames.add(playlist.getPlaylistName());
                    }
                    playlistViewModel.getData().postValue(playlistDtos);
                }));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
    }

    @Override
    public void onClick(View v) {
        String newPlaylistName = tilPlaylistName.getEditText().getText().toString().trim();
        if (newPlaylistName.isEmpty()) {
            tilPlaylistName.setError(getString(R.string.error_playlist_name_empty));
        } else if (playlistNames.contains(newPlaylistName)) {
            tilPlaylistName.setError(getString(R.string.error_playlist_name_not_unique));
        } else if (playlistToEdit != null && playlistToEdit.getPlaylistName().equals(newPlaylistName)) {
            getDialog().dismiss();
        } else {
            compositeDisposable.add(Observable.just(newPlaylistName).subscribeOn(Schedulers.io())
                    .map(s -> {
                        if (playlistToEdit == null) {
                            PlaylistDto newPlaylist = new PlaylistDto(s);
                            long newPlaylistId = App.getInstance().getDatabase().playlistDao().insert(newPlaylist);
                            newPlaylist.setPlaylistId(newPlaylistId);
                            return newPlaylist;
                        } else {
                            playlistToEdit.setPlaylistName(newPlaylistName);
                            App.getInstance().getDatabase().playlistDao().update(playlistToEdit);
                            return playlistToEdit;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(newPlaylist -> {
                        List<PlaylistDto> playlistDtoList = playlistViewModel.getData().getValue();
                        if (playlistToEdit == null) {
                            playlistDtoList.add(newPlaylist);
                        } else {
                            for (int i = 0; i < playlistDtoList.size(); i++) {
                                PlaylistDto playlistDto = playlistDtoList.get(i);
                                if (playlistDto.getPlaylistId() == playlistToEdit.getPlaylistId()) {
                                    playlistDtoList.remove(i);
                                    playlistDtoList.add(i, playlistToEdit);
                                }
                            }
                        }
                        playlistViewModel.getData().setValue(playlistDtoList);
                        getDialog().dismiss();
                    }));
        }
    }
}
