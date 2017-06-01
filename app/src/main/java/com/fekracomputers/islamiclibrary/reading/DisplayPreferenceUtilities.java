package com.fekracomputers.islamiclibrary.reading;

import android.content.SharedPreferences;

import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.settings.SettingsFragment;
import com.fekracomputers.islamiclibrary.utility.AppConstants;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 4/5/2017.
 */

public class DisplayPreferenceUtilities {
    public static boolean getDisplayPreference(String preferenceKey, boolean defaultValue, SharedPreferences sharedPref, UserDataDBHelper mUserDataDBHelper) {
        boolean globalTheme = sharedPref.getBoolean(preferenceKey, defaultValue);
        if (sharedPref.getBoolean(SettingsFragment.KEY_GLOBAL_DISPLAY_OVERRIDES_LOCAL, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.GLOBAL_OVERRIDES_LOCAL)) {
            return globalTheme;
        } else {
            return Boolean.valueOf(mUserDataDBHelper.getDisplayPreferenceValue(preferenceKey, String.valueOf(defaultValue)));
        }
    }

    public static int getDisplayPreference(String preferenceKey, int defaultValue, SharedPreferences sharedPref, UserDataDBHelper mUserDataDBHelper) {
        int globalTheme = sharedPref.getInt(preferenceKey, defaultValue);
        if (sharedPref.getBoolean(SettingsFragment.KEY_GLOBAL_DISPLAY_OVERRIDES_LOCAL, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.GLOBAL_OVERRIDES_LOCAL)) {
            return globalTheme;
        } else {
            return Integer.valueOf(mUserDataDBHelper.getDisplayPreferenceValue(preferenceKey, String.valueOf(defaultValue)));
        }
    }

    public static void setDisplayPreference(String preferenceKey, int value, SharedPreferences sharedPref, UserDataDBHelper userDataDBHelper) {

        if (sharedPref.getBoolean(SettingsFragment.KEY_GLOBAL_DISPLAY_OVERRIDES_LOCAL, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.GLOBAL_OVERRIDES_LOCAL)) {
            sharedPref.edit().putInt(preferenceKey, value).apply();
        } else {
            userDataDBHelper.setDisplayPreferenceValue(preferenceKey, String.valueOf(value));
        }
    }

    public static void setDisplayPreference(String preferenceKey, boolean value, SharedPreferences sharedPref, UserDataDBHelper userDataDBHelper) {

        if (sharedPref.getBoolean(SettingsFragment.KEY_GLOBAL_DISPLAY_OVERRIDES_LOCAL, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.GLOBAL_OVERRIDES_LOCAL)) {
            sharedPref.edit().putBoolean(preferenceKey, value).apply();
        } else {
            userDataDBHelper.setDisplayPreferenceValue(preferenceKey, String.valueOf(value));
        }
    }
}
