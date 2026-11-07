package com.example.bioprep.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.bioprep.R;
import com.example.bioprep.activities.AfterNotification;

/**
 * Utility helper for Lab 08 Topic 11 (Notifications).
 * Implements NotificationChannel with lights, light color, vibration, NotificationCompat.Builder,
 * RemoteViews custom layout, PendingIntent, and NotificationManagerCompat.
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "bioprep_study_channel";
    public static final int NOTIFICATION_ID = 1001;
    public static final int CUSTOM_NOTIF_ID = 1002;
    private static final String TAG = "NotificationHelper";

    private NotificationHelper() {
        // Utility
    }

    /**
     * Topic 11: Creates notification channel with lights, vibration, and color.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "HSC Biology Study Alerts";
            String description = "Alerts for study timer milestones and active recall drills";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.CYAN);
            channel.enableVibration(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created: " + CHANNEL_ID);
            }
        }
    }

    /**
     * Topic 11: Sends standard or custom RemoteViews notification with PendingIntent to AfterNotification.class.
     */
    public static void sendNotification(Context context, boolean useCustomView) {
        if (context == null) return;

        createNotificationChannel(context);

        // Permission check for Android 13+ (POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted.");
                return;
            }
        }

        // Explicit Intent to AfterNotification.class as specified in Topic 11
        Intent intent = new Intent(context, AfterNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notif_title))
                .setContentText(context.getString(R.string.notif_content))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (useCustomView) {
            // Topic 11: new RemoteViews(getPackageName(), R.layout...) and setContent()
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
            remoteViews.setTextViewText(R.id.tvNotifTitle, "HSC Biology Recall Alert");
            remoteViews.setTextViewText(R.id.tvNotifDesc, "Tap to review photosynthesis & cellular respiration!");
            builder.setContent(remoteViews);
        }

        Notification notification = builder.build();

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification);
            Log.d(TAG, "Notification posted successfully.");
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException while notifying: " + e.getMessage());
        }
    }

    public static void showCompletionNotification(Context context) {
        sendNotification(context, false);
    }
}
