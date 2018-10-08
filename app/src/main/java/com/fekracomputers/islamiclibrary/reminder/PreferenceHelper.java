package com.fekracomputers.islamiclibrary.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Date;

import timber.log.Timber;

final class PreferenceHelper {

    private static final String PREF_FILE_NAME = "rate_pref_file";

    private static final String PREF_KEY_INSTALL_DATE = "rate_install_date";

    private static final String PREF_KEY_LAUNCH_TIMES = "rate_launch_times";

    private static final String PREF_KEY_LATEST_SHOWN_CHANGE_LOG = "rate_last_shown_change_log";

    private static final String PREF_KEY_IS_AGREE_SHOW_DIALOG = "rate_is_agree_show_dialog";

    private static final String PREF_KEY_REMIND_INTERVAL = "rate_remind_interval";

    private PreferenceHelper() {
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    static Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    /**
     * Clear data in shared preferences.<br/>
     *
     * @param context context
     */
    static void clearSharedPreferences(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.remove(PREF_KEY_INSTALL_DATE)
                .remove(PREF_KEY_LAUNCH_TIMES)
                .remove(PREF_KEY_LATEST_SHOWN_CHANGE_LOG)
                .remove(PREF_KEY_IS_AGREE_SHOW_DIALOG)
                .apply();
    }

    /**
     * Clear data in shared preferences.<br/>
     *
     * @param context context
     */
    static void resetLaunchTimes(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.remove(PREF_KEY_LAUNCH_TIMES);
        editor.apply();

    }

    /**
     * Set agree flag about show dialog.<br/>
     * If it is false, rate dialog will never shown unless data is cleared.
     *
     * @param context context
     * @param isAgree agree with showing rate dialog
     */
    static void setAgreeShowDialog(Context context, boolean isAgree) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putBoolean(PREF_KEY_IS_AGREE_SHOW_DIALOG, isAgree);
        editor.apply();
    }

    static boolean getIsAgreeShowDialog(Context context) {
        return getPreferences(context).getBoolean(PREF_KEY_IS_AGREE_SHOW_DIALOG, true);
    }

    static void reSetLastRemind(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putLong(PREF_KEY_REMIND_INTERVAL, new Date().getTime());
        editor.apply();
    }

    static long getRemindInterval(Context context) {
        return getPreferences(context).getLong(PREF_KEY_REMIND_INTERVAL, 0);
    }

    static void setInstallDate(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putLong(PREF_KEY_INSTALL_DATE, new Date().getTime());
        editor.apply();
    }

    static long getInstallDate(Context context) {
        return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0);
    }

    static void setLaunchTimes(Context context, int launchTimes) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putInt(PREF_KEY_LAUNCH_TIMES, launchTimes);
        editor.apply();
    }

    static int getLaunchTimes(Context context) {
        return getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0);
    }

    static boolean isFirstLaunch(Context context) {
        return getPreferences(context).getLong(PREF_KEY_INSTALL_DATE, 0) == 0L;
    }

    public static void incrementLaunchTimes(Context context) {
        setLaunchTimes(context, getLaunchTimes(context) + 1);
    }

    public static boolean isFirstLaunchAfterUpdate(Context context) {
        try {
            int versionCode = getVersionCode(context);

            //version where changelog has been viewed
            int viewedChangelogVersion = getPreferences(context).getInt(PREF_KEY_LATEST_SHOWN_CHANGE_LOG, 0);
            return viewedChangelogVersion < versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.w(e, "Unable to get version code. Will not show changelog");
        }
        return false;
    }

    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        //current version
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionCode;
    }

    public static void updateLastViewdChangeLog(Context context) {
        try {
            getPreferencesEditor(context)
                    .putInt(PREF_KEY_LATEST_SHOWN_CHANGE_LOG, getVersionCode(context))
                    .commit();
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
    }
}