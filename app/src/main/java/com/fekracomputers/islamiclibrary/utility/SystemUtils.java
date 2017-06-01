package com.fekracomputers.islamiclibrary.utility;

import android.os.Build.VERSION;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 3/5/2017.
 */

public class SystemUtils {
    public static boolean runningOnOrAfter(int versionCode) {
        return VERSION.SDK_INT >= versionCode;
    }

    public static boolean runningOnNougatMr1OrLater() {
        return VERSION.SDK_INT >= 25;
    }

    public static boolean runningOnNougatDeviceReleaseOrLater() {
        return VERSION.SDK_INT >= 25;
    }

    public static boolean runningOnNougatOrLater() {
        return VERSION.SDK_INT >= 24;
    }

    public static boolean runningBeforeNougat() {
        return VERSION.SDK_INT < 24;
    }

    public static boolean runningOnMarshmallowOrLater() {
        return VERSION.SDK_INT >= 23;
    }

    public static boolean runningBeforeMarshmallow() {
        return VERSION.SDK_INT < 23;
    }

    public static boolean runningOnLollipopOrLater() {
        return VERSION.SDK_INT >= 21;
    }

    public static boolean runningBeforeLollipop() {
        return VERSION.SDK_INT < 21;
    }

    public static boolean runningOnKitKatOrLater() {
        return VERSION.SDK_INT >= 19;
    }

    public static boolean runningOnKitKatWatchOrLater() {
        return VERSION.SDK_INT >= 20;
    }

    public static boolean runningOnKitKat() {
        return VERSION.SDK_INT == 19 || VERSION.SDK_INT == 20;
    }

    public static boolean runningBeforeKitKat() {
        return VERSION.SDK_INT < 19;
    }

    public static boolean runningOnJellyBeanMR2OrLater() {
        return VERSION.SDK_INT >= 18;
    }

    public static boolean runningOnJellyBeanMR1OrLater() {
        return VERSION.SDK_INT >= 17;
    }

    public static boolean runningOnJellyBeanMR1() {
        return VERSION.SDK_INT == 17;
    }

    public static boolean runningOnJellyBeanOrLater() {
        return VERSION.SDK_INT >= 16;
    }

    public static boolean runningBeforeJellyBean() {
        return VERSION.SDK_INT < 16;
    }

    public static boolean runningOnIcsMR1OrLater() {
        return VERSION.SDK_INT >= 15;
    }

}
