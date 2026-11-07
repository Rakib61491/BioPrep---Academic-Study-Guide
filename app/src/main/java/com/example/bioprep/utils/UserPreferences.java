package com.example.bioprep.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREF_NAME = "BioPrepUserPreferences";

    private static final String KEY_AGREEMENT_ACCEPTED = "key_agreement_accepted";
    private static final String KEY_IS_LOGGED_IN = "key_is_logged_in";
    private static final String KEY_USER_NAME = "key_user_name";
    private static final String KEY_USER_EMAIL = "key_user_email";
    private static final String KEY_USER_AVATAR = "key_user_avatar";
    private static final String KEY_DAILY_GOAL_MINUTES = "key_daily_goal_minutes";
    private static final String KEY_REMINDER_ENABLED = "key_reminder_enabled";
    private static final String KEY_REMINDER_HOUR = "key_reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "key_reminder_minute";

    public static final String STUDENT_LEVEL_TITLE = "HSC Biology Student";
    public static final int DEFAULT_DAILY_GOAL_MINUTES = 120; // 2 hours

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isAgreementAccepted(Context context) {
        return getPrefs(context).getBoolean(KEY_AGREEMENT_ACCEPTED, false);
    }

    public static void setAgreementAccepted(Context context, boolean accepted) {
        getPrefs(context).edit().putBoolean(KEY_AGREEMENT_ACCEPTED, accepted).apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static void saveSession(Context context, String name, String email) {
        getPrefs(context).edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    public static void clearSession(Context context) {
        getPrefs(context).edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_USER_NAME)
                .remove(KEY_USER_EMAIL)
                .remove(KEY_USER_AVATAR)
                .putInt(KEY_DAILY_GOAL_MINUTES, DEFAULT_DAILY_GOAL_MINUTES)
                .apply();
    }

    public static String getUserName(Context context) {
        return getPrefs(context).getString(KEY_USER_NAME, "HSC Biology Student");
    }

    public static void setUserName(Context context, String name) {
        getPrefs(context).edit().putString(KEY_USER_NAME, name).apply();
    }

    public static String getUserEmail(Context context) {
        return getPrefs(context).getString(KEY_USER_EMAIL, "");
    }

    public static String getAvatarPath(Context context) {
        return getPrefs(context).getString(KEY_USER_AVATAR, null);
    }

    public static void setAvatarPath(Context context, String path) {
        getPrefs(context).edit().putString(KEY_USER_AVATAR, path).apply();
    }

    public static int getDailyGoalMinutes(Context context) {
        return getPrefs(context).getInt(KEY_DAILY_GOAL_MINUTES, DEFAULT_DAILY_GOAL_MINUTES);
    }

    public static void setDailyGoalMinutes(Context context, int minutes) {
        getPrefs(context).edit().putInt(KEY_DAILY_GOAL_MINUTES, minutes).apply();
    }

    public static boolean isReminderEnabled(Context context) {
        return getPrefs(context).getBoolean(KEY_REMINDER_ENABLED, true);
    }

    public static void setReminderEnabled(Context context, boolean enabled) {
        getPrefs(context).edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply();
    }

    public static int getReminderHour(Context context) {
        return getPrefs(context).getInt(KEY_REMINDER_HOUR, 20); // 8:00 PM default
    }

    public static int getReminderMinute(Context context) {
        return getPrefs(context).getInt(KEY_REMINDER_MINUTE, 0);
    }

    public static void setReminderTime(Context context, int hour, int minute) {
        getPrefs(context).edit()
                .putInt(KEY_REMINDER_HOUR, hour)
                .putInt(KEY_REMINDER_MINUTE, minute)
                .putBoolean(KEY_REMINDER_ENABLED, true)
                .apply();
    }
}

