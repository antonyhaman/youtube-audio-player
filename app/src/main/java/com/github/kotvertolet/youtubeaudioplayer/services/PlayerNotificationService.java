package com.github.kotvertolet.youtubeaudioplayer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.NotificationTarget;
import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.activities.main.MainActivity;
import com.github.kotvertolet.youtubeaudioplayer.db.dto.YoutubeSongDto;
import com.github.kotvertolet.youtubeaudioplayer.utilities.ReceiverManager;
import com.github.kotvertolet.youtubeaudioplayer.utilities.common.CommonUtils;
import com.google.android.exoplayer2.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_CHANGE_STATE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.ACTION_PLAYER_STATE_CHANGED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYBACK_STATUS;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_PLAYER_STATE_CODE;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.EXTRA_SONG;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYBACK_PROGRESS_CHANGED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_ERROR;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_PAUSED;
import static com.github.kotvertolet.youtubeaudioplayer.utilities.common.Constants.PLAYER_RESUMED;

public class PlayerNotificationService extends Service {

    private final String channelName = PlayerNotificationService.class.getSimpleName();
    private CommonUtils commonUtils = new CommonUtils();
    private final String channelId = commonUtils.createChannelId();
    private boolean isPlaying = true;
    private ReceiverManager receiverManager;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private RemoteViews smallNotificationLayout;
    private RemoteViews bigNotificationLayout;
    private int notificationId;
    private PlayerStateBroadcastReceiver playerStateBroadcastReceiver;
//    private PlayerChangeStateReceiver playerChangeStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        receiverManager = ReceiverManager.getInstance(this);
        notificationId = createID();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        smallNotificationLayout = new RemoteViews(getPackageName(), R.layout.layout_minimized_notification);
        bigNotificationLayout = new RemoteViews(getPackageName(), R.layout.layout_expanded_notification);
        Log.i(getClass().getSimpleName(), "Notification service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
        notificationManager.cancelAll();
        Log.i(getClass().getSimpleName(), "Notification service destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == null) {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            builder = new NotificationCompat.Builder(this, channelId)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setTicker(getResources().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_notification_small)
                    .setContentIntent(contentPendingIntent)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setDeleteIntent(contentPendingIntent)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setPriority(PRIORITY_MAX)
                    .setCustomContentView(smallNotificationLayout)
                    .setCustomBigContentView(bigNotificationLayout);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(channel);
                builder.setCategory(Notification.CATEGORY_SERVICE);
            }

            YoutubeSongDto songDto = intent.getBundleExtra(EXTRA_SONG).getParcelable(EXTRA_SONG);
            setupSongData(songDto, smallNotificationLayout);
            setupSongData(songDto, bigNotificationLayout);
            preparePlayPauseButton();
            prepareNextButton();
            prepareBackButton();
            registerReceivers();

            Notification notification = builder.build();
            // To make notification stay when user clears all notifications
            notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
            startForeground(notificationId, notification);
        } else {
            String action = intent.getAction();
            if (action.equals(ACTION_PLAYER_CHANGE_STATE)) {
                int command = intent.getIntExtra(EXTRA_PLAYER_STATE_CODE, 0);
                switch (command) {
                    case PlayerAction.PAUSE_PLAY:
                        sendPlayPauseBroadcast();
                        break;
                    case PlayerAction.BACK:
                        sendBackBroadcast();
                        break;
                    case PlayerAction.NEXT:
                        sendNextBroadcast();
                        break;
                }
            } else
                Log.e(getClass().getSimpleName(), "Intent with unknown action received: " + action);
        }
        return START_NOT_STICKY;
    }

    private void sendPlayPauseBroadcast() {
        Intent playPauseIntent = new Intent(this, ExoPlayerService.class);
        playPauseIntent.setAction(ACTION_PLAYER_CHANGE_STATE);
        playPauseIntent.putExtra(EXTRA_PLAYER_STATE_CODE, PlayerAction.PAUSE_PLAY);
        startService(playPauseIntent);
    }

    private void sendNextBroadcast() {
        Intent nextIntent = new Intent(this, ExoPlayerService.class);
        nextIntent.setAction(ACTION_PLAYER_CHANGE_STATE);
        nextIntent.putExtra(EXTRA_PLAYER_STATE_CODE, PlayerAction.NEXT);
        startService(nextIntent);
    }

    private void sendBackBroadcast() {
        Intent backIntent = new Intent(this, ExoPlayerService.class);
        backIntent.setAction(ACTION_PLAYER_CHANGE_STATE);
        backIntent.putExtra(EXTRA_PLAYER_STATE_CODE, PlayerAction.BACK);
        startService(backIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
    }

    private void registerReceivers() {
        playerStateBroadcastReceiver = new PlayerStateBroadcastReceiver();
//        playerChangeStateReceiver = new PlayerChangeStateReceiver();
        receiverManager.registerLocalReceiver(playerStateBroadcastReceiver, new IntentFilter(ACTION_PLAYER_STATE_CHANGED));
//        receiverManager.registerLocalReceiver(playerChangeStateReceiver, new IntentFilter(ACTION_PLAYER_CHANGE_STATE));
    }

    private void unregisterReceivers() {
        receiverManager.unregisterReceiver(playerStateBroadcastReceiver);
//        receiverManager.unregisterReceiver(playerChangeStateReceiver);
    }

    private void switchPlayIcon(boolean isPlaying) {
        RemoteViews smallRemoteViews = builder.getContentView();
        RemoteViews bigRemoteViews = builder.getBigContentView();
        if (isPlaying) {
            smallRemoteViews.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_pause_black_40dp);
            bigRemoteViews.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_pause_black_40dp);
        } else {
            smallRemoteViews.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_player_play_black_40dp);
            bigRemoteViews.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_player_play_black_40dp);
        }
        builder.setCustomContentView(smallRemoteViews);
        builder.setCustomBigContentView(bigRemoteViews);
    }


    private void preparePlayPauseButton() {
        Intent playPauseIntent = new Intent(this, PlayerNotificationService.class);
        playPauseIntent.setAction(ACTION_PLAYER_CHANGE_STATE);
        playPauseIntent.putExtra(EXTRA_PLAYER_STATE_CODE, PlayerAction.PAUSE_PLAY);
        PendingIntent playPausePendingIntent = PendingIntent.getService(this, 111, playPauseIntent, 0);
        //getPackageManager().queryBroadcastReceivers(playPauseIntent, PackageManager.MATCH_ALL);

        RemoteViews smallRemoteViews = builder.getContentView();
        RemoteViews bigRemoteViews = builder.getBigContentView();
        smallRemoteViews.setOnClickPendingIntent(R.id.ib_notification_player_play_pause, playPausePendingIntent);
        bigRemoteViews.setOnClickPendingIntent(R.id.ib_notification_player_play_pause, playPausePendingIntent);
    }

    private void prepareNextButton() {
        Intent nextIntent = new Intent(this, PlayerNotificationService.class);
        nextIntent.setAction(ACTION_PLAYER_CHANGE_STATE);
        nextIntent.putExtra(EXTRA_PLAYER_STATE_CODE, PlayerAction.NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 222, nextIntent, 0);

        RemoteViews smallRemoteViews = builder.getContentView();
        RemoteViews bigRemoteViews = builder.getBigContentView();

        smallRemoteViews.setImageViewResource(R.id.ib_notification_player_next, R.drawable.ic_player_next_black_40dp);
        smallRemoteViews.setOnClickPendingIntent(R.id.ib_notification_player_next, nextPendingIntent);
        builder.setCustomContentView(smallRemoteViews);

        bigRemoteViews.setImageViewResource(R.id.ib_notification_player_next, R.drawable.ic_player_next_black_40dp);
        bigRemoteViews.setOnClickPendingIntent(R.id.ib_notification_player_next, nextPendingIntent);
        builder.setCustomContentView(bigRemoteViews);
    }

    private void prepareBackButton() {
        Intent backIntent = new Intent(this, PlayerNotificationService.class);
        backIntent.setAction(ACTION_PLAYER_CHANGE_STATE);
        backIntent.putExtra(EXTRA_PLAYER_STATE_CODE, PlayerAction.BACK);
        PendingIntent backPendingIntent = PendingIntent.getService(this, 333, backIntent, 0);

        RemoteViews smallRemoteViews = builder.getContentView();
        RemoteViews bigRemoteViews = builder.getBigContentView();

        smallRemoteViews.setImageViewResource(R.id.ib_notification_player_back, R.drawable.ic_player_back_black_40dp);
        smallRemoteViews.setOnClickPendingIntent(R.id.ib_notification_player_back, backPendingIntent);
        builder.setCustomContentView(smallRemoteViews);

        bigRemoteViews.setImageViewResource(R.id.ib_notification_player_back, R.drawable.ic_player_back_black_40dp);
        bigRemoteViews.setOnClickPendingIntent(R.id.ib_notification_player_back, backPendingIntent);
        builder.setCustomContentView(bigRemoteViews);
    }

    private RemoteViews setupSongData(YoutubeSongDto song, RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.tv_notification_song_title, song.getTitle());
        remoteViews.setTextViewText(R.id.tv_notification_channel_title, song.getAuthor());
        builder.setCustomContentView(remoteViews);
        Notification notification = builder.build();
        NotificationTarget notificationTarget = new NotificationTarget(
                getApplicationContext(),
                R.id.iv_notification_player_thumb,
                remoteViews,
                notification,
                notificationId);
        Glide.with(getApplicationContext()).asBitmap().load(song.getThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(300, 300)
                .circleCrop()
                .into(notificationTarget);
        notificationManager.notify(notificationId, notification);
        return remoteViews;
    }

    private void buildAndNotify(RemoteViews remoteViews) {
        builder.setCustomContentView(remoteViews);
        notificationManager.notify(notificationId, builder.build());
    }

    private void buildAndNotify(RemoteViews smallNotification, RemoteViews bigNotification) {
        builder.setCustomContentView(smallNotification);
        builder.setCustomBigContentView(bigNotification);
        notificationManager.notify(notificationId, builder.build());
    }

    public class PlayerStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            RemoteViews small = builder.getContentView();
            RemoteViews big = builder.getBigContentView();
            int playerStateCode = intent.getIntExtra(EXTRA_PLAYER_STATE_CODE, -1);
            Log.i(this.getClass().getSimpleName(), String.format("Intent with action %s received", intent.getAction()));
            switch (playerStateCode) {
                case Player.STATE_READY:
                    boolean isPlaying = intent.getBooleanExtra(EXTRA_PLAYBACK_STATUS, false);
                    switchPlayIcon(isPlaying);
                    break;
                case Player.STATE_ENDED:
                    small.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_player_play_black_40dp);
                    big.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_player_play_black_40dp);
                    switchPlayIcon(false);
                    break;
                case Player.STATE_BUFFERING:
                    return;
                case Player.STATE_IDLE:
                    small.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_player_play_black_40dp);
                    big.setImageViewResource(R.id.ib_notification_player_play_pause, R.drawable.ic_player_play_black_40dp);
                    break;
                case PLAYBACK_PROGRESS_CHANGED:
                    return;
                case PLAYER_ERROR:
                    return;
                case PLAYER_PAUSED:
                    switchPlayIcon(false);
                    Log.i(getClass().getSimpleName(), "Player paused");
                    break;
                case PLAYER_RESUMED:
                    switchPlayIcon(true);
                    Log.i(getClass().getSimpleName(), "Player resumed");
                    break;
                default:
                    Log.e(getClass().getSimpleName(), "Wrong player state code: " + playerStateCode);
                    return;
            }
            buildAndNotify(small, big);
        }
    }

//    public class PlayerChangeStateReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int action = intent.getIntExtra(EXTRA_PLAYER_STATE_CODE, -1);
//            RemoteViews small = builder.getContentView();
//            RemoteViews big = builder.getBigContentView();
//            Log.i(getClass().getSimpleName(), String.format("Intent with action %s received", intent.getAction()));
//            switch (action) {
//                case PlayerAction.START:
//                    YoutubeSongDto song = intent.getParcelableExtra(EXTRA_SONG);
//                    small = setupSongData(song, small);
//                    big = setupSongData(song, big);
//                    Log.i(getClass().getSimpleName(), "Player Notification Start");
//                    break;
//                case PlayerAction.PAUSE_PLAY:
//                    isPlaying = !isPlaying;
//                    switchPlayIcon(isPlaying);
//                    Log.i(getClass().getSimpleName(), "Player Notification Pause/Play");
//                    break;
//                default:
//                    Log.e(getClass().getSimpleName(), "Unknown action received");
//            }
//            buildAndNotify(small, big);
//        }
//    }
}
