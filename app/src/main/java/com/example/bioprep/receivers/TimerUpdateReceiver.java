package com.example.bioprep.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bioprep.services.StudyTimerService;

/**
 * Dynamically registered BroadcastReceiver for receiving timer updates and completion events.
 */
public class TimerUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "TimerUpdateReceiver";

    public interface TimerListener {
        void onTimerUpdate(long remainingMillis);
        void onTimerFinished();
    }

    private TimerListener listener;

    public TimerUpdateReceiver() {
    }

    public TimerUpdateReceiver(TimerListener listener) {
        this.listener = listener;
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public void clearListener() {
        this.listener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        Log.d(TAG, "onReceive action: " + action);

        if (StudyTimerService.ACTION_TIMER_UPDATE.equals(action)) {
            long remaining = intent.getLongExtra(StudyTimerService.EXTRA_REMAINING_TIME_MS, 0L);
            if (listener != null) {
                listener.onTimerUpdate(remaining);
            }
        } else if (StudyTimerService.ACTION_TIMER_FINISHED.equals(action)) {
            if (listener != null) {
                listener.onTimerFinished();
            }
        }
    }
}

