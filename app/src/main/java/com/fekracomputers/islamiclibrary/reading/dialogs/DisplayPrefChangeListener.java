package com.fekracomputers.islamiclibrary.reading.dialogs;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.reading.fragments.BookPageFragment;

/**
 * This interface to be implemented by fragments of {@link BookPageFragment}
 * to listen to changes broadcast by the {@link DisplayOptionsPopupFragment}
 */

public interface DisplayPrefChangeListener {
    void setZoom(int newZoom);

    void setTashkeel(boolean tashkeelOn);

    void setPinchZoom(boolean pinchZoomOn);

    void setBackgroundColor(@ColorInt int color);

    void setHeadingColor(@ColorInt int color);

    void setTextColor(@ColorInt int color);

    void highLightSearchResult(@NonNull String searchQuery);

    void removeSearchResultHighlights();
}
