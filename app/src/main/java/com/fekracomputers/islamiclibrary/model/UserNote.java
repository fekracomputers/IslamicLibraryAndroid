package com.fekracomputers.islamiclibrary.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Mohammad on 9/11/2017.
 */

public abstract class UserNote {
    protected static final SimpleDateFormat SATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public int bookId;
    @Nullable
    public PageInfo pageInfo;
    public Title parentTitle;
    @Nullable
    public String timeStampString;

    public UserNote(int bookId, @Nullable PageInfo pageInfo, Title parentTitle, @Nullable String timeStampString) {
        this.bookId = bookId;
        this.pageInfo = pageInfo;
        this.parentTitle = parentTitle;
        this.timeStampString = timeStampString;
    }

    public UserNote(int bookId, PageInfo pageInfo) {
        this(bookId, pageInfo, null, null);
    }

    static Comparator<UserNote> getPageComparator() {
        return (o1, o2) -> Integer.valueOf(o1.pageInfo.pageId).compareTo(o2.pageInfo.pageId);
    }

    static Comparator<UserNote> getDateCompartor() {
        return (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                return sdf.parse(o1.timeStampString).compareTo(sdf.parse(o2.timeStampString));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        };
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    @Nullable
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(@Nullable PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Nullable
    public String getTimeStampString() {
        return timeStampString;
    }

    public void setTimeStampString(@Nullable String timeStampString) {
        this.timeStampString = timeStampString;
    }


}
