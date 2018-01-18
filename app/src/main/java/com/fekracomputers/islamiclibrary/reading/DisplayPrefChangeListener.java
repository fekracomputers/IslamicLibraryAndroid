package com.fekracomputers.islamiclibrary.reading;

import com.fekracomputers.islamiclibrary.reading.fragments.BookPageFragment;

/**
 * This interface to be implemented by fragments of {@link BookPageFragment}
 *to listen to changes broadcast by the {@link DisplayOptionsPopupFragment}
 */

public interface DisplayPrefChangeListener {
    void setZoom(int newZoom);

    void setTashkeel(boolean tashkeelOn);

    void setPinchZoom(boolean pinchZoomOn);
}
