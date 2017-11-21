package com.fekracomputers.islamiclibrary.model;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mohammad Yahia on 15/11/2016.
 */
public class Bookmark extends UserNote implements Comparable<Bookmark> {
    public static boolean sortByDate = false;

    public Bookmark(int bookId, PageInfo pageInfo, String timeStampString, Title parentTitle) {
        super(bookId, pageInfo, parentTitle, timeStampString);
    }

    @Override
    public int compareTo(@NonNull Bookmark bookmark) {
        if (!sortByDate) {
            return this.pageInfo.pageId - bookmark.pageInfo.pageId;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            try {
                return sdf.parse(this.timeStampString).compareTo(sdf.parse(bookmark.timeStampString));

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
