package com.github.kotvertolet.youtubeaudioplayer.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ReceiverManager {

    private static List<BroadcastReceiver> localReceivers = new ArrayList<>();
    private static List<BroadcastReceiver> globalReceivers = new ArrayList<>();
    private static ReceiverManager INSTANCE;
    private LocalBroadcastManager localBroadcastManager;
    private WeakReference<Context> contextWeakReference;

    private ReceiverManager(Context context) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        contextWeakReference = new WeakReference<>(context);
    }

    public static synchronized ReceiverManager getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new ReceiverManager(context);
        return INSTANCE;
    }

    public void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter intentFilter) {
        if (!isReceiverRegistered(receiver)) {
            localReceivers.add(receiver);
            localBroadcastManager.registerReceiver(receiver, intentFilter);
            Log.i(getClass().getSimpleName(), "Registered local receiver: " + receiver + "  with filter: " + intentFilter);
        }
    }

    public void registerGlobalReceiver(BroadcastReceiver receiver, IntentFilter intentFilter) {
        if (!isReceiverRegistered(receiver)) {
            globalReceivers.add(receiver);
            contextWeakReference.get().registerReceiver(receiver, intentFilter);
            Log.i(getClass().getSimpleName(), "Registered global receiver: " + receiver + "  with filter: " + intentFilter);
        }
    }

    public boolean isReceiverRegistered(BroadcastReceiver receiver) {
        boolean registered = false;
        if (receiver != null && localReceivers.contains(receiver) || globalReceivers.contains(receiver)) {
            registered = true;
        }
        Log.i(getClass().getSimpleName(), "Is receiver " + receiver + " registered? " + registered);
        return registered;
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (localReceivers.contains(receiver)) {
            localReceivers.remove(receiver);
            localBroadcastManager.unregisterReceiver(receiver);
            Log.i(getClass().getSimpleName(), "Unregistered local receiver: " + receiver);
        } else if (globalReceivers.contains(receiver)) {
            globalReceivers.remove(receiver);
            contextWeakReference.get().unregisterReceiver(receiver);
            Log.i(getClass().getSimpleName(), "Unregistered global receiver: " + receiver);
        } else Log.i(getClass().getSimpleName(), "Receiver: " + receiver + " is not registered");
    }

}
