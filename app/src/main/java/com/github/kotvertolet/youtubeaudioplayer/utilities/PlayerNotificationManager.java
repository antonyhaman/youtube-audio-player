//package com.example.android.datafrominternet.utilities;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.widget.RemoteViews;
//
//import androidx.core.app.NotificationCompat;
//
//import com.example.android.datafrominternet.R;
//import com.example.android.datafrominternet.activities.main.MainActivity;
//
//import java.lang.ref.WeakReference;
//
//import static com.example.android.datafrominternet.utilities.common.Constants.NOTIFICATION_SERVICE_ID;
//
//public class PlayerNotificationManager {
//
//    private WeakReference<Context> context;
//
//    public PlayerNotificationManager(Context context) {
//        this.context = new WeakReference<>(context);
//    }
//
//    public Notification createNotification() {
//        Intent notificationIntent = new Intent(context.get(), MainActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent contentPendingIntent = PendingIntent.getActivity(context.get(), 0, notificationIntent, 0);
//
//        RemoteViews notificationLayout = new RemoteViews(context.get().getPackageName(), R.layout.layout_expanded_notification);
//
//        notificationLayout.setOnClickPendingIntent(R.id.ib_notification_player_play_pause, );
//
//        Notification notification = new NotificationCompat.Builder(context.get())
//                .setContentTitle(context.get().getResources().getString(R.string.app_name))
//                .setTicker(context.get().getResources().getString(R.string.app_name))
//                //.setContentText(getResources().getString(R.string.my_string))
//                .setSmallIcon(R.drawable.ic_android_black_8dp)
//                //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
//                .setContentIntent(contentPendingIntent)
//                .setOngoing(true)
//                .setChannelId(NOTIFICATION_SERVICE_ID)
//                .setDeleteIntent(contentPendingIntent)  // if needed
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationLayout)
//                .build();
//        // To make notification stay when user clears all notifications
//        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_SERVICE_ID,
//                    context.get().getString(R.string.notification_channel_name),
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = context.get().getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
//}
