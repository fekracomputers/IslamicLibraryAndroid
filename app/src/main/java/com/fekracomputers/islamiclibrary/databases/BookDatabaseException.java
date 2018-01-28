package com.fekracomputers.islamiclibrary.databases;

import java.util.Locale;

/**
 * Created by Mohammad on 23/1/2018.
 */

public class BookDatabaseException extends Exception {
    private final Exception cause;
    private final int bookId;
    private final String bookPath;

    public BookDatabaseException(Exception cause, int bookId, String bookPath) {
        this.cause = cause;
        this.bookId = bookId;
        this.bookPath = bookPath;
    }

    @Override
    public String getMessage() {
        return String.format(Locale.US,
                "msg:%s, bookId:%d,filePath:%s",
                super.getMessage(),
                bookId,
                bookPath
        );
    }
}
