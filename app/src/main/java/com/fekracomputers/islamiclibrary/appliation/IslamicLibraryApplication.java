package com.fekracomputers.islamiclibrary.appliation;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.fekracomputers.islamiclibrary.BuildConfig;
import com.fekracomputers.islamiclibrary.settings.SettingsFragment;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * locale changing from Quran Andoid
 */
public class IslamicLibraryApplication extends Application {
//    static {
//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ProductionTree());
        }
    }

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

    private class ProductionTree extends Timber.Tree {
        ProductionTree() {
            Fabric.with(IslamicLibraryApplication.this, new Crashlytics(), new Answers());
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            Crashlytics.log(message);
            if (t != null) {
                Crashlytics.logException(t);
            }
            // If this is an error or a warning, log it as a exception so we see it in Crashlytics.
            if (priority > Log.WARN) {
                Crashlytics.logException(new Throwable(message));
            }
        }
    }
}

