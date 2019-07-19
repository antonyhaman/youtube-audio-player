package com.github.kotvertolet.youtubeaudioplayer.utilities.common;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.TypedValue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.kotvertolet.youtubeaudioplayer.R;
import com.github.kotvertolet.youtubeaudioplayer.data.NetworkType;

import org.joda.time.Period;

import java.lang.ref.WeakReference;
import java.text.ChoiceFormat;
import java.text.NumberFormat;

public class CommonUtils {

    private WeakReference<Context> context;
    private LocalBroadcastManager localBroadcastManager;

    public CommonUtils(Context context) {
        this.context = new WeakReference<>(context);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public String getCountryCode() {
        TelephonyManager tm = (TelephonyManager) context.get().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso();
    }

    public NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) context.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public NetworkType getNetworkClass() {
        NetworkInfo info = getNetworkInfo();
        if (info == null || !info.isConnected())
            return NetworkType.TYPE_NOT_CONNECTED; //not connected
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            return NetworkType.TYPE_WIFI;
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return NetworkType.TYPE_2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
                    return NetworkType.TYPE_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
                case 19:  //LTE_CA
                    return NetworkType.TYPE_4G;
                default:
                    return NetworkType.TYPE_NOT_CONNECTED;
            }
        }
        return NetworkType.TYPE_NOT_CONNECTED;
    }

    public boolean isNetworkAvailable() {
        NetworkInfo networkInfo = getNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public String parseISO8601time(String iso8601time) {
        return DateUtils.formatElapsedTime(Period.parse(iso8601time).toStandardDuration().getStandardSeconds());
    }

    public String convertMillsIntoTimeString(int timeMs) {
        StringBuilder mFormatBuilder;
        mFormatBuilder = new StringBuilder();

        int seconds = timeMs % 60;
        int minutes = (timeMs / 60) % 60;
        int hours = timeMs / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public String formatYtViewsAndLikesString(String rawString) {
        final char nonBreakableSpaceChar = (char) 160;
        final char commaChar = (char) 44;
        final char zeroChar = '0';
        if (rawString != null) {
            Double viewCount = Double.valueOf(rawString);
            String label = new ChoiceFormat("0# |100000#K|1000000#M|1000000000#B").format(viewCount);
            String formattedLine = NumberFormat.getInstance().format(viewCount);
            formattedLine = formattedLine.replace(nonBreakableSpaceChar, commaChar);
            int strLength = formattedLine.length();
            switch (label) {
                case " ":
                    return formattedLine;
                case "K": {
                    return formattedLine.substring(0, 3) + label;
                }
                case "M": {
                    if (strLength == 8) {
                        return formattedLine.substring(0, 1) + label;
                    } else if (strLength == 9 && formattedLine.charAt(2) != zeroChar) {
                        return formattedLine.substring(0, strLength - 6) + label;
                    } else {
                        return formattedLine.substring(0, strLength - 8) + label;
                    }
                }
                case "B": {
                    if (strLength == 13) {
                        if (formattedLine.charAt(2) != zeroChar) {
                            return formattedLine.substring(0, strLength - 10) + label;
                        } else {
                            return formattedLine.substring(0, strLength - 12) + label;
                        }
                    } else {
                        return formattedLine.substring(0, strLength - 11) + label;
                    }
                }
                default:
                    return formattedLine;
            }
        } else return "";
    }

    public float dpInPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.get().getResources().getDisplayMetrics());
    }

    public int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public boolean isServiceRunning(Class tClass) {
        ActivityManager manager = (ActivityManager) context.get().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (tClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void sendLocalBroadcastMessage(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        intent.putExtras(bundle);
        localBroadcastManager.sendBroadcast(intent);
    }

    public AlertDialog createAlertDialog(int titleId, int messageId, boolean isCancelable,
                                         int positiveButtonId, DialogInterface.OnClickListener positiveCallback,
                                         int negativeButtonId, DialogInterface.OnClickListener negativeCallback) {
        AppCompatActivity activity = (AppCompatActivity) context.get();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogTheme));
        builder = builder.setTitle(titleId)
                .setMessage(messageId);
        if (isCancelable) {
            builder.setCancelable(true);
        } else {
            builder.setCancelable(false);
        }
        if (positiveButtonId != 0 && positiveCallback != null) {
            builder.setPositiveButton(positiveButtonId, positiveCallback);
        }
        if (negativeButtonId != 0 && negativeCallback != null) {
            builder.setNegativeButton(negativeButtonId, negativeCallback);
        }
        return builder.create();
    }

    public AlertDialog createAlertDialog(String title, String message, boolean isCancelable,
                                         int positiveButtonId, DialogInterface.OnClickListener positiveCallback,
                                         int negativeButtonId, DialogInterface.OnClickListener negativeCallback) {
        AppCompatActivity activity = (AppCompatActivity) context.get();
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AlertDialogTheme));
        builder = builder.setTitle(title)
                .setMessage(message);
        if (isCancelable) {
            builder.setCancelable(true);
        } else {
            builder.setCancelable(false);
        }
        if (positiveButtonId != 0 && positiveCallback != null) {
            builder.setPositiveButton(positiveButtonId, positiveCallback);
        }
        if (negativeButtonId != 0 && negativeCallback != null) {
            builder.setNegativeButton(negativeButtonId, negativeCallback);
        }
        return builder.create();
    }

    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context.get(), drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}

