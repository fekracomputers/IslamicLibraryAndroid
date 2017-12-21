package com.fekracomputers.islamiclibrary.download.reciver;

import java.util.Locale;

/**
 * Created by Mohammad on 7/12/2017.
 */

public class FileDownloadException extends IllegalArgumentException {
    private int bookId = -1;
    private String fullFilePath;
    private int downloadManagerStatus;

    public FileDownloadException(String reason, Throwable cause, int bookId, String fullFilePath, int downloadManagerStatus) {
        super(reason, cause);
        this.bookId = bookId;
        this.fullFilePath = fullFilePath;
        this.downloadManagerStatus = downloadManagerStatus;
    }

    @Override
    public String getMessage() {
        return String.format(Locale.US,
                "msg:%s, bookId:%d,filePath:%s,downloadManagerStatus:%d",
                super.getMessage(),
                bookId,
                fullFilePath,
                downloadManagerStatus);
    }

    public int getBookId() {
        return bookId;
    }
}
