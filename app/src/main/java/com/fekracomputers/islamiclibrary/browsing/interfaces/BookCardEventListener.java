package com.fekracomputers.islamiclibrary.browsing.interfaces;

import android.support.annotation.Nullable;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 8/5/2017.
 */

public interface BookCardEventListener {
    void registerListener(BrowsingActivityListingFragment browsingActivityListingFragment);

    void unRegisterListener(BrowsingActivityListingFragment browsingActivityListingFragment);

    @Nullable
    BookCardEventsCallback getBookCardEventCallback();
}
