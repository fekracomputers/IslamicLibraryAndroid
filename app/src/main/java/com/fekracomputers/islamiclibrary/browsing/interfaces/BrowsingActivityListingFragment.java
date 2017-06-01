package com.fekracomputers.islamiclibrary.browsing.interfaces;

import com.fekracomputers.islamiclibrary.model.BookCatalogElement;

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

    void selecteItem(BookCatalogElement bookCatalogElement);
}
