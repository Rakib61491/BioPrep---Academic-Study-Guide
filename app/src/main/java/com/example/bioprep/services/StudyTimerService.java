package com.example.bioprep.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.example.bioprep.utils.NotificationHelper;

/**
 * Bound Service managing an active study countdown timer.
 * Emits broadcasts on tick and completion, and triggers the completion notification.
 */
public class StudyTimerService extends Service {

    public static final String ACTION_TIMER_UPDATE = "com.example.bioprep.ACTION_TIMER_UPDATE";
    public static final String ACTION_TIMER_FINISHED = "com.example.bioprep.ACTION_TIMER_FINISHED";
    public static final String EXTRA_REMAINING_TIME_MS = "remaining_time_ms";

    private static final String TAG = "StudyTimerService";
    private static final long TICK_INTERVAL_MS = 1000L;

    private final IBinder binder = new LocalBinder();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private long endTimeMillis = 0L;
    private long remainingTimeMillis = 0L;
    private boolean isRunning = false;

    private final Runnable timerTickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;

            long now = SystemClock.elapsedRealtime();
            remainingTimeMillis = Math.max(0L, endTimeMillis - now);

            sendTimerUpdateBroadcast(remainingTimeMillis);

            if (remainingTimeMillis <= 0) {
                isRunning = false;
                handler.removeCallbacks(this);
                sendTimerFinishedBroadcast();
                NotificationHelper.showCompletionNotification(StudyTimerService.this);
                Log.d(TAG, "Timer reached zero and completed.");
            } else {
                handler.postDelayed(this, TICK_INTERVAL_MS);
            }
        }
    };

    public class LocalBinder extends Binder {
        public StudyTimerService getService() {
            return StudyTimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "StudyTimerService bound.");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "StudyTimerService unbound.");
        return super.onUnbind(intent);
    }

    /**
     * Starts the countdown timer with the specified duration in milliseconds.
     */
    public void startTimer(long durationMillis) {
        if (durationMillis <= 0) return;

        handler.removeCallbacks(timerTickRunnable);
        this.remainingTimeMillis = durationMillis;
        this.endTimeMillis = SystemClock.elapsedRealtime() + durationMillis;
        this.isRunning = true;

        Log.d(TAG, "Timer started with duration: " + durationMillis + " ms");
        sendTimerUpdateBroadcast(remainingTimeMillis);
        handler.postDelayed(timerTickRunnable, TICK_INTERVAL_MS);
    }

    /**
     * Pauses the timer and preserves remaining duration.
     */
    public void pauseTimer() {
        if (!isRunning) return;

        isRunning = false;
        long now = SystemClock.elapsedRealtime();
        remainingTimeMillis = Math.max(0L, endTimeMillis - now);
        handler.removeCallbacks(timerTickRunnable);

        Log.d(TAG, "Timer paused with remaining: " + remainingTimeMillis + " ms");
        sendTimerUpdateBroadcast(remainingTimeMillis);
    }

    /**
     * Resumes the timer from the preserved remaining duration.
     */
    public void resumeTimer() {
        if (isRunning || remainingTimeMillis <= 0) return;

        this.endTimeMillis = SystemClock.elapsedRealtime() + remainingTimeMillis;
        this.isRunning = true;

        Log.d(TAG, "Timer resumed with remaining: " + remainingTimeMillis + " ms");
        sendTimerUpdateBroadcast(remainingTimeMillis);
        handler.postDelayed(timerTickRunnable, TICK_INTERVAL_MS);
    }

    /**
     * Stops the timer and resets remaining time to zero.
     */
    public void stopTimer() {
        isRunning = false;
        remainingTimeMillis = 0L;
        endTimeMillis = 0L;
        handler.removeCallbacks(timerTickRunnable);

        Log.d(TAG, "Timer stopped.");
    }

    public long getRemainingTimeMillis() {
        if (isRunning) {
            long now = SystemClock.elapsedRealtime();
            return Math.max(0L, endTimeMillis - now);
        }
        return remainingTimeMillis;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void sendTimerUpdateBroadcast(long remainingMs) {
        Intent intent = new Intent(ACTION_TIMER_UPDATE);
        intent.putExtra(EXTRA_REMAINING_TIME_MS, remainingMs);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    private void sendTimerFinishedBroadcast() {
        Intent intent = new Intent(ACTION_TIMER_FINISHED);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacks(timerTickRunnable);
        Log.d(TAG, "StudyTimerService destroyed.");
    }
}

