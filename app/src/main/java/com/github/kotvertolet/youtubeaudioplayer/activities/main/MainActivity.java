package com.github.kotvertolet.youtubeaudioplayer.activities.main;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.BaseColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.adapters.PlaybackQueueAdapter;
import com.github.kotvertolet.youtubeaudioplayer.data.PlaylistWithSongs;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.AllPlaylistsAndSongsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.PlaylistsWithSongsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.RecommendationsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.data.liveData.SearchResultsViewModel;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.PlaylistDto;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.fragments.PlaylistEditingFragment;
import com.github.kotvertolet.youtubeaudioplayer.fragments.RecommendationsFragment;
import com.github.kotvertolet.youtubeaudioplayer.fragments.SearchResultsFragment;
import com.github.kotvertolet.youtubeaudioplayer.fragments.SettingsFragment;
import com.github.kotvertolet.youtubeaudioplayer.fragments.dialogs.PlaylistPickerDialogFragment;
import com.github.kotvertolet.youtubeaudioplayer.services.ExoPlayerService;
import com.github.kotvertolet.youtubeaudioplayer.services.PlayerAction;
import com.github.kotvertolet.youtubeaudioplayer.tasks.LoadPlaylistsAsyncTask;
import com.github.kotvertolet.youtubeaudioplayer.utilities.ReceiverManager;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.exoplayer2.Player;
import com.jakewharton.rxbinding.view.RxView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_CHANGE_STATE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_STATE_CHANGED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYLIST_NAVIGATION;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.APP_PREFERENCES;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYBACK_STATUS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYER_STATE_CODE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_TRACK_DURATION;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_TRACK_PROGRESS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYBACK_PROGRESS_CHANGED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_ERROR;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_ERROR_THROWABLE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PREFERENCE_REPEAT;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PREFERENCE_SHUFFLE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.RECOMMENDATIONS_RECENT;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.REPEAT_MODE_NO_REPEAT;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.REPEAT_MODE_REPEAT_ALL;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.REPEAT_MODE_REPEAT_ONE;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED;
import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View, SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = "YT_PLAYER_DEBUG";
    private static final AtomicInteger backClicksCount = new AtomicInteger();
    private final FragmentManager fm = getSupportFragmentManager();
    private final Handler mHandler = new Handler();
    private final Runnable backClickRunnable = () -> backClicksCount.set(0);
    private boolean isPlaybackEnded = false;
    private SharedPreferences sharedPreferences;
    private String pendingSearchQuery = "";
    private SearchView searchView;
    private ProgressBar loadingIndicator;
    private MainActivityContract.Presenter presenter;
    private CommonUtils utils;
    private PlaybackQueueAdapter playbackQueueAdapter;
    private RelativeLayout rlSeeKBarBlock;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private AppCompatImageView ivPlayerThumb;
    private AppCompatSeekBar sbPlayerProgress;
    private AppCompatImageButton playerButtonPlayPause;
    private TextView tvSongTitle;
    private TextView tvSecondarySongTitle;
    private TextView tvSongChannel;
    private TextView tvSecondarySongChannel;
    private AppCompatImageButton acibRepeatDisabled;
    private AppCompatImageButton acibRepeatAll;
    private AppCompatImageButton acibRepeatOne;
    private AppCompatImageButton acibShuffleDisabled;
    private AppCompatImageButton acibShuffleEnabled;
    private TextView tvTrackCurrentTime;
    private TextView tvTrackDuration;
    private AppCompatImageView ivChevron;
    private PlayerStateBroadcastReceiver playerStateBroadcastReceiver;
    private PlaylistNavigationBroadcastReceiver playlistNavigationBroadcastReceiver;
    private View llControls;
    private ReceiverManager receiverManager;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        presenter = new MainActivityPresenterImpl(this, this);
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);
        presenter = new MainActivityPresenterImpl(this, this);
        utils = new CommonUtils(this);
        receiverManager = ReceiverManager.getInstance(this);
        registerReceiver(playerStateBroadcastReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        showRecommendations();
        initPlayerSlidingPanel();
        Log.d(TAG, "Main activity has been created");
        registerReceivers();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        stopService(new Intent(this, ExoPlayerService.class));
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(backClickRunnable);
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        Log.d(TAG, "Main activity has been destroyed");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.menu_action_search));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final List<String> suggestions = new ArrayList<>();

        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                // Assigning value to a temp field in order avoid new suggestion fetch
                pendingSearchQuery = suggestions.get(position);
                searchView.setQuery(pendingSearchQuery, true);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    // Clearing pending search query as it will be executed
                    pendingSearchQuery = null;
                    // Do this trick to hide suggestions list
                    ((AutoCompleteTextView) searchView.findViewById(R.id.search_src_text)).dismissDropDown();
                    return presenter.makeYoutubeSearch(searchView.getQuery().toString());
                }
                return false;
            }

            @SuppressLint("CheckResult")
            @Override
            public boolean onQueryTextChange(String query) {
                // Checking if query long enough
                if (query.length() > 3 && !query.equals(pendingSearchQuery)) {
                    presenter.getSearchSuggestions(query)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(suggestionsResponse -> {
                                        List<String> tempList = Arrays.asList(suggestionsResponse.getSuggestions());
                                        // Limiting the suggestions to only 5
                                        tempList = tempList.subList(0, 4);
                                        // Clearing the old suggestions before adding the new ones
                                        suggestions.clear();
                                        suggestions.addAll(tempList);
                                        String[] columns = {BaseColumns._ID,
                                                SearchManager.SUGGEST_COLUMN_TEXT_1,
                                                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                                        };
                                        MatrixCursor cursor = new MatrixCursor(columns);
                                        for (int i = 0; i < suggestions.size(); i++) {
                                            String[] tempArr = {Integer.toString(i), suggestions.get(i), suggestions.get(i)};
                                            cursor.addRow(tempArr);
                                        }
                                        suggestionAdapter.swapCursor(cursor);
                                    },
                                    error -> Log.e(getClass().getSimpleName(), error.getMessage()));
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        resetActionBar();
        return super.onSupportNavigateUp();
    }

    private void resetActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        Intent intent;
        switch (itemThatWasClickedId) {
            case R.id.action_settings:
                SettingsFragment fragment = new SettingsFragment();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName());
                ft.addToBackStack(null);
                ft.commit();
                //TODO: think about it
                minimizePlayer();
                return true;
            case R.id.action_clear_db:
                AsyncTask.execute(() -> App.getInstance().getDatabase().clearAllTables());
                MutableLiveData<Map<String, LinkedList<YoutubeSongDto>>> recommendationsViewModel =
                        RecommendationsViewModel.getInstance().getData();
                Map<String, LinkedList<YoutubeSongDto>> recommendationsMap = recommendationsViewModel.getValue();
                if (recommendationsMap.containsKey(RECOMMENDATIONS_RECENT)) {
                    recommendationsMap.remove(RECOMMENDATIONS_RECENT);
                    recommendationsViewModel.setValue(recommendationsMap);
                }
                showToast("Db is cleared", Toast.LENGTH_SHORT);
                return true;
            case R.id.action_playlists:
                if (AllPlaylistsAndSongsViewModel.getInstance().getData().getValue().size() > 0) {
                    new PlaylistPickerDialogFragment().show(getSupportFragmentManager(), "playlist_picker");
                } else {
                    DialogInterface.OnClickListener listener = (dialog, which)
                            -> dialog.dismiss();
                    utils.createAlertDialog("Error",
                            "There are no playlists to display", true,
                            R.string.button_ok, listener, 0, null).show();
                }
                return true;
            case R.id.action_close_app:
                intent = new Intent(this, ExoPlayerService.class);
                stopService(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showSearchResults(List<YoutubeSongDto> data) {
        showLoadingIndicator(false);
        minimizePlayer();
        SearchResultsFragment addedFragment = (SearchResultsFragment) fm.findFragmentByTag(SearchResultsFragment.class.getSimpleName());
        if (addedFragment == null) {
            SearchResultsFragment fragment = new SearchResultsFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(null);
            ft.commit();
        }
        SearchResultsViewModel.getInstance().getData().setValue(data);
    }

    @Override
    public void showPlaylistEditingFragment(YoutubeSongDto songDto) {
        PlaylistEditingFragment addedFragment = (PlaylistEditingFragment) fm.findFragmentByTag(PlaylistEditingFragment.class.getSimpleName());
        if (addedFragment == null) {
            PlaylistEditingFragment fragment = new PlaylistEditingFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE_NEW_SONG_FOR_PLAYLIST, songDto);
            fragment.setArguments(bundle);
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, fragment, fragment.getClass().getSimpleName());
            ft.addToBackStack(null);
            ft.commit();
            //TODO: think about it
            minimizePlayer();
        }
    }

    @Override
    public void initPlayerSlider(YoutubeSongDto data) {
        //TODO: Get rid of trims, make in db
        tvSongTitle.setText(data.getTitle().trim());
        tvSecondarySongTitle.setText(data.getTitle().trim());
        tvSongChannel.setText(data.getAuthor().trim());
        tvSecondarySongChannel.setText(data.getAuthor());
        Glide.with(this).asBitmap().load(data.getThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(300, 300)
                .circleCrop()
                .into(ivPlayerThumb);
        //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        SlidingUpPanelLayout.PanelState panelState = slidingUpPanelLayout.getPanelState();
        if (panelState == SlidingUpPanelLayout.PanelState.HIDDEN) {
            slidingUpPanelLayout.setPanelState(COLLAPSED);
        }
    }

    @Override
    public void setPlayerPlayingState(boolean isPlaying) {
        if (isPlaying) {
            playerButtonPlayPause.setImageResource(R.drawable.ic_pause_black_40dp);
        } else {
            playerButtonPlayPause.setImageResource(R.drawable.ic_player_play_black_40dp);
        }
    }

    @Override
    public void showLoadingIndicator(boolean show) {
        if (!show) {
            loadingIndicator.setVisibility(View.GONE);
        } else {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    @Override
    public void showToast(int resId, int length) {
        Toast.makeText(this, getString(resId), length).show();
    }

    @Override
    public void showRecommendations() {
        RecommendationsFragment fragment = new RecommendationsFragment();
        fm.beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        resetActionBar();
        minimizePlayer();

        // Collapsing search view
//        if (!searchView.isIconified()) {
//            searchView.onActionViewCollapsed();
//        }

        if (fm.getBackStackEntryCount() == 0) {
            if (backClicksCount.get() == 0) {
                showToast("Press back button once more to close the app", Toast.LENGTH_SHORT);
                backClicksCount.getAndIncrement();
                mHandler.postDelayed(backClickRunnable, 300);
            } else {
                finishAffinity();
            }
        } else super.onBackPressed();
    }

    private void maximizePlayer() {
        SlidingUpPanelLayout.PanelState state = slidingUpPanelLayout.getPanelState();
        if (state.equals(COLLAPSED)) {
            slidingUpPanelLayout.setPanelState(EXPANDED);
        }
    }

    private void minimizePlayer() {
        SlidingUpPanelLayout.PanelState state = slidingUpPanelLayout.getPanelState();
        if (state.equals(EXPANDED)) {
            slidingUpPanelLayout.setPanelState(COLLAPSED);
        }
    }

    public ComponentName startExoPlayerService() {
        if (!utils.isServiceRunning(ExoPlayerService.class)) {
            Intent intent = new Intent(this, ExoPlayerService.class);
            componentName = startService(intent);
            Log.d(TAG, "ExoPlayer service start initiated");
        } else Log.d(TAG, "ExoPlayer service is already started");
        return componentName;
    }

    private void registerReceivers() {
        playerStateBroadcastReceiver = new PlayerStateBroadcastReceiver();
        playlistNavigationBroadcastReceiver = new PlaylistNavigationBroadcastReceiver();
        receiverManager.registerLocalReceiver(playerStateBroadcastReceiver, new IntentFilter(ACTION_PLAYER_STATE_CHANGED));
        receiverManager.registerLocalReceiver(playlistNavigationBroadcastReceiver, new IntentFilter(ACTION_PLAYLIST_NAVIGATION));
    }

    private void unregisterReceivers() {
        receiverManager.unregisterReceiver(playerStateBroadcastReceiver);
        receiverManager.unregisterReceiver(playlistNavigationBroadcastReceiver);
    }

    @SuppressLint("CheckResult")
    private void initPlayerSlidingPanel() {
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        RelativeLayout rlSlidingPlayer = findViewById(R.id.rl_sliding_player);
        RelativeLayout rlSongInfo = findViewById(R.id.rl_song_info_block);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        rlSeeKBarBlock = findViewById(R.id.rl_seek_bar_block);
        ivPlayerThumb = findViewById(R.id.iv_player_thumb);
        AppCompatImageButton buttonPlayerBack = findViewById(R.id.ib_player_back);
        playerButtonPlayPause = findViewById(R.id.ib_player_play_pause);
        AppCompatImageButton playerButtonNext = findViewById(R.id.ib_player_next);
        tvSongTitle = findViewById(R.id.tv_player_song_title);
        tvSongTitle.setSelected(true);
        tvSongChannel = findViewById(R.id.tv_channel_title);
        tvSecondarySongTitle = findViewById(R.id.tv_secondary_song_title);
        tvSecondarySongChannel = findViewById(R.id.tv_secondary_channel_title);
        acibRepeatDisabled = findViewById(R.id.acib_repeat_disabled);
        acibRepeatAll = findViewById(R.id.acib_repeat_all_enabled);
        acibRepeatOne = findViewById(R.id.acib_repeat_one_enabled);
        acibShuffleDisabled = findViewById(R.id.acib_shuffle_disabled);
        acibShuffleEnabled = findViewById(R.id.acib_shuffle_enabled);
        sbPlayerProgress = findViewById(R.id.sb_player);
        tvTrackCurrentTime = findViewById(R.id.tv_track_current_time);
        tvTrackDuration = findViewById(R.id.tv_track_duration);
        llControls = findViewById(R.id.ll_player_controls);
        ivChevron = findViewById(R.id.iv_chevron);
        playbackQueueAdapter = new PlaybackQueueAdapter(presenter);
        RecyclerView playlistRecycler = findViewById(R.id.rv_generic);
        playlistRecycler.setAdapter(playbackQueueAdapter);
        playlistRecycler.setLayoutManager(new LinearLayoutManager(this));
        playlistRecycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        setupRepeatButtons();
        setupShuffleButtons();

        // Observing changes in db such as songs added to or removed from playlist and if so updating the playback queue in player
        PlaylistsWithSongsViewModel.getInstance().getPlaylist().observe(this, playlist -> playbackQueueAdapter.setData(playlist));

        AllPlaylistsAndSongsViewModel.getInstance().getData().observe(this, allPlaylists -> {
            MutableLiveData<PlaylistWithSongs> playbackQueueLiveData = PlaylistsWithSongsViewModel.getInstance().getPlaylist();
            PlaylistDto playlistDto = playbackQueueLiveData.getValue().getPlaylist();
            if (playlistDto != null) {
                for (PlaylistWithSongs playlistWithSongs : allPlaylists) {
                    if (playlistWithSongs.getPlaylist().getPlaylistId() == playlistDto.getPlaylistId()) {
                        playbackQueueLiveData.setValue(playlistWithSongs);
                    }
                }
            }
        });

        App.getInstance().getDatabase().playlistSongsDao().getAllRx().observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).subscribe(playlistSongDtos ->
                new LoadPlaylistsAsyncTask(App.getInstance().getDatabase()).execute());


        RxView.clicks(playerButtonPlayPause).throttleFirst(300, TimeUnit.MILLISECONDS)
                .subscribe(oVoid -> {
                    if (isPlaybackEnded) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.PLAY_AGAIN);
                        utils.sendLocalBroadcastMessage(ACTION_PLAYER_CHANGE_STATE, bundle);
                        //presenter.playAgain();
                        Log.i(TAG, "Previous song requested");
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.PAUSE_PLAY);
                        utils.sendLocalBroadcastMessage(ACTION_PLAYER_CHANGE_STATE, bundle);
                    }
                });
        RxView.clicks(buttonPlayerBack).throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(oVoid -> {
                    presenter.playPreviousPlaylistItem();
                    Log.i(TAG, "Previous song requested");
                });
        RxView.clicks(playerButtonNext).throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(oVoid -> {
                    presenter.playNextPlaylistItem();
                    Log.i(TAG, "Next song requested");
                });

        sbPlayerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.CHANGE_PLAYBACK_PROGRESS);
                    bundle.putInt(EXTRA_TRACK_PROGRESS, progress);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_CHANGE_STATE, bundle);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                int controlsMultiplier = 1;
                double thumbnailMultiplier = 1;
                if (displayMetrics.widthPixels > 1080) {
                    controlsMultiplier = 4;
                    thumbnailMultiplier = 1.3;
                }

                transformThumbnail(slideOffset, thumbnailMultiplier);
                transformInfo(slideOffset);
                transformControls(slideOffset, controlsMultiplier);
                //transformChevron(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
            }
        });
    }

    private void setupShuffleButtons() {
        boolean shuffle = sharedPreferences.getBoolean(PREFERENCE_SHUFFLE, false);
        if (shuffle) {
            acibShuffleEnabled.setVisibility(View.VISIBLE);
        } else acibShuffleDisabled.setVisibility(View.VISIBLE);

        acibShuffleDisabled.setOnClickListener(v -> sharedPreferences.edit().putBoolean(PREFERENCE_SHUFFLE, true).apply());
        acibShuffleEnabled.setOnClickListener(v -> sharedPreferences.edit().putBoolean(PREFERENCE_SHUFFLE, false).apply());
    }

    private void setupRepeatButtons() {
        int repeatMode = sharedPreferences.getInt(PREFERENCE_REPEAT, 0);
        switch (repeatMode) {
            case 0:
                acibRepeatDisabled.setVisibility(View.VISIBLE);
                break;
            case 1:
                acibRepeatAll.setVisibility(View.VISIBLE);
                break;
            case 2:
                acibRepeatOne.setVisibility(View.VISIBLE);
                break;
        }

        acibRepeatDisabled.setOnClickListener(v ->
                sharedPreferences.edit().putInt(PREFERENCE_REPEAT, REPEAT_MODE_REPEAT_ALL).apply());
        acibRepeatAll.setOnClickListener(v ->
                sharedPreferences.edit().putInt(PREFERENCE_REPEAT, REPEAT_MODE_REPEAT_ONE).apply());
        acibRepeatOne.setOnClickListener(v ->
                sharedPreferences.edit().putInt(PREFERENCE_REPEAT, REPEAT_MODE_NO_REPEAT).apply());
    }

    private void transformChevron(float slideOffset) {
        ivChevron.setRotation(slideOffset * 90);

    }

    private void transformSeekbarBlock(float slideOffset) {
        if (slideOffset > 0) {
            ViewGroup.LayoutParams viewGrouplLayoutParams = rlSeeKBarBlock.getLayoutParams();
            ViewGroup.MarginLayoutParams viewGroupMargins = (ViewGroup.MarginLayoutParams) viewGrouplLayoutParams;
            float _margin_top_DIFFpx = utils.dpInPx(15);
            int newMarginTop = (int) Math.ceil((slideOffset) * _margin_top_DIFFpx);
            Log.wtf("NEW MARGIN", String.valueOf(newMarginTop));
            viewGroupMargins.topMargin = newMarginTop;
        }
    }

    private void transformThumbnail(float slideOffset, double multiplier) {
        int initialDimensionsInPx = (int) utils.dpInPx(50);
        int initialLeftMargin = (int) (utils.dpInPx(60) * multiplier);
        int initialTopMargin = (int) utils.dpInPx(5);

        int newDimen = (int) Math.ceil((slideOffset) * initialDimensionsInPx + initialDimensionsInPx);
        int newLeftMargin = (int) Math.ceil((slideOffset) * initialLeftMargin + initialTopMargin);
        int newMarginTop = (int) Math.ceil((slideOffset) * initialTopMargin + initialTopMargin);
        RelativeLayout.LayoutParams thumbnailParams = new RelativeLayout.LayoutParams(newDimen, newDimen);

        thumbnailParams.setMargins(newLeftMargin, newMarginTop, 0, 0);
        ivPlayerThumb.setLayoutParams(thumbnailParams);
    }

    private void transformControls(float slideOffset, int multiplier) {
        int initialDimensionsInPx = (int) utils.dpInPx(20) * multiplier;
        int topMarginCoef = (int) (utils.dpInPx(35));
        int initialTopMargin = (int) (utils.dpInPx(10));

        int newTopMargin = (int) Math.ceil((slideOffset) * topMarginCoef + initialTopMargin);
        int newRightMargin = (int) Math.ceil((slideOffset) * initialDimensionsInPx);

        RelativeLayout.LayoutParams controlParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        controlParams.setMargins(0, newTopMargin, newRightMargin, 0);
        controlParams.addRule(RelativeLayout.LEFT_OF, R.id.iv_chevron);
        llControls.setLayoutParams(controlParams);
    }

    private void transformInfo(float offset) {
        float mainAlpha = (float) (1.0 - offset * 2);
        float secondaryAlpha = (float) (offset * 1.4);
        tvSongTitle.setAlpha(mainAlpha);
        tvSongChannel.setAlpha(mainAlpha);
        tvSecondarySongTitle.setAlpha(secondaryAlpha);
        tvSecondarySongChannel.setAlpha(secondaryAlpha);
//        isRepeatSwitcher.setAlpha(secondaryAlpha);
//        isShuffleSwitcher.setAlpha(secondaryAlpha);
    }

    public MainActivityContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void setPresenter(MainActivityContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREFERENCE_REPEAT:
                switch (sharedPreferences.getInt(key, 0)) {
                    case 0:
                        acibRepeatOne.setVisibility(View.GONE);
                        acibRepeatDisabled.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        acibRepeatDisabled.setVisibility(View.GONE);
                        acibRepeatAll.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        acibRepeatAll.setVisibility(View.GONE);
                        acibRepeatOne.setVisibility(View.VISIBLE);
                        break;
                }
                break;
            case PREFERENCE_SHUFFLE:
                if (sharedPreferences.getBoolean(key, false)) {
                    acibShuffleEnabled.setVisibility(View.VISIBLE);
                    acibShuffleDisabled.setVisibility(View.GONE);
                } else {
                    acibShuffleEnabled.setVisibility(View.GONE);
                    acibShuffleDisabled.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public class PlayerStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int playerStateCode = intent.getIntExtra(EXTRA_PLAYER_STATE_CODE, -1);
            Log.i(this.getClass().getSimpleName(), String.format("Intent with action %s received", intent.getAction()));
            switch (playerStateCode) {
                case Player.STATE_READY:
                    isPlaybackEnded = false;

                    showLoadingIndicator(false);
                    int trackDuration = intent.getIntExtra(EXTRA_TRACK_DURATION, 0);
                    boolean isPlaying = intent.getBooleanExtra(EXTRA_PLAYBACK_STATUS, false);
                    setPlayerPlayingState(isPlaying);
                    sbPlayerProgress.setMax(trackDuration);
                    tvTrackDuration.setText(utils.convertMillsIntoTimeString(trackDuration));
                    Log.i(this.getClass().getSimpleName(), "Playback state ready, code: " + playerStateCode);
                    break;
                case Player.STATE_ENDED:
                    isPlaybackEnded = true;

                    setPlayerPlayingState(false);
                    sbPlayerProgress.setProgress(0);
                    int repeatMode = sharedPreferences.getInt(PREFERENCE_REPEAT, 0);
                    if (repeatMode == REPEAT_MODE_REPEAT_ONE) {
                        //presenter.playAgain();
                        Bundle bundle = new Bundle();
                        bundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.PLAY_AGAIN);
                        utils.sendLocalBroadcastMessage(ACTION_PLAYER_CHANGE_STATE, bundle);
                    } else {
                        presenter.playNextPlaylistItem();
                    }
                    Log.i(this.getClass().getSimpleName(), "Playback ended, code: " + playerStateCode);
                    break;
                case Player.STATE_BUFFERING:
                    showLoadingIndicator(true);
                    break;
                case PLAYBACK_PROGRESS_CHANGED:
                    int trackCurrentPosition = intent.getIntExtra(EXTRA_TRACK_PROGRESS, -1);
                    sbPlayerProgress.setProgress(trackCurrentPosition);
                    tvTrackCurrentTime.setText(utils.convertMillsIntoTimeString(trackCurrentPosition));
                    Log.i(this.getClass().getSimpleName(), "Progress changed: " + utils.convertMillsIntoTimeString(trackCurrentPosition));
                    break;
                case Player.STATE_IDLE:
                    Log.i(this.getClass().getSimpleName(), "Player is idling, status code: " + playerStateCode);
                    break;
                case PLAYER_ERROR:
                    setPlayerPlayingState(false);
                    sbPlayerProgress.setProgress(0);
                    Exception exception = (Exception) Objects.requireNonNull(intent.getExtras()).getSerializable(PLAYER_ERROR_THROWABLE);
                    presenter.handleException(exception);
                    Log.e(this.getClass().getSimpleName(), "Player error occurred");
                    break;
                default:
                    Log.e(this.getClass().getSimpleName(), "Wrong player state code: " + playerStateCode);
                    break;
            }
        }
    }

    public class PlaylistNavigationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_PLAYER_STATE_CODE, -1);
            Log.i(this.getClass().getSimpleName(), String.format("Intent with action %s received", action));
            switch (action) {
                case PlayerAction.NEXT:
                    presenter.playNextPlaylistItem();
                    Log.i(getClass().getSimpleName(), "Intent with action ");
                    break;
                case PlayerAction.BACK:
                    presenter.playPreviousPlaylistItem();
                    break;
                default:
                    Log.e(getClass().getSimpleName(), "Unknown intent received: \n" + intent.toString());
            }
        }
    }
}
