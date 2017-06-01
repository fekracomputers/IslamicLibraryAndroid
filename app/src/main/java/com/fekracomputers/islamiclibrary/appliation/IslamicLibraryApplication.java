package com.fekracomputers.islamiclibrary.appliation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.settings.SettingsFragment;

import java.util.Locale;

/**
 *
 *  locale changing from Quran Andoid
 */
public class IslamicLibraryApplication extends Application {
//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }
    public void refreshLocale(@NonNull Context context, boolean force) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final String language = settings.getBoolean(SettingsFragment.KEY_UI_LANG_ARABIC, false) ? "ar" : null;

        final Locale locale;
        if ("ar".equals(language)) {
            locale = new Locale("ar");
        } else if (force) {
            // get the system locale (since we overwrote the default locale)
            locale = Resources.getSystem().getConfiguration().locale;
        } else {
            // nothing to do...
            return;
        }

        updateLocale(context, locale);
        final Context appContext = context.getApplicationContext();
        if (context != appContext) {
            updateLocale(appContext, locale);
        }
    }

    private void updateLocale(@NonNull Context context, @NonNull Locale locale) {
        final Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}

