package com.github.kotvertolet.youtubeaudioplayer.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.github.kotvertolet.youtubeaudioplayer.App;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.ExoPlayerUtils;
import com.github.kotvertolet.youtubeaudioplayer.utilities.ReceiverManager;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;

import androidx.annotation.Nullable;

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
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_PAUSED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_RESUMED;

public class ExoPlayerService extends Service {

    private final String TAG = getClass().getSimpleName();
    private PlayerCommandsBroadcastReceiver playerCommandsBroadcastReceiver;
    private SimpleExoPlayer exoPlayer;
    private WifiManager.WifiLock wifiLock;
    private Handler progressHandler;
    private CommonUtils utils;
    private MediaSource mediaSource;
    private PlayerStateListener playerStateListener;
    private HeadsetStateBroadcastReceiver headsetStateReceiver;
    private ReceiverManager receiverManager;
    private ExoPlayerUtils exoPlayerUtils;


    private long playbackPosition = 0;
    private int currentWindow = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        utils = new CommonUtils();
        receiverManager = ReceiverManager.getInstance(this);
        exoPlayerUtils = new ExoPlayerUtils();

        createPlayer();
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
        exoPlayerUtils.stopCacheTasks();
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
            //Updating Seekbar on UI thread
            progressHandler.post(new Runnable() {
                @Override
                public void run() {
                    final int stateCode = exoPlayer.getPlaybackState();
                    if (stateCode == Player.STATE_READY || stateCode == Player.STATE_BUFFERING) {
                        Bundle bundle = new Bundle();
                        int trackCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                        bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYBACK_PROGRESS_CHANGED);
                        bundle.putInt(EXTRA_TRACK_PROGRESS, trackCurrentPosition);
                        utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle, getApplicationContext());
                        progressHandler.postDelayed(this, 1000);
                    } else {
                        throw new IllegalStateException("ExoPlayer was dead while progress handler was still going. Player state code was: " + stateCode);
                    }
                }
            });
        }
    }

    private void startPlayback(Uri uri) {
        final int stateCode = exoPlayer.getPlaybackState();
        if (stateCode != Player.STATE_IDLE) {
            clearPlayerState();
            createPlayer();
        }
        mediaSource = exoPlayerUtils.prepareCachedMediaSource(uri);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        Log.i(TAG, "Playback started, uri: " + uri.toString());
    }

    private void startPlayback(YoutubeSongDto songDto) {
        clearPlayerState();
        createPlayer();
        mediaSource = exoPlayerUtils.prepareMediaSource(songDto);
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
//        Bundle bundle = new Bundle();
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            pausePlay();
//            releasePlayer();
//            bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYER_PAUSED);
//            Log.i(TAG, "Playback status changed to 'paused'");
        } else {
            continuePlay();
//            changePlaybackState(this.playbackPosition);
//            bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYER_RESUMED);
//            Log.i(TAG, "Playback status changed to 'resumed'");
        }
//        utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle, getApplicationContext());
    }

    private void pausePlay() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            Bundle bundle = new Bundle();
            releasePlayer();
            bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYER_PAUSED);
            utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle, getApplicationContext());
            Log.i(TAG, "Playback status changed to 'paused'");
        }
    }

    private void continuePlay() {
        if (mediaSource != null && (exoPlayer == null || !exoPlayer.getPlayWhenReady())) {
            Bundle bundle = new Bundle();
            changePlaybackState(playbackPosition);
            bundle.putInt(EXTRA_PLAYER_STATE_CODE, PLAYER_RESUMED);
            Log.i(TAG, "Playback status changed to 'resumed'");
            utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle, getApplicationContext());
        }
    }

    private void changePlaybackState(long playbackPosition) {
        if (exoPlayer == null) {
            createPlayer();
            exoPlayer.prepare(mediaSource, false, false);
        }
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.removeListener(playerStateListener);
            playerStateListener = null;
            exoPlayerUtils.stopHandlers(progressHandler);
            progressHandler = null;
            exoPlayer.release();
            exoPlayer = null;
            Log.i(TAG, "Exoplayer state has been released");
        } else Log.i(TAG, "Exoplayer is already released");
    }

    private void clearPlayerState() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.seekTo(0);
        }
        Log.i(this.TAG, "Exoplayer state has been cleared");
    }

    private void createPlayer() {
        playerStateListener = new PlayerStateListener();
        exoPlayer = new SimpleExoPlayer.Builder(App.getInstance()).build();
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
        if (!utils.isServiceRunning(PlayerNotificationService.class, getApplicationContext())) {
            Intent intent = new Intent(this, PlayerNotificationService.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_SONG, song);
            intent.putExtra(EXTRA_SONG, bundle);
            startService(intent);
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
            utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle, getApplicationContext());
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Bundle bundle = new Bundle();
            // Sending message to MainActivity with player status
            bundle.putInt(EXTRA_PLAYER_STATE_CODE, playbackState);
            switch (playbackState) {
                case Player.STATE_ENDED:
                    //Stop playback and return to the start position
                    clearPlayerState();
                    releasePlayer();
                    bundle.putBoolean(EXTRA_PLAYBACK_STATUS, false);
                    Log.i(TAG, "Playback ended!");
                    break;
                case Player.STATE_READY:
                    bundle.putInt(EXTRA_TRACK_DURATION, (int) (exoPlayer.getDuration() / 1000));
                    bundle.putBoolean(EXTRA_PLAYBACK_STATUS, exoPlayer.getPlayWhenReady());
                    startPlaybackProgressUpdateRunnable();
                    Log.i(TAG, "Playback is ready!");
                    break;
                case Player.STATE_BUFFERING:
                    Log.i(TAG, "Playback buffering!");
                    break;
                case Player.STATE_IDLE:
                    Log.i(TAG, "ExoPlayer idle!");
                    break;
                default:
                    Log.i(TAG, "Playback state is invalid: " + playbackState);
                    break;
            }
            utils.sendLocalBroadcastMessage(ACTION_PLAYER_STATE_CHANGED, bundle, getApplicationContext());
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
                case PlayerAction.CHANGE_PLAYBACK_PROGRESS:
                    int progress = intent.getIntExtra(EXTRA_TRACK_PROGRESS, 0);
                    changePlaybackState(progress * 1000);
                    Log.i(TAG, "Playback position changed");
                    break;
                case PlayerAction.PLAY_AGAIN:
                    //clearPlayerState(true);
                    changePlaybackState(0);
                    break;
                case PlayerAction.NEXT:
                case PlayerAction.BACK:
                    Bundle bundle = new Bundle();
                    bundle.putInt(EXTRA_PLAYER_STATE_CODE, action);
                    utils.sendLocalBroadcastMessage(ACTION_PLAYLIST_NAVIGATION, bundle, getApplicationContext());
                    break;
                default:
                    Log.e(TAG, "Unknown intent received: \n" + intent.toString());
            }
        }
    }

    public class HeadsetStateBroadcastReceiver extends BroadcastReceiver {
        private static final int STATE_DISCONNECTED  = 0x00000000;

        private final String[] HEADPHONE_ACTIONS = {
                Intent.ACTION_HEADSET_PLUG,
                "android.bluetooth.headset.action.STATE_CHANGED",
                "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED"
        };

        @Override
        public void onReceive(final Context context, final Intent intent) {
            int state = STATE_DISCONNECTED;
            // Wired headset monitoring
            if (intent.getAction().equals(HEADPHONE_ACTIONS[0])) {
                state = intent.getIntExtra("state", STATE_DISCONNECTED);
                Log.i(getClass().getSimpleName(), String.format("Wired headset state change with state: %d", state));
            }
            // Bluetooth monitoring
            // Works up to and including Honeycomb
            else if (intent.getAction().equals(HEADPHONE_ACTIONS[1])) {
                state = intent.getIntExtra("android.bluetooth.headset.extra.STATE", STATE_DISCONNECTED);
                Log.i(getClass().getSimpleName(), String.format("Bluetooth 1 headset state change with state: %d", state));
            }
            // Works for Ice Cream Sandwich
            else if (intent.getAction().equals(HEADPHONE_ACTIONS[2])) {
                state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", STATE_DISCONNECTED);
                Log.i(getClass().getSimpleName(), String.format("Bluetooth 2 headset state change with state: %d", state));
            }
            if (state == STATE_DISCONNECTED) {
//                exoPlayer.setPlayWhenReady(false);
                pausePlay();
            } else {
                continuePlay();
            }
        }
    }
}
