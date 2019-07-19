package com.github.kotvertolet.youtubeaudioplayer.adapters;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;

import java.util.List;

public class PlaybackQueueAdapter extends RecyclerView.Adapter<PlaybackQueueAdapter.ViewHolder> {

    private MainActivityContract.Presenter presenter;
    private List<YoutubeSongDto> songs;
    private PlaylistWithSongs playlistWithSongs;

    public PlaybackQueueAdapter(MainActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public PlaybackQueueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_search_item, parent, false);
        return new PlaybackQueueAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YoutubeSongDto item = songs.get(position);
        String url = item.getThumbnail();
        Glide.with(holder.itemView).load(url)
                .circleCrop()
                .into(holder.imRepositoryImage);
        holder.tvVideoLength.setText(item.getDuration());
        holder.tvVideoTitle.setText(item.getTitle());
        holder.tvVideoAuthor.setText(item.getAuthor());
        holder.tvViewCount.setText(item.getViewCount());
        holder.tvLikesCount.setText(item.getLikeCount());
        holder.tvDislikesCount.setText(item.getDislikeCount());
        holder.song = item;
    }

    @Override
    public int getItemCount() {
        if (songs == null) {
            return 0;
        }
        return songs.size();
    }

    public void setData(PlaylistWithSongs playlistWithSongs) {
        this.playlistWithSongs = playlistWithSongs;
        this.songs = playlistWithSongs.getSongs();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        ImageView imRepositoryImage;
        TextView tvVideoLength;
        TextView tvVideoTitle;
        TextView tvVideoAuthor;
        TextView tvViewCount;
        TextView tvLikesCount;
        TextView tvDislikesCount;
        ImageView ivMenuButton;
        YoutubeSongDto song;

        public ViewHolder(View itemView) {
            super(itemView);
            imRepositoryImage = itemView.findViewById(R.id.iv_song_thumb);
            tvVideoLength = itemView.findViewById(R.id.tv_song_length);
            tvVideoTitle = itemView.findViewById(R.id.tv_player_song_title);
            tvVideoAuthor = itemView.findViewById(R.id.tv_channel_title);
            tvViewCount = itemView.findViewById(R.id.tv_views_count);
            tvLikesCount = itemView.findViewById(R.id.tv_likes_count);
            tvDislikesCount = itemView.findViewById(R.id.tv_dislikes_count);
            ivMenuButton = itemView.findViewById(R.id.iv_popup_button);
            ivMenuButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private void addMenu(View itemView) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), ivMenuButton);
            if (playlistWithSongs.getPlaylist().getPlaylistId() == Constants.DEFAULT_PLAYLIST_ID) {
                popupMenu.inflate(R.menu.default_playlist_menu);
            } else popupMenu.inflate(R.menu.playlist_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        presenter.addToPlaylist(song);
                        return true;
                    case R.id.remove_from_playlist:
                        AsyncTask.execute(() -> App.getInstance().getDatabase().playlistSongsDao()
                                .deleteByPlaylistAndSongId(playlistWithSongs.getPlaylist().getPlaylistId(), song.getVideoId()));
                        presenter.playNextPlaylistItem();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }

        @Override
        public boolean onLongClick(View v) {
            addMenu(v);
            return true;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == itemView.getId()) {
                presenter.playPlaylistItem(getAdapterPosition());
            } else if (id == ivMenuButton.getId()) {
                addMenu(itemView);
            }
        }
    }
}
