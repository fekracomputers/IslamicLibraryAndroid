package com.fekracomputers.islamiclibrary.model;

import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 1/3/2017.
 */
public interface BookCatalogElement extends Parcelable {
    int getId();

    @StringRes
    int getName();
    Fragment getNewFragment();
    @DrawableRes
    int getIconDrawableId();
}
