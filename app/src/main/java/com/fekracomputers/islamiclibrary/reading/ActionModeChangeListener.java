package com.fekracomputers.islamiclibrary.reading;

/**
 * Created by Mohammad Yahia on 14/12/2016.
 */

public interface ActionModeChangeListener {

    void actionModeStarted();
    void actionModeFinished();
    void onBookmarkStateChange(boolean newBookmarkState, int pageId);
    void onContextualMenuItemClicked(int itemId, int currentItem);
}
