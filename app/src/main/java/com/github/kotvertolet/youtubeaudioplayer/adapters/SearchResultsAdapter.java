package com.github.kotvertolet.youtubeaudioplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.custom.view.CustomRecyclerView;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.jakewharton.rxbinding.view.RxView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchResultsAdapter extends CustomRecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<YoutubeSongDto> currentAdapterData;
    private WeakReference<MainActivityContract.Presenter> presenter;
    private int adapterPosition;

    public SearchResultsAdapter(WeakReference<MainActivityContract.Presenter> presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        YoutubeSongDto item = currentAdapterData.get(position);
        String url = item.getThumbnail();
        Glide.with(holder.itemView).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .into(holder.imRepositoryImage);
        holder.tvVideoLength.setText(item.getDuration());
        holder.tvVideoTitle.setText(item.getTitle());
        holder.tvVideoAuthor.setText(item.getAuthor());
        holder.tvViewCount.setText(item.getViewCount());
        holder.tvLikesCount.setText(item.getLikeCount());
        holder.tvDislikesCount.setText(item.getDislikeCount());
        holder.songDto = item;
    }

    @Override
    public int getItemCount() {
        if (currentAdapterData == null)
            return 0;
        return currentAdapterData.size();
    }

    public void replaceData(List<YoutubeSongDto> newAdapterData) {
        currentAdapterData = newAdapterData;
        notifyDataSetChanged();
    }

    public void addData(List<YoutubeSongDto> newAdapterData) {
        currentAdapterData.addAll(newAdapterData);
        notifyDataSetChanged();
    }

    public int getAdapterPosition() {
        return adapterPosition;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ImageView imRepositoryImage;
        TextView tvVideoLength;
        TextView tvVideoTitle;
        TextView tvVideoAuthor;
        TextView tvViewCount;
        TextView tvLikesCount;
        TextView tvDislikesCount;
        ImageView ivPopupButton;
        YoutubeSongDto songDto;

        ViewHolder(View itemView) {
            super(itemView);
            imRepositoryImage = itemView.findViewById(R.id.iv_song_thumb);
            tvVideoLength = itemView.findViewById(R.id.tv_song_length);
            tvVideoTitle = itemView.findViewById(R.id.tv_player_song_title);
            tvVideoAuthor = itemView.findViewById(R.id.tv_channel_title);
            tvViewCount = itemView.findViewById(R.id.tv_views_count);
            tvLikesCount = itemView.findViewById(R.id.tv_likes_count);
            tvDislikesCount = itemView.findViewById(R.id.tv_dislikes_count);
            ivPopupButton = itemView.findViewById(R.id.iv_popup_button);

            RxView.clicks(ivPopupButton).throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> addMenu(itemView));

            RxView.clicks(itemView).throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(aVoid -> {
                        PlaylistWithSongs playlistWithSongs = new PlaylistWithSongs();
                        playlistWithSongs.setPlaylist(new PlaylistDto(Constants.DEFAULT_PLAYLIST_ID));
                        playlistWithSongs.setSongs(currentAdapterData);
                        presenter.get().preparePlaybackQueueAndPlay(playlistWithSongs, getAdapterPosition());
                    });
            itemView.setOnLongClickListener(this);
        }

        private void addMenu(View itemView) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), ivPopupButton);
            popupMenu.inflate(R.menu.song_item_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        presenter.get().addToPlaylist(songDto);
                        return true;
                    default:
                        return false;
                }

            });
            popupMenu.show();
        }

        @Override
        public boolean onLongClick(View v) {
            adapterPosition = getAdapterPosition();
            v.showContextMenu();
            return true;
        }
    }
}
