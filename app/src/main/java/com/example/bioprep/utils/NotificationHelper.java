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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.bioprep.R;
import com.example.bioprep.activities.AfterNotification;
import com.example.bioprep.activities.AnalyticsActivity;
import com.example.bioprep.activities.QuizResultActivity;
import com.example.bioprep.database.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

/**
 * Utility helper for Lab 08 Topic 11 (Notifications).
 * Implements NotificationChannel with lights, light color, vibration, NotificationCompat.Builder,
 * RemoteViews custom layout, PendingIntent, and NotificationManagerCompat.
 * Supports Practice Exam completion notifications and public lock-screen study metrics.
 */
public final class NotificationHelper {

    public static final String CHANNEL_ID = "bioprep_study_channel";
    public static final String EXAM_CHANNEL_ID = "bioprep_exam_channel";
    public static final int NOTIFICATION_ID = 1001;
    public static final int CUSTOM_NOTIF_ID = 1002;
    public static final int EXAM_NOTIF_ID = 2001;
    public static final int STATS_NOTIF_ID = 2002;
    private static final String TAG = "NotificationHelper";

    private NotificationHelper() {
        // Utility
    }

    /**
     * Topic 11: Creates notification channel with lights, vibration, and color.
     * Sets VISIBILITY_PUBLIC so notification details are visible on the lock screen.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) return;

            // Study Channel
            NotificationChannel studyChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "HSC Biology Study Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            studyChannel.setDescription("Alerts for study timer milestones, daily goals, and lockscreen stats");
            studyChannel.enableLights(true);
            studyChannel.setLightColor(Color.CYAN);
            studyChannel.enableVibration(true);
            studyChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(studyChannel);

            // Exam Completion Channel
            NotificationChannel examChannel = new NotificationChannel(
                    EXAM_CHANNEL_ID,
                    "Exam Practice Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            examChannel.setDescription("Notifications when a practice exam or mock test is completed");
            examChannel.enableLights(true);
            examChannel.setLightColor(Color.GREEN);
            examChannel.enableVibration(true);
            examChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(examChannel);

            Log.d(TAG, "Notification channels created with lockscreen visibility PUBLIC.");
        }
    }

    private static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Requirement 2 & 3: Sends notification upon Practice Exam completion, displaying:
     * - Exam score and accuracy percentage
     * - Total study time, active streak, and overall accuracy
     * - Configured with NotificationCompat.VISIBILITY_PUBLIC for complete lock-screen visibility.
     */
    public static void notifyPracticeExamCompleted(
            Context context,
            long attemptId,
            String chapterTitle,
            int score,
            int total
    ) {
        if (context == null) return;
        Context appCtx = context.getApplicationContext();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                createNotificationChannel(appCtx);
                if (!hasNotificationPermission(appCtx)) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission not granted for exam completion.");
                    return;
                }

                AppDatabase db = AppDatabase.getInstance(appCtx);

                // Calculate cumulative stats
                int totalStudySeconds = db.studySessionDao().getTotalStudySeconds();
                int totalCorrect = db.practiceDao().getTotalCorrectAnswers();
                int totalAnswered = db.practiceDao().getTotalAnsweredQuestions();
                int accuracyPercent = totalAnswered > 0
                        ? (int) Math.round((totalCorrect / (double) totalAnswered) * 100.0) : 0;

                int streak = calculateStreakDays(appCtx, db);

                // Format study time
                long hours = totalStudySeconds / 3600;
                long mins = (totalStudySeconds % 3600) / 60;
                String studyTimeStr = hours > 0 ? (hours + "h " + mins + "m") : (mins + "m");

                int examAccuracy = total > 0 ? (int) Math.round((score / (double) total) * 100.0) : 0;

                // PendingIntent to QuizResultActivity
                Intent intent = new Intent(appCtx, QuizResultActivity.class);
                intent.putExtra(QuizResultActivity.EXTRA_ATTEMPT_ID, attemptId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        appCtx,
                        (int) attemptId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                String cleanTitle = chapterTitle != null ? chapterTitle : "Practice Exam";
                String contentTitle = "Exam Completed: " + score + "/" + total + " (" + examAccuracy + "%)";
                String shortText = "Studied: " + studyTimeStr + " | Streak: " + streak + "d | Accuracy: " + accuracyPercent + "%";

                String bigText = "Exam: " + cleanTitle + "\n" +
                        "• Score: " + score + "/" + total + " (" + examAccuracy + "% accuracy)\n" +
                        "• Total Study Time: " + studyTimeStr + "\n" +
                        "• Active Streak: " + streak + " days\n" +
                        "• Overall Accuracy: " + accuracyPercent + "% (" + totalCorrect + "/" + totalAnswered + " correct)";

                NotificationCompat.Builder builder = new NotificationCompat.Builder(appCtx, EXAM_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(contentTitle)
                        .setContentText(shortText)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle("🎓 " + contentTitle)
                                .bigText(bigText))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_EVENT)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Visible on lock screen

                NotificationManagerCompat.from(appCtx).notify(EXAM_NOTIF_ID, builder.build());
                Log.d(TAG, "Practice Exam completion notification posted with lock-screen visibility.");

                // Also update the lock screen study metrics notification
                showStudyStatsLockscreenNotification(appCtx);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying exam completion: " + e.getMessage());
            }
        });
    }

    /**
     * Requirement 3: Shows public lock-screen notification detailing:
     * - How much you have studied (hours and minutes)
     * - Active streak (consecutive days)
     * - Overall accuracy percentage
     * Fully visible on the lock screen via NotificationCompat.VISIBILITY_PUBLIC.
     */
    public static void showStudyStatsLockscreenNotification(Context context) {
        if (context == null) return;
        Context appCtx = context.getApplicationContext();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                createNotificationChannel(appCtx);
                if (!hasNotificationPermission(appCtx)) return;

                AppDatabase db = AppDatabase.getInstance(appCtx);
                int totalStudySeconds = db.studySessionDao().getTotalStudySeconds();
                int totalCorrect = db.practiceDao().getTotalCorrectAnswers();
                int totalAnswered = db.practiceDao().getTotalAnsweredQuestions();
                int accuracyPercent = totalAnswered > 0
                        ? (int) Math.round((totalCorrect / (double) totalAnswered) * 100.0) : 0;

                int streak = calculateStreakDays(appCtx, db);

                long hours = totalStudySeconds / 3600;
                long mins = (totalStudySeconds % 3600) / 60;
                String studyTimeStr = hours > 0 ? (hours + "h " + mins + "m") : (mins + "m");
                int goalMinutes = UserPreferences.getDailyGoalMinutes(appCtx);

                Intent intent = new Intent(appCtx, AnalyticsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        appCtx,
                        STATS_NOTIF_ID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                String title = "BioPrep Study Status 📊";
                String shortText = "Studied: " + studyTimeStr + " | Streak: " + streak + "d | Accuracy: " + accuracyPercent + "%";
                String bigText = "HSC Biology Daily Progress\n" +
                        "• Total Studied: " + studyTimeStr + " (Daily Goal: " + goalMinutes + "m)\n" +
                        "• Active Streak: " + streak + " days\n" +
                        "• Overall Accuracy: " + accuracyPercent + "% across " + totalAnswered + " questions\n" +
                        "Tap to open Daily Goals & Analytics.";

                NotificationCompat.Builder builder = new NotificationCompat.Builder(appCtx, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(shortText)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle("📚 " + title)
                                .bigText(bigText))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Visible on lock screen

                NotificationManagerCompat.from(appCtx).notify(STATS_NOTIF_ID, builder.build());
                Log.d(TAG, "Study stats lock-screen notification posted.");
            } catch (Exception e) {
                Log.e(TAG, "Error posting study stats notification: " + e.getMessage());
            }
        });
    }

    private static int calculateStreakDays(Context context, AppDatabase db) {
        int dailyGoalSecs = UserPreferences.getDailyGoalMinutes(context) * 60;
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayDate = dbDateFormat.format(new Date());
        int todaySecs = db.studySessionDao().getStudySecondsForDate(todayDate);

        int streak = 0;
        if (todaySecs >= dailyGoalSecs) {
            streak++;
        }

        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 365; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            String prevDate = dbDateFormat.format(cal.getTime());
            int secs = db.studySessionDao().getStudySecondsForDate(prevDate);
            if (secs >= dailyGoalSecs) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

    /**
     * Topic 11: Sends standard or custom RemoteViews notification with PendingIntent to AfterNotification.class.
     */
    public static void sendNotification(Context context, boolean useCustomView) {
        if (context == null) return;

        createNotificationChannel(context);

        // Permission check for Android 13+ (POST_NOTIFICATIONS)
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "POST_NOTIFICATIONS permission not granted.");
            return;
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
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

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
