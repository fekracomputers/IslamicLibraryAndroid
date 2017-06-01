package com.fekracomputers.islamiclibrary.databases;

/**
 * Created by Mohammad Yahia on 15/11/2016.
 */

public class UserDataDBContract {

    public static final String PRIMARY_KEY_start = ",PRIMARY KEY (";

    private UserDataDBContract() {
    }

    public static class BookmarkEntry {
        public static final String TABLE_NAME = "bookmarks";
        public static final String COLUMN_NAME_BOOK_ID = "bookId";
        public static final String COLUMN_NAME_PAGE_ID = "pageId";
        public static final String COLUMN_NAME_TIME_STAMP = "timeStamp";
        public static final String CREATE_STATEMENT = "create table "
                + TABLE_NAME + "( " +
                COLUMN_NAME_BOOK_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_NAME_PAGE_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_NAME_TIME_STAMP + SQL.TEXT + "DEFAULT (datetime('now','localtime'))" +
                PRIMARY_KEY_start + COLUMN_NAME_BOOK_ID + SQL.COMMA + COLUMN_NAME_PAGE_ID + ")" +
                ")";

    }

    public class SerializedHighlightEntry {
        public static final String COLUMN_NAME_PAGE_ID = "pageId";
        public static final String COLUMN_NAME_BOOK_ID = "bookId";
        public static final String COLUMN_NAME_SERIALIZED_HIGHLIGHTS = "serializedHighlightsString";
        public static final String TABLE_NAME = "SerializedHighlight";
        public static final String COLUMN_NAME_TIME_STAMP = "timeStamp";
        public static final String CREATE_STATEMENT = "create table "
                + TABLE_NAME + "( " +
                COLUMN_NAME_BOOK_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_NAME_PAGE_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_NAME_SERIALIZED_HIGHLIGHTS + SQL.TEXT + SQL.COMMA +
                COLUMN_NAME_TIME_STAMP + SQL.TEXT + "DEFAULT (datetime('now','localtime'))" +
                PRIMARY_KEY_start + COLUMN_NAME_BOOK_ID + SQL.COMMA + COLUMN_NAME_PAGE_ID + ")" +
                ")";
    }

    public class HighlightEntry {
        public static final String TABLE_NAME = "HighlightEntry";
        public static final String COLUMN_NAME_BOOK_ID = "bookId";
        public static final String COLUMN_NAME_PAGE_ID = "pageId";
        public static final String COLUMN_NAME_HIGHLIGHT_ID = "highlightId";
        public static final String COLUMN_CLASS_NAME = "className";
        public static final String COLUMN_CONTAINER_ELEMENT_ID = "containerElementId";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_NAME_TIME_STAMP = "timeStamp";
        public static final String COLUMN_NAME_ROW_ID = "rowid";
        public static final String COLUMN_NOTE_TEXT = "noteText";
        static final String TABLE_HIGHLIGHTS_CREATE = "create table "
                + TABLE_NAME + "( " +
                COLUMN_NAME_BOOK_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_NAME_PAGE_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_NAME_HIGHLIGHT_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_CLASS_NAME + SQL.TEXT + SQL.COMMA +
                COLUMN_CONTAINER_ELEMENT_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_TEXT + SQL.TEXT + SQL.COMMA +
                COLUMN_NOTE_TEXT + SQL.TEXT + SQL.COMMA +
                COLUMN_NAME_TIME_STAMP + SQL.TEXT + "DEFAULT (datetime('now','localtime'))" +
                PRIMARY_KEY_start + COLUMN_NAME_BOOK_ID + SQL.COMMA + COLUMN_NAME_PAGE_ID + SQL.COMMA + COLUMN_NAME_HIGHLIGHT_ID + ")" +
                ")";
    }

    public class DisplayPreferenceEntry {
        public static final String Table_NAME = "DisplayPreference";
        public static final String COLUMN_NAME_BOOK_ID = "bookId";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
        static final String CREATE_STATEMENT = "create table "
                + Table_NAME + "( " +
                COLUMN_NAME_BOOK_ID + SQL.INTEGER + SQL.COMMA +
                COLUMN_KEY + SQL.TEXT + SQL.COMMA +
                COLUMN_VALUE + SQL.TEXT +
                PRIMARY_KEY_start + COLUMN_NAME_BOOK_ID + SQL.COMMA + COLUMN_KEY + ")" +
                ")";
    }

    public class AccessInformationEntry {
        public static final String Table_NAME = "AccessInformation";
        public static final String COLUMN_NAME_BOOK_ID = "bookId";
        public static final String LAST_OPENED_TIME_STAMP = "lastOpened";
        public static final String LAST_OPENED_PAGE_ID = "lastOpenedId";
        public static final String LAST_OPENED_PAGE_NUMBER = "lastOpenedPageNumber";
        public static final String LAST_OPENED_PART_NUMBER = "lastOpenedPartNumber";
        public static final String COLUMN_ACCESS_COUNT = "accessCount";
        static final String CREATE_STATEMENT = "create table "
                + Table_NAME + "( " +
                COLUMN_NAME_BOOK_ID + SQL.INTEGER_PRIMARY_KEY + SQL.COMMA +
                LAST_OPENED_TIME_STAMP + SQL.TEXT + SQL.COMMA +
                LAST_OPENED_PAGE_ID + SQL.INTEGER + SQL.COMMA +
                LAST_OPENED_PAGE_NUMBER + SQL.INTEGER + SQL.COMMA +
                LAST_OPENED_PART_NUMBER + SQL.INTEGER + SQL.COMMA +
                COLUMN_ACCESS_COUNT + SQL.INTEGER +"DEFAULT 0 "+
                ")";
    }
    
}
