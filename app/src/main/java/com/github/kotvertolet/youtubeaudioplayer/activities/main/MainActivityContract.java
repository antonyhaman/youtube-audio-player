package com.github.kotvertolet.youtubeaudioplayer.activities.main;

import com.github.kotvertolet.youtubeaudioplayer.activities.BasePresenter;
import com.github.kotvertolet.youtubeaudioplayer.activities.BaseView;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.data.models.SearchSuggestionsResponse;
import com.github.kotvertolet.youtubeaudioplayer.data.models.YoutubeSearchResult;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;

import java.util.List;

import io.reactivex.Observable;


public interface MainActivityContract {

    interface View extends BaseView<Presenter> {

        void showToast(String message, int length);

        void showToast(int resId, int length);

        void showLoadingIndicator(boolean show);

        void showRecommendations();

        void showSearchResults(YoutubeSearchResult data);

        void showPlaylistEditingFragment(YoutubeSongDto songDto);

        void initPlayerSlider(YoutubeSongDto data);

        void setPlayerPlayingState(boolean isPlaying);
    }

    interface Presenter extends BasePresenter {

        void startExoPlayerService();

        void prepareAudioStreamAndPlay(YoutubeSongDto model);

        void playPreparedStream(YoutubeSongDto data);

        boolean searchYoutubeFirstPage(String searchQuery);

        boolean searchYoutubeNextPage(String searchQuery, String nextPageToken);

        Observable<SearchSuggestionsResponse> getSearchSuggestions(String query);

        void preparePlaybackQueueAndPlay(PlaylistWithSongs playlistWithSongs, int position);

        void playPlaylistItem(int position);

        void playAgain();

        void playNextPlaylistItem();

        void playPreviousPlaylistItem();

        void playRandom();

        void addToPlaylist(YoutubeSongDto videoDataDto);

    }
}
