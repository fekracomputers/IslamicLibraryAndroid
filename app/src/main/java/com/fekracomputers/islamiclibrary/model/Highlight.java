package com.fekracomputers.islamiclibrary.model;

import android.content.ContentValues;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import com.fekracomputers.islamiclibrary.databases.UserDataDBContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mohammad Yahia on 21/12/2016.
 */

public class Highlight implements Comparable<Highlight> {


    private static final SparseIntArray highlightColorMap = new SparseIntArray();
    private static final SparseIntArray highlightDarkColorMap = new SparseIntArray();
    public static boolean sortByDate = false;
    private static final SimpleDateFormat SATABASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


    /** TODO binding the highlight class name to its color is currently in 4 places
     * highlight.css
     * Highlight.java
     * highlight_colors.xml
     * popup_text_selection.xml
     */
    static {
        highlightColorMap.append(1, 0xFFff7043);
        highlightColorMap.append(2, 0xFF00e5ff);
        highlightColorMap.append(3, 0xFFfbc02d);
        highlightColorMap.append(4, 0xFF8bc34a);
        highlightDarkColorMap.append(1, 0xFFffebee);
        highlightDarkColorMap.append(2, 0xFFe0f7fa);
        highlightDarkColorMap.append(3, 0xFFfff8e1);
        highlightDarkColorMap.append(4, 0xFFedf6e3);
    }

    public String timeStampString;
    public int bookId;
    public PageInfo pageInfo;
    public String text;
    public int id;
    public String className;
    public int containerElementId;
    public Title parentTitle;
    public String noteText;
    public long rowId;
    private static final Pattern HIGHLIGHT_CLASS_PATTERN = Pattern.compile("^highlight(\\d+)$");
    private static final Pattern HIGHLIGHT_PATTERN = Pattern.compile("^type:(\\w+)(?:\\|\\d+\\$\\d+\\$\\d+\\$\\w+\\$\\d*\\$[^|\\$]+(?:\\$[^|\\$]+)*)+$");

    public Highlight(String text, int id, String className, int containerElementId, String timeStampString, int bookId, int pageId, int partNumber, int pageNumber, Title parentTitle, long rowId, String noteText) {
        this.text = text;
        this.id = id;
        this.className = className;
        this.containerElementId = containerElementId;
        this.timeStampString = timeStampString;
        this.bookId = bookId;
        this.pageInfo=new PageInfo(pageId, partNumber, pageNumber);
        this.parentTitle = parentTitle;
        this.rowId = rowId;
        this.noteText = noteText;
    }


    Highlight(String text, int id, String className, int containerElementId, PageInfo pageInfo, String noteText, int bookId) {
        this.text = text;
        this.id = id;
        this.className = className;
        this.containerElementId = containerElementId;
        this.pageInfo = pageInfo;//TODO this is not good
        this.noteText = noteText;
        this.bookId = bookId;
    }



    @ColorInt
    public static int getHighlightColor(String className) {
        Matcher matcher = HIGHLIGHT_CLASS_PATTERN.matcher(className);
        int classNumber = 0;
        if (matcher.matches()) {
            classNumber = Integer.parseInt(matcher.group(1));
        }
        return highlightColorMap.get(classNumber);
    }

    @ColorInt
    public static int getDarkHighlightColor(String className) {

        Matcher matcher = HIGHLIGHT_CLASS_PATTERN.matcher(className);
        int classNumber = 0;
        if (matcher.matches()) {
            classNumber = Integer.parseInt(matcher.group(1));
        }

        return highlightDarkColorMap.get(classNumber);
    }

    public static <T> ArrayList<T> deserializeGeneric(String serialized, PageInfo pageInfo, Class<ContentValues> clazz, int bookId) {

        Matcher matcher = HIGHLIGHT_PATTERN.matcher(serialized);
        if (matcher.matches()) {

            String[] serializedHighlights = serialized.split("\\|");
            ArrayList<T> highlights = new ArrayList<>();
            for (int i = 1; i < serializedHighlights.length; i++) {

                String[] parts;
                parts = serializedHighlights[i].split("\\$");


          /*
                var parts = [
                characterRange.start,//0
                        characterRange.end,//1
                        highlight.id,//2
                        highlight.classApplier.className,//3
                        highlight.containerElementId//4
                        Text//5
                        Note//6
                ];
    */
                int containerElementId = 0;
                if (!parts[4].isEmpty()) {
                    containerElementId = Integer.valueOf(parts[4]);
                }
                String note = null;
                if (parts.length == 7 && !parts[6].isEmpty()) note = parts[6];
                Highlight highlight = new Highlight(parts[5], Integer.valueOf(parts[2]), parts[3], containerElementId, pageInfo, note,bookId);
                if (clazz.equals(Highlight.class)) {
                    highlights.add((T) highlight);

                } else if (clazz.equals(ContentValues.class)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_BOOK_ID, bookId);
                    contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID, highlight.pageInfo.pageId);
                    contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID, highlight.id);
                    contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME, highlight.className);
                    contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID, highlight.containerElementId);
                    contentValues.put(UserDataDBContract.HighlightEntry.COLUMN_TEXT, highlight.text);
                    highlights.add((T) contentValues);
                }

            }
            return highlights;

        } else {
            throw new Error("Serialized highlights are invalid.");
        }


    }


    public static ArrayList<Highlight> deserialize(String serialized, PageInfo pageInfo,int bookId) {
        final Pattern pattern = Pattern.compile("^type:(\\w+)(?:\\|(\\d+)\\$(\\d+)\\$(\\d+)\\$(\\w+)\\$(\\d*)\\$([^|]+))+$");
        Matcher matcher = pattern.matcher(serialized);
        if (matcher.matches()) {

            String[] serializedHighlights = serialized.split("\\|");
            ArrayList<Highlight> highlights = new ArrayList<>();
            for (int i = 1; i < serializedHighlights.length; i++) {

                String[] parts;
                parts = serializedHighlights[i].split("\\$");

          /*
                var parts = [
                characterRange.start,//0
                        characterRange.end,//1
                        highlight.id,//2
                        highlight.classApplier.className,//3
                        highlight.containerElementId//4
                        Text//5
                ];
    */
                int containerElementId = 0;
                if (!parts[4].isEmpty()) {
                    containerElementId = Integer.valueOf(parts[4]);
                }
                String note = null;
                if (parts.length == 7 && !parts[6].isEmpty()) note = parts[6];
                Highlight highlight = new Highlight(parts[5], Integer.valueOf(parts[2]), parts[3], containerElementId, pageInfo, note,bookId);

                highlights.add(highlight);
            }
            return highlights;

        } else {
            throw new Error("Serialized highlights are invalid.");
        }

    }


    @Override
    public int compareTo(@NonNull Highlight highlight) {
        if (!sortByDate) {
            return this.pageInfo.pageId - highlight.pageInfo.pageId;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);

            try {

                sdf.parse(this.timeStampString).compareTo(sdf.parse(highlight.timeStampString));

                return 0;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }


        }
    }


    public boolean hasNote() {
        return noteText != null && !noteText.isEmpty();
    }

    public Date getDateTime() throws ParseException {
        return SATABASE_DATE_FORMAT.parse(timeStampString);

    }
}


