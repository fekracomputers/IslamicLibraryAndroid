package com.fekracomputers.islamiclibrary.browsing.interfaces;

/**
 * بسم الله الرحمن الرحيم
 */
public interface BrowsingActivityListingFragment {
    void actionModeDestroyed();

    void actionModeStarted();

    void bookSelectionStatusUpdate();

    int getType();

    void BookDownloadStatusUpdate(int bookId, int downloadStatus);

    void switchTodownloadedOnly(boolean checked);

    void reAcquireCursors();

    void closeCursors();

    void selectAllItems(int id);
}
