package com.github.kotvertolet.youtubeaudioplayer.utilities.common;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public static void callSimpleDialog(Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message).setPositiveButton("OK", null).create().show();
    }

    public static void callSimpleDialog(Context context, int id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(id).setPositiveButton("OK", null).create().show();
    }

    public static void callSimpleDialog(Context context, int idDescription, int idTitle) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(idDescription).setTitle(idTitle)
                .setPositiveButton("OK", null).create().show();
    }
}
