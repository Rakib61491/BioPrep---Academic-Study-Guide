package com.example.bioprep.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * Background Service conforming directly to Lab 08 Topic 8.
 * Plays ambient/focus sound using MediaPlayer, looping, START_STICKY, onStartCommand, onDestroy.
 */
public class BackgroundAudioService extends Service {

    private static final String TAG = "BackgroundAudioService";
    private MediaPlayer myPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (myPlayer == null) {
                myPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
                if (myPlayer != null) {
                    myPlayer.setLooping(true);
                    myPlayer.start();
                }
            } else if (!myPlayer.isPlaying()) {
                myPlayer.start();
            }
            Toast.makeText(this, "Focus Audio Service Started (START_STICKY)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio: " + e.getMessage());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (myPlayer != null) {
            try {
                if (myPlayer.isPlaying()) {
                    myPlayer.stop();
                }
                myPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping audio: " + e.getMessage());
            }
            myPlayer = null;
        }
        Toast.makeText(this, "Focus Audio Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}

