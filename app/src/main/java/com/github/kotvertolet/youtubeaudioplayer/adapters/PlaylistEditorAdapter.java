package com.github.kotvertolet.youtubeaudioplayer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.fragments.dialogs.PlaylistCreationDialog;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistEditorAdapter extends RecyclerView.Adapter<PlaylistEditorAdapter.ViewHolder> {

    private WeakReference<Context> context;
    private List<PlaylistWithSongs> data;
    private boolean[] checkboxesState;


    public PlaylistEditorAdapter(WeakReference<Context> contextWeakReference) {
        context = contextWeakReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_playlist_edit_item, parent, false);
        return new PlaylistEditorAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistWithSongs playlist = data.get(position);
        if (playlist != null) {
            holder.playlistWithSongs = playlist;
            holder.tvPlaylistName.setText(playlist.getPlaylist().getPlaylistName());
            holder.tvSongsNumber.setText(String.format("Songs: %s", String.valueOf(playlist.getSongs().size())));
            holder.cbSelectPlaylist.setChecked(checkboxesState[position]);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        } else return data.size();
    }

    public void replaceData(List<PlaylistWithSongs> playlists) {
        data = playlists;
        checkboxesState = new boolean[data.size()];
        notifyDataSetChanged();
    }

    public boolean[] getPlaylistItemsCheckboxState() {
        return checkboxesState;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CheckBox cbSelectPlaylist;
        TextView tvPlaylistName;
        TextView tvSongsNumber;
        PlaylistWithSongs playlistWithSongs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaylistName = itemView.findViewById(R.id.tv_playlist_name);
            tvSongsNumber = itemView.findViewById(R.id.tv_songs_count);
            cbSelectPlaylist = itemView.findViewById(R.id.cb_select_playlist);
            cbSelectPlaylist.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            boolean isChecked;
            if (v.getId() == cbSelectPlaylist.getId()) {
                isChecked = cbSelectPlaylist.isChecked();
            } else {
                isChecked = !cbSelectPlaylist.isChecked();
                cbSelectPlaylist.setChecked(isChecked);
            }
            checkboxesState[getAdapterPosition()] = isChecked;
        }

        @Override
        public boolean onLongClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.BUNDLE_PLAYLISTS, playlistWithSongs.getPlaylist());
            PlaylistCreationDialog playlistCreationDialog = new PlaylistCreationDialog();
            playlistCreationDialog.setArguments(bundle);
            playlistCreationDialog.show(((AppCompatActivity) context.get()).getSupportFragmentManager(), "PLAYLIST_CREATION_DIALOG");
            return false;
        }
    }
}
