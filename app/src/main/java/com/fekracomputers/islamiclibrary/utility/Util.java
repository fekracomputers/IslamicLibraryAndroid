package com.fekracomputers.islamiclibrary.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

/**
 * Created by Eugen on 13. 5. 2015.
 *
 * @hide
 */
public final class Util {
    private static final int[] TEMP_ARRAY = new int[1];

    private Util() {
    }

    public static float dpToPx(@NonNull Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int dpToPxOffset(@NonNull Context context, int dp) {
        return (int) (dpToPx(context, dp));
    }

    public static int dpToPxSize(@NonNull Context context, int dp) {
        return (int) (0.5f + dpToPx(context, dp));
    }

    public static int resolveResourceId(@NonNull Context context, @AttrRes int attr, int fallback) {
        TEMP_ARRAY[0] = attr;
        TypedArray ta = context.obtainStyledAttributes(TEMP_ARRAY);
        try {
            return ta.getResourceId(0, fallback);
        } finally {
            ta.recycle();
        }
    }

    public static void restartIfLocaleChanged(@NonNull Activity activity, boolean oldIsArabic)
    {
        final boolean isUpdatedLocaleArabic=isArabicUi(activity);
        if (isUpdatedLocaleArabic != oldIsArabic) {
            final Intent i = activity.getIntent();
            activity.finish();
            activity.startActivity(i);
        }
    }
    public static boolean isArabicUi(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return  settings.getBoolean("ui_lang_arabic", false);
    }

    public static int getThemeColor(@NonNull Context context, int attrId) {
       final TypedValue sTypedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attrId, sTypedValue, true)) {
            return sTypedValue.data;
        }
        return 0;
    }

    public static void enableSoftInput(@NonNull View view, boolean toEnable) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        if (toEnable) {
            imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @ColorInt
    public static int getColorFromAttr(@NonNull Context context, int attr, @ColorInt int defaultColor) {
        TypedArray a = context.obtainStyledAttributes(new TypedValue().data, new int[]{attr});
        int intColor = a.getColor(0, defaultColor);
        a.recycle();
        return intColor;
    }

    public static void setImageButtonEnabled(@NonNull Context ctxt, boolean enabled, @NonNull ImageButton item,
                                             int iconResId) {
        item.setEnabled(enabled);
        Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
        item.setImageDrawable(icon);
    }

    /**
     * Mutates and applies a filter that converts the given drawable to a Gray
     * image. This method may be used to simulate the color of disable icons in
     * Honeycomb's ActionBar.
     *
     * @return a mutated version of the given drawable with a color filter
     * applied.
     */
    @Nullable
    public static Drawable convertDrawableToGrayScale(@Nullable Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }
}