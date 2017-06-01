package com.fekracomputers.islamiclibrary.browsing.adapters;

import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 6/3/2017.
 */

public class RecyclerViewPartialUpdatePayload {
    public static final int UPDATE_VISABILITY = 0;
    public static final int UPDATE_CHECKED = 1;
    public static final int UPDATE_DOWNLOAD_STATUS = 3;
    private int bookId;
    public int requestCode;
    public int downloadStatus = DownloadsConstants.STATUS_INVALID;
    private Boolean booleanValue;


    public RecyclerViewPartialUpdatePayload(int requestCode, Boolean value) {
        this.requestCode = requestCode;
        this.booleanValue = value;
    }

    public RecyclerViewPartialUpdatePayload(int requestCode, int bookId, int downloadStatus) {
        this.bookId = bookId;
        this.requestCode = requestCode;
        this.downloadStatus = downloadStatus;
    }

    public RecyclerViewPartialUpdatePayload(int requestCode) {
        this.requestCode = requestCode;
    }

    public Boolean booleanValue() {
        return booleanValue;
    }

    public int getBookId() {
        return bookId;
    }
}
