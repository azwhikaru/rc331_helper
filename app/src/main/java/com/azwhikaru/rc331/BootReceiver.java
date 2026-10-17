package com.azwhikaru.rc331;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (!Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) return;
        Log.i(RootCommands.TAG, "boot_action=" + action);
        PendingResult result = goAsync();
        new Thread(() -> {
            try {
                RootCommands.apply();
            } finally {
                result.finish();
            }
        }).start();
    }
}
