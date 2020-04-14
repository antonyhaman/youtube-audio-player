package com.github.kotvertolet.youtubeaudioplayer.adapters.recommendations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivityContract;
import com.github.kotvertolet.youtubeaudioplayer.custom.view.CustomRecyclerView;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalRecommendationsAdapter extends CustomRecyclerView.Adapter<HorizontalRecommendationsAdapter.ViewHolder> {

    private List<YoutubeSongDto> youtubeVideoData;
    private MainActivityContract.Presenter presenter;

    public HorizontalRecommendationsAdapter(MainActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recommendation_item, parent, false);
        return new HorizontalRecommendationsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        YoutubeSongDto item = youtubeVideoData.get(i);
        String url = item.getThumbnail();
        Glide.with(viewHolder.itemView).load(url)
                //.override(imageWidthPixels, imageHeightPixels)
                .into(viewHolder.imRepositoryImage);
        viewHolder.tvVideoLength.setText(item.getDuration());
        viewHolder.tvVideoTitle.setText(item.getTitle());
        viewHolder.tvVideoAuthor.setText(item.getAuthor());
        viewHolder.tvViewCount.setText(item.getViewCount());
        viewHolder.tvLikesCount.setText(item.getLikeCount());
        viewHolder.tvDislikesCount.setText(item.getDislikeCount());
        viewHolder.model = item;
    }

    @Override
    public int getItemCount() {
        if (youtubeVideoData == null) {
            return 0;
        } else return youtubeVideoData.size();
    }

    public void addData(List<YoutubeSongDto> youtubeModels) {
        if (youtubeVideoData == null) youtubeVideoData = new ArrayList<>();
        youtubeVideoData.addAll(youtubeModels);
        notifyDataSetChanged();
    }

    public void setData(List<YoutubeSongDto> youtubeModels) {
        youtubeVideoData = youtubeModels;
        notifyDataSetChanged();
    }

    public void resetState() {
        if (youtubeVideoData.size() > 0) this.youtubeVideoData = new ArrayList<>();
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
        YoutubeSongDto model;

        public ViewHolder(View itemView) {
            super(itemView);
            imRepositoryImage = itemView.findViewById(R.id.iv_thumbnail);
            tvVideoLength = itemView.findViewById(R.id.tv_duration);
            tvVideoTitle = itemView.findViewById(R.id.tv_player_song_title);
            tvVideoAuthor = itemView.findViewById(R.id.tv_uploader);
            tvViewCount = itemView.findViewById(R.id.tv_views_count);
            tvLikesCount = itemView.findViewById(R.id.tv_likes_count);
            tvDislikesCount = itemView.findViewById(R.id.tv_dislikes_count);
            ivMenuButton = itemView.findViewById(R.id.iv_popup_button);

//            RxView.clicks(ivMenuButton).throttleFirst(500, TimeUnit.MILLISECONDS)
//                    .subscribe(oVoid -> addMenu(itemView));
//
//            RxView.clicks(itemView).throttleFirst(500, TimeUnit.MILLISECONDS)
//                    .subscribe(oVoid -> {
//                        PlaylistWithSongs playlistWithSongs = new PlaylistWithSongs();
//                        playlistWithSongs.setPlaylist(new PlaylistDto(Constants.DEFAULT_PLAYLIST_ID));
//                        playlistWithSongs.setSongs(youtubeVideoData);
//                        presenter.preparePlaybackQueueAndPlay(playlistWithSongs, getAdapterPosition());
//                    });
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            ivMenuButton.setOnClickListener(this);
        }

        private void addMenu(View itemView) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), ivMenuButton);
            popupMenu.inflate(R.menu.song_item_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.add_to_playlist:
                        presenter.addToPlaylist(model);
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
                PlaylistWithSongs playlistWithSongs = new PlaylistWithSongs();
                playlistWithSongs.setPlaylist(new PlaylistDto(Constants.DEFAULT_PLAYLIST_ID));
                playlistWithSongs.setSongs(youtubeVideoData);
                presenter.preparePlaybackQueueAndPlay(playlistWithSongs, getAdapterPosition());
            } else if (id == ivMenuButton.getId()) {
                addMenu(itemView);
            }
        }
    }
}
