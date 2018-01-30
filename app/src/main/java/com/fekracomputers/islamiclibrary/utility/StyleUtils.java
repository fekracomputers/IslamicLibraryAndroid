package com.fekracomputers.islamiclibrary.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.fekracomputers.islamiclibrary.R;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 3/5/2017.
 */

public class StyleUtils {
    public static void setThemedActionBarDrawable(Context context, ActionBar actionBar) {
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.BooksTheme, 0, 0);
        actionBar.setBackgroundDrawable(a.getDrawable(0));
        a.recycle();
    }

    public static void configureFlatBlueActionBar(Context context, ActionBar actionBar) {
        setThemedActionBarDrawable(context, actionBar);
        actionBar.setDisplayOptions(14, 14);
    }

    public static void setActionBarElevation(ActionBar actionBar, float elevation) {
        if (SystemUtils.runningOnLollipopOrLater() && actionBar != null) {
            actionBar.setElevation(elevation);
        }
    }

    public static void flattenToolbar(Toolbar toolbar) {
        if (SystemUtils.runningOnLollipopOrLater() && toolbar != null) {
            toolbar.setElevation(0.0f);
        }
    }
}
