package com.github.kotvertolet.youtubeaudioplayer.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.custom.CachingTasksManager;
import com.github.kotvertolet.youtubeaudioplayer.data.NetworkType;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.AudioStreamsUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.ReceiverManager;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_CHANGE_STATE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_STATE_CHANGED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYLIST_NAVIGATION;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYBACK_STATUS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYER_STATE_CODE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_SONG;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_TRACK_DURATION;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_TRACK_PROGRESS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYBACK_PROGRESS_CHANGED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_ERROR;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_ERROR_THROWABLE;

public class ExoPlayerService extends Service {

    private final String USER_AGENT = "^_^";
    private final String TAG = getClass().getSimpleName();
    private PlayerCommandsBroadcastReceiver playerCommandsBroadcastReceiver;
    private SimpleExoPlayer exoPlayer;
    private WifiManager.WifiLock wifiLock;
    private Handler progressHandler;
    private Handler idlingHandler;
    private CommonUtils utils;
    private MediaSource mediaSource;
    private PlayerStateListener playerStateListener;
    private HeadsetStateBroadcastReceiver headsetStateReceiver;
    private ReceiverManager receiverManager;
    private SimpleCache simpleCache;
    private AudioStreamsUtils audioStreamsUtils;
    private CachingTasksManager cacheTaskManager;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        utils = new CommonUtils(getApplicationContext());
        receiverManager = ReceiverManager.getInstance(this);
        App app = App.getInstance();
        simpleCache = app.getPlayerCache();
        audioStreamsUtils = new AudioStreamsUtils();
        cacheTaskManager = app.getCachingTasksManager();
        createPlayer();
        sharedPreferences = app.getSharedPreferences();
        createWifiLock();
        registerReceivers();
        Log.i(this.getClass().getSimpleName(), "Exoplayer service has been created");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            String action = intent.getAction();
            if (action.equals(ACTION_PLAYER_CHANGE_STATE)) {
                playerCommandsBroadcastReceiver.onReceive(this, intent);
            }
        }
        return super.onStartCommand(intent, flags, START_STICKY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cacheTaskManager.stopAllTasks();
        clearPlayerState();
        unregisterReceivers();
        stopService(new Intent(this, PlayerNotificationService.class));
        if (wifiLock.isHeld()) wifiLock.release();
        Log.i(this.getClass().getSimpleName(), "Exoplayer service has been destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startPlaybackProgressUpdateRunnable() {
        if (progressHandler == null && exoPlayer.getPlayWhenReady()) {
            progressHandler = new Handler();
        }
        //Updating Seekbar on UI thread
        progressHandler.post(new Runnable() {
            @Override
            public void run() {
                final int stateCode = exoPlayer.getPlaybackState();
                if (exoPlayer != null && stateCode == Player.STATE_READY || stateCode == Player.STATE_BUFFERING) {
                    Bundle bundle = new Bundle();
                    int trackCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYBACK_PROGRESS_CHANGED);
                    bundle.putInt(EXTRA_TRACK_PROGRESS, trackCurrentPosition);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle);
                    progressHandler.postDelayed(this, 1000);
                } else if (stateCode == Player.STATE_ENDED) {
                    Bundle bundle = new Bundle();
                    int trackCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYBACK_PROGRESS_CHANGED);
                    bundle.putInt(EXTRA_TRACK_PROGRESS, trackCurrentPosition);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle);
                    progressHandler.postDelayed(this, 1000);
                } else if (stateCode == Player.STATE_IDLE) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, Player.STATE_IDLE);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle);
                } else {
                    throw new IllegalStateException("ExoPlayer was dead while progress handler was still going. Player state code was: " + stateCode);
                }
            }
        });
    }

    private void startIdlingRunnable() {
        if (idlingHandler != null) {
            clearPlayerState();
        } else idlingHandler = new Handler();

        idlingHandler.post(new Runnable() {
            @Override
            public void run() {
                final int stateCode = exoPlayer.getPlaybackState();
                if (exoPlayer != null && stateCode == Player.STATE_ENDED) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, Player.STATE_IDLE);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle);
                    idlingHandler.postDelayed(this, 1000);
                } else {
                    throw new IllegalStateException("ExoPlayer was dead while progress handles was still going.");
                }
            }
        });
    }

    private void startPlayback(Uri uri) {
        final int stateCode = exoPlayer.getPlaybackState();
        if (stateCode != Player.STATE_IDLE) {
            clearPlayerState();
            createPlayer();
        }
        mediaSource = prepareCachedMediaSource(uri);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        Log.i(TAG, "Playback started, uri: " + uri.toString());
    }

    private void startPlayback(YoutubeSongDto songDto) {
        final int stateCode = exoPlayer.getPlaybackState();
        if (stateCode != Player.STATE_IDLE) {
            clearPlayerState();
            createPlayer();
        }
        mediaSource = prepareMediaSource(songDto);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        Log.i(TAG, "Playback started, uri: " + songDto.getStreamUrl());
    }

    private void startPlayback(MediaSource mediaSource) {
        final int stateCode = exoPlayer.getPlaybackState();
        if (stateCode != Player.STATE_IDLE) {
            clearPlayerState();
            createPlayer();
        }
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        Log.i(TAG, "Playback restarted with the same media source, tag:  " + mediaSource.getTag());
    }

    private void changePlaybackState() {
        exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
        String playbackState = exoPlayer.getPlayWhenReady() ? "playing" : "not playing";
        Log.i(TAG, "Playback status changed to: " + playbackState);
    }

    private MediaSource prepareMediaSource(YoutubeSongDto songDto) {
        Uri uri = Uri.parse(songDto.getStreamUrl());
        if (audioStreamsUtils.isSongFullyCached(songDto)) {
            return prepareCachedMediaSource(uri);
        } else {
            return prepareSimpleMediaSource(uri, songDto);
        }
    }

    private MediaSource prepareCachedMediaSource(Uri uri) {
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache,
                new DefaultHttpDataSourceFactory(USER_AGENT));
        return new ExtractorMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);
    }

    private MediaSource prepareSimpleMediaSource(Uri uri, YoutubeSongDto songDto) {
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(USER_AGENT);
        if (songDto.getDurationInSeconds() < 1800 && !cacheTaskManager.hasTask(songDto.getVideoId())) {
            if (sharedPreferences.getBoolean(Constants.PREFERENCE_RESTRICT_MOBILE_NETWORK_CACHING, true)) {
                if (utils.getNetworkClass().equals(NetworkType.TYPE_WIFI)) {
                    cacheTaskManager.addTaskAndStart(songDto, uri, simpleCache, dataSourceFactory.createDataSource());
                }
            } else
                cacheTaskManager.addTaskAndStart(songDto, uri, simpleCache, dataSourceFactory.createDataSource());
        }
        return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private void createPlayer() {
        playerStateListener = new PlayerStateListener();
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        exoPlayer.addListener(playerStateListener);
        setAudioFocus();
        Log.i(this.getClass().getSimpleName(), "Exoplayer has been created");
    }

    private void setAudioFocus() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();
        exoPlayer.setAudioAttributes(audioAttributes, true);
    }

    private void clearPlayerState() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.seekTo(0);
            exoPlayer.removeListener(playerStateListener);
            exoPlayer.release();
        }
        stopHandler(progressHandler);
        stopHandler(idlingHandler);
        Log.i(this.getClass().getSimpleName(), "Exoplayer state has been cleared");
    }

    private void createWifiLock() {
        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "YT_PLAYER_LOCK");
        wifiLock.acquire();
    }

    private void registerReceivers() {
        playerCommandsBroadcastReceiver = new PlayerCommandsBroadcastReceiver();
        receiverManager.registerLocalReceiver(playerCommandsBroadcastReceiver, new IntentFilter(ACTION_PLAYER_CHANGE_STATE));

        headsetStateReceiver = new HeadsetStateBroadcastReceiver();
        final IntentFilter headphoneActionsFilter = new IntentFilter();
        for (String action : headsetStateReceiver.HEADPHONE_ACTIONS) {
            headphoneActionsFilter.addAction(action);
        }
        receiverManager.registerGlobalReceiver(headsetStateReceiver, headphoneActionsFilter);
    }

    private void unregisterReceivers() {
        receiverManager.unregisterReceiver(playerCommandsBroadcastReceiver);
        receiverManager.unregisterReceiver(headsetStateReceiver);
    }

    private void startNotificationService(YoutubeSongDto song) {
        if (!utils.isServiceRunning(PlayerNotificationService.class)) {
            Intent intent = new Intent(this, PlayerNotificationService.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_SONG, song);
            intent.putExtra(EXTRA_SONG, bundle);
            startService(intent);
        }
    }

    private void stopHandler(Handler idlingHandler) {
        if (idlingHandler != null) {
            idlingHandler.removeCallbacksAndMessages(null);
        }
    }

    public class PlayerStateListener implements Player.EventListener {

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYER_ERROR);
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    bundle.putSerializable(PLAYER_ERROR_THROWABLE, error.getSourceException());
                    Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    bundle.putSerializable(PLAYER_ERROR_THROWABLE, error.getRendererException());
                    Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    bundle.putSerializable(PLAYER_ERROR_THROWABLE, error.getUnexpectedException());
                    Log.e(TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                    break;
            }
            utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_PLAYER_STATE_CODE, playbackState);
            switch (playbackState) {
                case Player.STATE_ENDED:
                    //Stop playback and return to the start position
                    clearPlayerState();
                    //Todo: Think how to avoid sending this and stick to handler
                    Bundle playbackEndedBundle = new Bundle();
                    playbackEndedBundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYBACK_PROGRESS_CHANGED);
                    playbackEndedBundle.putInt(EXTRA_TRACK_PROGRESS, 0);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, playbackEndedBundle);
                    startIdlingRunnable();
                    Log.i(TAG, "Playback ended!");
                    break;
                case Player.STATE_READY:
                    stopHandler(idlingHandler);
                    Log.i(TAG, "Playback is ready!");
                    bundle.putInt(EXTRA_TRACK_DURATION, (int) (exoPlayer.getDuration() / 1000));
                    bundle.putBoolean(EXTRA_PLAYBACK_STATUS, exoPlayer.getPlayWhenReady());
                    startPlaybackProgressUpdateRunnable();
                    break;
                case Player.STATE_BUFFERING:
                    stopHandler(idlingHandler);
                    Log.i(TAG, "Playback buffering!");
                    break;
                case Player.STATE_IDLE:
                    //startIdlingRunnable();
                    Log.i(TAG, "ExoPlayer idle!");
                    break;
                default:
                    Log.i(TAG, "Playback state is invalid: " + playbackState);
                    break;
            }
            utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle);
        }
    }

    public class PlayerCommandsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra(EXTRA_PLAYER_STATE_CODE, -1);
            Log.i(this.getClass().getSimpleName(), String.format("Intent with action %s received", action));
            switch (action) {
                case PlayerAction.START:
                    //String streamUriStr = intent.getStringExtra(EXTRA_SONG);
                    YoutubeSongDto song = intent.getParcelableExtra(EXTRA_SONG);
                    startNotificationService(song);
                    if (song != null) {
                        //startPlayback(Uri.parse(streamUriStr));
                        startPlayback(song);
                        Log.i(TAG, "Playback initiated");
                    } else {
                        Log.e(TAG, "Empty URI received");
                    }
                    break;
                case PlayerAction.PAUSE_PLAY:
                    changePlaybackState();
                    break;
                case PlayerAction.NEXT:
                    Bundle nextBundle = new Bundle();
                    nextBundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.NEXT);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYLIST_NAVIGATION, nextBundle);
                    break;
                case PlayerAction.BACK:
                    Bundle backBundle = new Bundle();
                    backBundle.putInt(EXTRA_PLAYER_STATE_CODE, PlayerAction.BACK);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYLIST_NAVIGATION, backBundle);
                    break;
                case PlayerAction.CHANGE_PLAYBACK_PROGRESS:
                    int progress = intent.getIntExtra(EXTRA_TRACK_PROGRESS, 0);
                    exoPlayer.seekTo(progress * 1000);
                    break;
                case PlayerAction.PLAY_AGAIN:
                    //clearPlayerState(true);
                    if (exoPlayer.getPlayWhenReady() && exoPlayer.getContentPosition() > 0) {
                        exoPlayer.seekTo(0);
                    } else {
                        startPlayback(mediaSource);
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown intent received: \n" + intent.toString());
            }
        }
    }

    public class HeadsetStateBroadcastReceiver extends BroadcastReceiver {

        private final String[] HEADPHONE_ACTIONS = {
                Intent.ACTION_HEADSET_PLUG,
                "android.bluetooth.headset.action.STATE_CHANGED",
                "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"
        };

        @Override
        public void onReceive(final Context context, final Intent intent) {
            int state = 0;
            // Wired headset monitoring
            if (intent.getAction().equals(HEADPHONE_ACTIONS[0])) {
                state = intent.getIntExtra("state", 0);
            }
            // Bluetooth monitoring
            // Works up to and including Honeycomb
            else if (intent.getAction().equals(HEADPHONE_ACTIONS[1])) {
                state = intent.getIntExtra("android.bluetooth.headset.extra.STATE", 0);
            }
            // Works for Ice Cream Sandwich
            else if (intent.getAction().equals(HEADPHONE_ACTIONS[2])) {
                state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
            }
            if (state == 0 && exoPlayer.getPlayWhenReady()) {
                exoPlayer.setPlayWhenReady(false);
            }
        }
    }
}
