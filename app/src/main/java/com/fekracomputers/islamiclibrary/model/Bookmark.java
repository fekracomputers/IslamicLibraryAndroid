package com.fekracomputers.islamiclibrary.model;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mohammad Yahia on 15/11/2016.
 */
public class Bookmark implements Comparable<Bookmark> {
    private static final SimpleDateFormat SATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public static boolean sortByDate = false;
    public int bookId;
    public String timeStampString;
    public Title parentTitle;
    public PageInfo pageInfo;

    public Bookmark(int bookId, int pageId, int pageNumber, int partNumber, String timeStampString, Title parentTitle) {
        this.bookId = bookId;
        this.timeStampString = timeStampString;
        this.parentTitle = parentTitle;
        this.pageInfo = new PageInfo(pageId, partNumber, pageNumber);
    }

    @Override
    public int compareTo(@NonNull Bookmark bookmark) {
        if (!sortByDate) {
            return this.pageInfo.pageId - bookmark.pageInfo.pageId;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            try {

                sdf.parse(this.timeStampString).compareTo(sdf.parse(bookmark.timeStampString));

                return 0;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }


        }

    }

    public Date getDateTime() throws ParseException {
        return SATABASE_DATE_FORMAT.parse(timeStampString);
    }
}
