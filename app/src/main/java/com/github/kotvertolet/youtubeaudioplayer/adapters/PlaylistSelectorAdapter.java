package com.github.kotvertolet.youtubeaudioplayer.adapters;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlaylistSelectorAdapter extends RecyclerView.Adapter<PlaylistSelectorAdapter.ViewHolder> {

    private List<PlaylistWithSongs> data;
    private MainActivityContract.Presenter presenter;
    private Dialog dialog;

    public PlaylistSelectorAdapter(MainActivityContract.Presenter presenter, Dialog dialog) {
        this.presenter = presenter;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public PlaylistSelectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_playlist_picker_item, parent, false);
        return new PlaylistSelectorAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSelectorAdapter.ViewHolder holder, int position) {
        PlaylistWithSongs playlist = data.get(position);
        holder.tvPlaylistName.setText(playlist.getPlaylist().getPlaylistName());
        List<YoutubeSongDto> songsList = playlist.getSongs();
        holder.tvSongsNumber.setText(String.format("Songs: %s", songsList.size()));
        if (songsList.size() == 0) {
            holder.ivDummyThumb.setVisibility(View.VISIBLE);
        } else if (songsList.size() < 4) {
            Glide.with(holder.itemView).asBitmap().load(songsList.get(0).getThumbnail())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivDummyThumb);
            holder.ivDummyThumb.setVisibility(View.VISIBLE);
        } else {
            holder.ivDummyThumb.setVisibility(View.GONE);
            for (int i = 0; i < songsList.size() && i <= 3; i++) {
                switch (i) {
                    case 0:
                        Glide.with(holder.itemView).asBitmap().load(songsList.get(i).getThumbnail())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.ivThumb1);
                        break;
                    case 1:
                        Glide.with(holder.itemView).asBitmap().load(songsList.get(i).getThumbnail())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.ivThumb2);
                        break;
                    case 2:
                        Glide.with(holder.itemView).asBitmap().load(songsList.get(i).getThumbnail())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.ivThumb3);
                        break;
                    case 3:
                        Glide.with(holder.itemView).asBitmap().load(songsList.get(i).getThumbnail())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(holder.ivThumb4);
                        break;
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        } else return data.size();
    }

    public void replaceData(List<PlaylistWithSongs> playlistsWithSongs) {
        data = playlistsWithSongs;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPlaylistName;
        TextView tvSongsNumber;
        private ImageView ivThumb1;
        private ImageView ivThumb2;
        private ImageView ivThumb3;
        private ImageView ivThumb4;
        private ImageView ivDummyThumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlaylistName = itemView.findViewById(R.id.tv_playlist_name);
            tvSongsNumber = itemView.findViewById(R.id.tv_songs_count);
            ivThumb1 = itemView.findViewById(R.id.iv_thumb_1);
            ivThumb2 = itemView.findViewById(R.id.iv_thumb_2);
            ivThumb3 = itemView.findViewById(R.id.iv_thumb_3);
            ivThumb4 = itemView.findViewById(R.id.iv_thumb_4);
            ivDummyThumb = itemView.findViewById(R.id.iv_thumb_dummy);

            RxView.clicks(itemView).throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(oVoid -> {
                        presenter.preparePlaybackQueueAndPlay(data.get(getAdapterPosition()), 0);
                        dialog.dismiss();
                    });
        }
    }
}
