package com.example.bioprep.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

/**
 * BroadcastReceiver conforming directly to Lab 08 Topic 9.
 * Receives Intent.ACTION_AIRPLANE_MODE_CHANGED and queries Airplane mode state.
 */
public class AirplaneModeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean isStateOn = intent.getBooleanExtra("state", false);
            boolean isSystemOn = isAirplaneModeOn(context);

            String statusMessage = (isStateOn || isSystemOn)
                    ? "Airplane Mode is ON (Distraction-Free Study Mode Active) ✈️"
                    : "Airplane Mode is OFF (Online Sync Restored) 🌐";

            Log.i("AirplaneModeReceiver", statusMessage);
        }
    }

    /**
     * Helper method checking airplane mode state as specified in Topic 9:
     * Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0)
     */
    public static boolean isAirplaneModeOn(Context context) {
        if (context == null) return false;
        try {
            return Settings.Global.getInt(
                    context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON,
                    0
            ) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}

