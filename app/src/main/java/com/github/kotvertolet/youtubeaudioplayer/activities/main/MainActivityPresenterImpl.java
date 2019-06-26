package com.github.kotvertolet.youtubeaudioplayer.activities.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.custom.CachingTasksManager;
import com.github.kotvertolet.youtubeaudioplayer.custom.exceptions.UserFriendly;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.data.dataSource.RemoteDataSource;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.PlaylistsWithSongsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.RecommendationsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.data.models.SearchSuggestionsResponse;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.services.ExoPlayerService;
import com.github.kotvertolet.youtubeaudioplayer.services.PlayerAction;
import com.github.kotvertolet.youtubeaudioplayer.tasks.AudioStreamExtractionAsyncTask;
import com.github.kotvertolet.youtubeaudioplayer.tasks.VideoSearchAsyncTask;
import com.github.kotvertolet.youtubeaudioplayer.utilities.AudioStreamsUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.PlaylistWrapper;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import io.reactivex.Observable;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_CHANGE_STATE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.APP_PREFERENCES;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYER_STATE_CODE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.RECOMMENDATIONS_RECENT;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.DialogUtils.callSimpleDialog;


public class MainActivityPresenterImpl implements MainActivityContract.Presenter {

    private WeakReference<MainActivity> context;
    private WeakReference<MainActivityContract.View> view;
    private RemoteDataSource dataSource;
    private CommonUtils utils;
    private AudioStreamsUtils audioStreamsUtils;
    private PlaylistWrapper playlistWrapper;
    private SimpleCache simpleCache;
    private CachingTasksManager cachingTasksManager;

    public MainActivityPresenterImpl(MainActivity context, MainActivityContract.View viewContract) {
        this.context = new WeakReference<>(context);
        viewContract.setPresenter(this);
        view = new WeakReference<>(viewContract);
        dataSource = RemoteDataSource.getInstance();
        utils = new CommonUtils(context);
        audioStreamsUtils = new AudioStreamsUtils();
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        playlistWrapper = new PlaylistWrapper(sharedPreferences);
        simpleCache = App.getInstance().getPlayerCache();
        cachingTasksManager = App.getInstance().getCachingTasksManager();
    }

    @Override
    public <T extends UserFriendly> void handleException(T exception) {
        // Hide spinner if it visible
        view.get().showLoadingIndicator(false);

        if (exception.getThrowable() != null) {
            exception.getThrowable().printStackTrace();
        }
        DialogInterface.OnClickListener listener = (dialog, which) -> dialog.dismiss();
        utils.createAlertDialog(R.string.error, exception.getUserErrorMessage(),
                true, R.string.button_ok, listener, 0, null).show();
    }

    @Override
    public void handleException(Exception exception) {
        exception.printStackTrace();

        // Hide spinner if it visible
        view.get().showLoadingIndicator(false);
        DialogInterface.OnClickListener listener = (dialog, which) -> dialog.dismiss();
        utils.createAlertDialog(R.string.error, R.string.generic_error_message,
                true, R.string.button_ok, listener, 0, null).show();
    }

    @Override
    public void startExoPlayerService() {
        context.get().startExoPlayerService();
    }

    @Override
    public void prepareAudioStreamAndPlay(YoutubeSongDto songData) {
        String url = songData.getStreamUrl();
        // Check if song is cached
        Set<String> cacheKeys = simpleCache.getKeys();
        // Song may be cached partially if caching task was interrupted,
        // due to some error or if user closed the app before song was fully cached
        if (songData.getStreamUrl() != null && cacheKeys.contains(url)) {
            if (audioStreamsUtils.isSongFullyCached(songData)) {
                playPreparedStream(songData);
                addSongToRecentsList(songData);
            }
            // Song may be still caching but if not we deleting the unfinished cache
            else if (!cachingTasksManager.hasTask(songData.getVideoId())) {
                CacheUtil.remove(simpleCache, url);
                checkInternetAndStartExtractionTask(songData);
            }
        } else checkInternetAndStartExtractionTask(songData);

    }

    private void addSongToRecentsList(YoutubeSongDto songData) {
        MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> recommendationsViewModel =
                RecommendationsViewModel.getInstance().getData();
        // Create RECENTS list if there is no any
        Map<String, LinkedList<YoutubeSongDto>> recommendationsMap = recommendationsViewModel.getValue();
        if (!recommendationsMap.containsKey(RECOMMENDATIONS_RECENT)) {
            recommendationsMap.put(RECOMMENDATIONS_RECENT, new LinkedList<>());
        }
        songData.setLastPlayedTimestamp(System.currentTimeMillis());
        App.getInstance().getDatabase().youtubeSongDao().insert(songData);

//        LinkedList<YoutubeSongDto> youtubeSongDtos = recommendationsMap.get(RECOMMENDATIONS_RECENT);
//        youtubeSongDtos.addFirst(songData);
//        recommendationsViewModel.setValue(recommendationsMap);
    }

    @Override
    public void playPreparedStream(YoutubeSongDto songData) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!utils.isServiceRunning(ExoPlayerService.class)) {
                    startExoPlayerService();
                    handler.postDelayed(this, 100);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.START);
                    //bundle.putString(Constants.EXTRA_SONG, songData);
                    bundle.putParcelable(Constants.EXTRA_SONG, songData);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_CHANGE_STATE, bundle);
                    // Preparing player UI
                    view.get().initPlayerSlider(songData);
                }
            }
        });
        songData.setLastPlayedTimestamp(System.currentTimeMillis());
        App.getInstance().getDatabase().youtubeSongDao().insert(songData);

//        // Create RECENTS list if there is no any
//        MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> recommendationsViewModel =
//                RecommendationsViewModel.getInstance().getData();
//        Map<String, LinkedList<YoutubeSongDto>> recommendationsMap = recommendationsViewModel.getValue();
//        if (!recommendationsMap.containsKey(RECOMMENDATIONS_RECENT)) {
//            recommendationsMap.put(RECOMMENDATIONS_RECENT, new LinkedList<>());
//        }
//        // Add song to RECENTS if it's not there already
//        LinkedList<YoutubeSongDto> recentList = recommendationsMap.get(RECOMMENDATIONS_RECENT);
//        if (!recommendationsMap.get(RECOMMENDATIONS_RECENT).contains(songData)) {
//            recentList.addFirst(songData);
//        }
//        recommendationsMap.put(RECOMMENDATIONS_RECENT, recentList);
//        recommendationsViewModel.setValue(recommendationsMap);
    }

    @Override
    public boolean makeYoutubeSearch(String searchQuery) {
        if (searchQuery.length() == 0) {
            callSimpleDialog(context.get(), R.string.empty_search_query_error_message, R.string.error);
            return false;
        } else if (!utils.isNetworkAvailable()) {
            callSimpleDialog(context.get(), R.string.network_error_message, R.string.error);
            return false;
        }
        view.get().showLoadingIndicator(true);

        new VideoSearchAsyncTask(this, view, utils).execute(searchQuery);
        return true;
    }

    @Override
    public Observable<SearchSuggestionsResponse> getSearchSuggestions(String query) {
        return dataSource.getSuggestionsRx(query);
    }

    @Override
    public void preparePlaybackQueueAndPlay(PlaylistWithSongs playlistWithSongs, int position) {
        PlaylistsWithSongsViewModel playlistsWithSongsViewModel = PlaylistsWithSongsViewModel.getInstance();
        MutableLiveData<PlaylistWithSongs> playlistWithSongsMutableLiveData = playlistsWithSongsViewModel.getPlaylist();
        playlistWithSongsMutableLiveData.setValue(playlistWithSongs);
        playlistsWithSongsViewModel.getPlaylistPosition().setValue(position);

        List<YoutubeSongDto> playlistSongs = playlistWithSongsMutableLiveData.getValue().getSongs();
        if (playlistSongs.size() == 0) {
            view.get().showToast("Playlist is empty", Toast.LENGTH_SHORT);
        } else {
            if (playlistSongs.size() > position) {
                prepareAudioStreamAndPlay(playlistSongs.get(position));
            } else {
                throw new IndexOutOfBoundsException(String.format("Playlist size was: %s but required position is: %s", playlistSongs.size(), position));
            }
        }
    }

    @Override
    public void playPlaylistItem(int position) {
        prepareAudioStreamAndPlay(playlistWrapper.getSongByPosition(position));
    }

    @Override
    public void playAgain() {
        prepareAudioStreamAndPlay(playlistWrapper.getCurrentSong());
    }

    @Override
    public void playNextPlaylistItem() {
        YoutubeSongDto songDto = playlistWrapper.getNextSong();
        if (songDto != null) {
            prepareAudioStreamAndPlay(songDto);
        } else {
            Log.i(this.getClass().getSimpleName(), "Playlist has ended");
        }
    }

    @Override
    public void playPreviousPlaylistItem() {
        prepareAudioStreamAndPlay(playlistWrapper.getPreviousSong());
    }

    @Override
    public void playRandom() {
        prepareAudioStreamAndPlay(playlistWrapper.getRandomSong());
    }

    @Override
    public void addToPlaylist(YoutubeSongDto videoDataDto) {
        view.get().showPlaylistEditingFragment(videoDataDto);
    }

    private void checkInternetAndStartExtractionTask(YoutubeSongDto songData) {
        if (utils.isNetworkAvailable()) {
            new AudioStreamExtractionAsyncTask(this, view, audioStreamsUtils, songData)
                    .execute(songData.getVideoId());
        } else {
            DialogInterface.OnClickListener positiveCallback = (dialog, which) -> {
                if (utils.isNetworkAvailable()) {
                    new AudioStreamExtractionAsyncTask(this, view, audioStreamsUtils, songData)
                            .execute(songData.getVideoId());
                } else {
                    checkInternetAndStartExtractionTask(songData);
                }
            };
            DialogInterface.OnClickListener negativeCallback = (dialog, which) -> dialog.dismiss();
            utils.createAlertDialog(R.string.no_connection_error_message_title, R.string.network_error_message,
                    false, R.string.try_again_message, positiveCallback, R.string.button_cancel, negativeCallback).show();
        }
    }
}
