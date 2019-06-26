package com.github.kotvertolet.youtubeaudioplayer.fragments.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivity;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.adapters.PlaylistSelectorAdapter;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.AllPlaylistsAndSongsViewModel;

import java.lang.ref.WeakReference;

public class PlaylistPickerDialogFragment extends DialogFragment {

    private WeakReference<Context> context;
    private PlaylistSelectorAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setTitle("Playlists");
        getDialog().setCancelable(true);
        View v = inflater.inflate(R.layout.layout_playlist_picker, container);
        return v;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainActivityContract.Presenter presenter = ((MainActivity) context.get()).getPresenter();
        playlistAdapter = new PlaylistSelectorAdapter(presenter, getDialog());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context.get());
        RecyclerView rvPlaylists = view.findViewById(R.id.rl_playlists);
        rvPlaylists.setLayoutManager(linearLayoutManager);
        rvPlaylists.setAdapter(playlistAdapter);
        rvPlaylists.addItemDecoration(new DividerItemDecoration(context.get(), LinearLayoutManager.VERTICAL));
        playlistAdapter.replaceData(AllPlaylistsAndSongsViewModel.getInstance().getData().getValue());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = new WeakReference<>(context);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
