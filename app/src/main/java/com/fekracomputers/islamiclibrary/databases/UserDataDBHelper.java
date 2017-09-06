package com.fekracomputers.islamiclibrary.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fekracomputers.islamiclibrary.model.Bookmark;
import com.fekracomputers.islamiclibrary.model.Highlight;
import com.fekracomputers.islamiclibrary.model.PageInfo;

import java.util.ArrayList;

/**
 * Database Helper class to Store user data (BookMarks ,Notes ,Highlights and comments)
 * one instance per book
 * one file on disk per book
 */

public class UserDataDBHelper {
    private static final String TAG = "UserDataDBHelper";
    private static internalUserDBHelper sInternalUserDBHelper;
    private final Context context;
    private int bookId;

    public UserDataDBHelper(Context context, int bookId) {

        this.context = context;
        this.bookId = bookId;
    }


    public static synchronized UserDataDBHelper getInstance(Context context, int bookId) {
        if (sInternalUserDBHelper == null) {
            sInternalUserDBHelper = new internalUserDBHelper(context);
        }
        return new UserDataDBHelper(context, bookId);
    }

    public void addBookmark(int pageId) {
        sInternalUserDBHelper.addBookmark(pageId, bookId);
    }

    public void RemoveBookmark(int pageId) {
        sInternalUserDBHelper.RemoveBookmark(pageId, bookId);
    }

    public boolean isPageBookmarked(int pageId) {
        return sInternalUserDBHelper.isPageBookmarked(pageId, bookId);
    }

    public Highlight getHighlightById(int highlightId, int pageRowId) {
        return sInternalUserDBHelper.getHighlightById(highlightId, pageRowId, context, bookId);
    }

    public void addNoteToHighlight(Highlight mSelectedHighlight) {
        sInternalUserDBHelper.addNoteToHighlight(mSelectedHighlight, bookId);
    }

    public void setSerializedHighlights(PageInfo pageInfo, String serializedHighlights) {
        sInternalUserDBHelper.setSerializedHighlights(pageInfo, serializedHighlights, bookId);
    }

    public String getSerializedHighlights(int pageRowId) {
        return sInternalUserDBHelper.getSerializedHighlights(pageRowId, bookId);
    }

    public String getDisplayPreferenceValue(String preferenceKey, String s) {
        return sInternalUserDBHelper.getDisplayPreferenceValue(preferenceKey, s, bookId);
    }

    public void setDisplayPreferenceValue(String preferenceKey, String s) {
        sInternalUserDBHelper.setDisplayPreferenceValue(preferenceKey, s, bookId);
    }

    public ArrayList<Bookmark> getAllBookmarks(String order) {
        return sInternalUserDBHelper.getAllBookmarks(order, context, bookId);
    }

    public ArrayList<Highlight> getAllHighlights() {
        return sInternalUserDBHelper.getAllHighlights(context, bookId);
    }

    public void logBookAccess() {
        sInternalUserDBHelper.logBookAccess(bookId);
    }

    public void logPageAccess(PageInfo pageId) {
        sInternalUserDBHelper.logPageAccess(pageId, bookId);
    }


    public PageInfo getLastPageInfo() {
        return sInternalUserDBHelper.getLastPageInfo(bookId, context);
    }


    private static class internalUserDBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "user_data";
        private static final int DATABASE_VERSION = 1;

        private internalUserDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(UserDataDBContract.BookmarkEntry.CREATE_STATEMENT);
            sqLiteDatabase.execSQL(UserDataDBContract.SerializedHighlightEntry.CREATE_STATEMENT);
            sqLiteDatabase.execSQL(UserDataDBContract.HighlightEntry.TABLE_HIGHLIGHTS_CREATE);
            sqLiteDatabase.execSQL(UserDataDBContract.DisplayPreferenceEntry.CREATE_STATEMENT);
            sqLiteDatabase.execSQL(UserDataDBContract.AccessInformationEntry.CREATE_STATEMENT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        void addBookmark(int pageNumber, int bookId) {
            ContentValues bookmark = new ContentValues();
            bookmark.put(UserDataDBContract.BookmarkEntry.COLUMN_NAME_BOOK_ID, bookId);
            bookmark.put(UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID, pageNumber);
            getWritableDatabase().insert(UserDataDBContract.BookmarkEntry.TABLE_NAME, null, bookmark);
        }

        void RemoveBookmark(int pageId, int bookId) {
            getWritableDatabase().
                    delete(UserDataDBContract.BookmarkEntry.TABLE_NAME,
                            UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID + "=? and " + UserDataDBContract.BookmarkEntry.COLUMN_NAME_BOOK_ID + "=?",
                            new String[]{String.valueOf(pageId), String.valueOf(bookId)});
        }

        boolean isPageBookmarked(int pageNumber, int bookId) {
            return getReadableDatabase().query(
                    UserDataDBContract.BookmarkEntry.TABLE_NAME, new String[]{"1"},
                    UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID + "=? and " + UserDataDBContract.BookmarkEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(pageNumber), String.valueOf(bookId)},
                    null, null, null).moveToFirst();

        }


        /**
         * @param order    must be {@link UserDataDBContract.BookmarkEntry#COLUMN_NAME_PAGE_ID} or
         *                 {@link UserDataDBContract.BookmarkEntry#COLUMN_NAME_TIME_STAMP}
         * @param mContext
         * @param bookId
         * @return list of all book marks in the book ordered by page containing the book mark
         */
        public ArrayList<Bookmark> getAllBookmarks(String order, Context mContext, int bookId) throws IllegalArgumentException {
            if (!order.equals(UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID) ||
                    !order.equals(UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID)) {
                throw new IllegalArgumentException("order must be {@link UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID} or" +
                        " UserDataDBContract.BookmarkEntry.COLUMN_NAME_TIME_STAMP}");

            }

            ArrayList<Bookmark> bookmarksList = new ArrayList<>();

            Cursor c = getReadableDatabase().query(UserDataDBContract.BookmarkEntry.TABLE_NAME,
                    new String[]{UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID,
                            UserDataDBContract.BookmarkEntry.COLUMN_NAME_TIME_STAMP},
                    UserDataDBContract.BookmarkEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(bookId)},
                    null,
                    null,
                    UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID
            );
            final int INDEX_PAGE_ID = c.getColumnIndex(UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID);
            final int INDEX_TIME_STAMP = c.getColumnIndex(UserDataDBContract.BookmarkEntry.COLUMN_NAME_TIME_STAMP);
            BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(mContext, bookId);
            while (c.moveToNext()) {
                int pageId = c.getInt(INDEX_PAGE_ID);
                PageInfo pageInfo = bookDatabaseHelper.getPageInfoByPageId(pageId);

                bookmarksList.add(new Bookmark(bookId, pageId, pageInfo.pageNumber, pageInfo.partNumber, c.getString(INDEX_TIME_STAMP), bookDatabaseHelper.getParentTitle(pageId)));
            }
            c.close();
            return bookmarksList;
        }

        void setSerializedHighlights(PageInfo pageInfo, String serializedHighlights, int bookId) {

            ContentValues highlights = new ContentValues();
            highlights.put(UserDataDBContract.SerializedHighlightEntry.COLUMN_NAME_BOOK_ID, bookId);
            highlights.put(UserDataDBContract.SerializedHighlightEntry.COLUMN_NAME_PAGE_ID, pageInfo.pageId);
            highlights.put(UserDataDBContract.SerializedHighlightEntry.COLUMN_NAME_SERIALIZED_HIGHLIGHTS, serializedHighlights);
            getWritableDatabase().replace(UserDataDBContract.SerializedHighlightEntry.TABLE_NAME, null, highlights);
            deserializeHighlightsAndSave(serializedHighlights, pageInfo, bookId);
            //getReadableDatabase().insert(UserDataDBContract.SerializedHighlightEntry.TABLE_NAME, null, highlights);

        }

        public ArrayList<Highlight> getAllHighlights(Context mContext, int bookId) {
            ArrayList<Highlight> highlightArrayList = new ArrayList<>();

            Cursor c = getReadableDatabase().query(UserDataDBContract.HighlightEntry.TABLE_NAME,
                    new String[]{
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID,
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID,
                            UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME,
                            UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID,
                            UserDataDBContract.HighlightEntry.COLUMN_TEXT,
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_TIME_STAMP,
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_ROW_ID,
                            UserDataDBContract.HighlightEntry.COLUMN_NOTE_TEXT
                    },
                    UserDataDBContract.BookmarkEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(bookId)},
                    null,
                    null,
                    UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID
            );

            final int INDEX_PAGE_ID = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID);
            final int INDEX_HIGHLIGHT_ID = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID);
            final int INDEX_CLASS_NAME = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME);
            final int INDEX_ELEMENT_ID = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID);
            final int INDEX_TEXT = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_TEXT);
            final int INDEX_NOTE_TEXT = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NOTE_TEXT);
            final int INDEX_TIME_STAMP = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NAME_TIME_STAMP);
            final int INDEX_ROW_ID = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NAME_ROW_ID);
            BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(mContext, bookId);

            while (c.moveToNext()) {
                int pageId = c.getInt(INDEX_PAGE_ID);
                PageInfo pageInfo = bookDatabaseHelper.getPageInfoByPageId(pageId);
                int highlightId = c.getInt(INDEX_HIGHLIGHT_ID);
                String className = c.getString(INDEX_CLASS_NAME);
                int elementId = c.getInt(INDEX_ELEMENT_ID);
                String timeStamp = c.getString(INDEX_TIME_STAMP);
                String text = c.getString(INDEX_TEXT);
                String noteText = c.getString(INDEX_NOTE_TEXT);
                long rowId = c.getLong(INDEX_ROW_ID);

                highlightArrayList.add(new Highlight(text, highlightId, className, elementId, timeStamp, bookId, pageId, pageInfo.partNumber, pageInfo.pageNumber, bookDatabaseHelper.getParentTitle(pageId), rowId, noteText));
            }
            c.close();
            return highlightArrayList;
        }

        private void deserializeHighlightsAndSave(String serializedHighlights, PageInfo pageInfo, int bookId) {
            ArrayList<ContentValues> highlights = Highlight.deserializeGeneric(serializedHighlights, pageInfo, ContentValues.class, bookId);
            ArrayList<Integer> existingHighlightsId = new ArrayList<>(highlights.size());

            for (ContentValues highlight_current : highlights) {
                existingHighlightsId.add(highlight_current.getAsInteger(UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID));
            }
            String existingHighlightsIdString = existingHighlightsId.toString();

            existingHighlightsIdString = existingHighlightsIdString.replace('[', '(');
            existingHighlightsIdString = existingHighlightsIdString.replace(']', ')');
            SQLiteDatabase db = getWritableDatabase();

            db.beginTransaction();
            try {
                db.delete(UserDataDBContract.HighlightEntry.TABLE_NAME,
                        UserDataDBContract.HighlightEntry.COLUMN_NAME_BOOK_ID + "=?" + " and " +
                                UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID + "=?" + " and "
                                + UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID + " NOT IN  " + existingHighlightsIdString,
                        new String[]{String.valueOf(bookId), String.valueOf(pageInfo.pageId)}
                );

                for (ContentValues highlight : highlights) {
                    db.insertWithOnConflict(
                            UserDataDBContract.HighlightEntry.TABLE_NAME,
                            null,
                            highlight,
                            SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
            } catch (SQLException e) {
                Log.e(TAG, "deserializeHighlightsAndSave: ", e);
            } finally {
                db.endTransaction();
            }

        }

        String getSerializedHighlights(int mPageId, int bookId) {

            Cursor c = getReadableDatabase().query(UserDataDBContract.SerializedHighlightEntry.TABLE_NAME,
                    new String[]{UserDataDBContract.SerializedHighlightEntry.COLUMN_NAME_SERIALIZED_HIGHLIGHTS},
                    UserDataDBContract.SerializedHighlightEntry.COLUMN_NAME_PAGE_ID + "=? and " +
                            UserDataDBContract.SerializedHighlightEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(mPageId), String.valueOf(bookId)}, null, null, null
            );
            String serializedHighlights = "";
            if (c.moveToFirst()) {
                serializedHighlights = c.getString(0);
            }
            c.close();
            return serializedHighlights;
        }

        public Highlight getHighlightById(int highlightId, int pageId, Context mContext, int bookId) {
            Cursor c = getReadableDatabase().query(UserDataDBContract.HighlightEntry.TABLE_NAME,
                    new String[]{
                            UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME,
                            UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID,
                            UserDataDBContract.HighlightEntry.COLUMN_TEXT,
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_TIME_STAMP,
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_ROW_ID,
                            UserDataDBContract.HighlightEntry.COLUMN_NOTE_TEXT
                    },
                    UserDataDBContract.HighlightEntry.COLUMN_NAME_BOOK_ID + "=?" + " and " +
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID + "=?" + " and " +
                            UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID + "=?",
                    new String[]{String.valueOf(bookId), String.valueOf(pageId), String.valueOf(highlightId)},
                    null,
                    null,
                    UserDataDBContract.BookmarkEntry.COLUMN_NAME_PAGE_ID
            );


            final int INDEX_CLASS_NAME = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_CLASS_NAME);
            final int INDEX_ELEMENT_ID = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_CONTAINER_ELEMENT_ID);
            final int INDEX_TEXT = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_TEXT);
            final int INDEX_NOTE_TEXT = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NOTE_TEXT);
            final int INDEX_TIME_STAMP = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NAME_TIME_STAMP);
            final int INDEX_ROW_ID = c.getColumnIndex(UserDataDBContract.HighlightEntry.COLUMN_NAME_ROW_ID);
            BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(mContext, bookId);
            Highlight highlight = null;
            if (c.moveToFirst()) {
                PageInfo pageInfo = bookDatabaseHelper.getPageInfoByPageId(pageId);
                String className = c.getString(INDEX_CLASS_NAME);
                int elementId = c.getInt(INDEX_ELEMENT_ID);
                String timeStamp = c.getString(INDEX_TIME_STAMP);
                String text = c.getString(INDEX_TEXT);
                String noteText = c.getString(INDEX_NOTE_TEXT);
                long rowId = c.getLong(INDEX_ROW_ID);
                highlight = new Highlight(text, highlightId, className, elementId, timeStamp, bookId, pageId, pageInfo.partNumber, pageInfo.pageNumber, bookDatabaseHelper.getParentTitle(pageId), rowId, noteText);
            }
            c.close();
            return highlight;
        }

        void addNoteToHighlight(@NonNull Highlight highlight, int bookId) {
            if (highlight.hasNote()) {
                ContentValues highlightContent = new ContentValues();
                highlightContent.put(UserDataDBContract.HighlightEntry.COLUMN_NOTE_TEXT, highlight.noteText);
                getWritableDatabase().update(UserDataDBContract.HighlightEntry.TABLE_NAME, highlightContent,
                        UserDataDBContract.HighlightEntry.COLUMN_NAME_BOOK_ID + "=? and " +
                                UserDataDBContract.HighlightEntry.COLUMN_NAME_HIGHLIGHT_ID + "=? and " +
                                UserDataDBContract.HighlightEntry.COLUMN_NAME_PAGE_ID + "=?",
                        new String[]{String.valueOf(bookId), String.valueOf(highlight.id), String.valueOf(highlight.pageInfo.pageId)}
                );
            }

        }

        String getDisplayPreferenceValue(String key, String defaultValue, int bookId) {
            Cursor c = getReadableDatabase()
                    .query(UserDataDBContract.DisplayPreferenceEntry.Table_NAME,
                            new String[]{UserDataDBContract.DisplayPreferenceEntry.COLUMN_VALUE},
                            UserDataDBContract.DisplayPreferenceEntry.COLUMN_NAME_BOOK_ID + "=? and " +
                                    UserDataDBContract.DisplayPreferenceEntry.COLUMN_KEY + "=?",
                            new String[]{String.valueOf(bookId), key},
                            null,
                            null,
                            null);
            String value;
            if (c.moveToFirst()) {
                value = c.getString(0);
            } else {
                value = defaultValue;
                setDisplayPreferenceValue(key, value, bookId);
            }
            c.close();
            return value;
        }

        public void setDisplayPreferenceValue(String key, String value, int bookId) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(UserDataDBContract.DisplayPreferenceEntry.COLUMN_NAME_BOOK_ID, bookId);
            contentValue.put(UserDataDBContract.DisplayPreferenceEntry.COLUMN_KEY, key);
            contentValue.put(UserDataDBContract.DisplayPreferenceEntry.COLUMN_VALUE, value);
            getWritableDatabase().insertWithOnConflict(UserDataDBContract.DisplayPreferenceEntry.Table_NAME,
                    null, contentValue, SQLiteDatabase.CONFLICT_REPLACE);
        }


        void logBookAccess(int bookId) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValue = new ContentValues();
            contentValue.put(UserDataDBContract.AccessInformationEntry.COLUMN_NAME_BOOK_ID, bookId);
            db.insertWithOnConflict(UserDataDBContract.AccessInformationEntry.Table_NAME,
                    null,
                    contentValue,
                    SQLiteDatabase.CONFLICT_IGNORE);

            db.execSQL(" UPDATE " +
                            UserDataDBContract.AccessInformationEntry.Table_NAME +
                            " SET " + UserDataDBContract.AccessInformationEntry.COLUMN_ACCESS_COUNT + "=" +
                            UserDataDBContract.AccessInformationEntry.COLUMN_ACCESS_COUNT + "+1" +
                            SQL.WHERE + UserDataDBContract.AccessInformationEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(bookId)});
        }

        void logPageAccess(PageInfo pageInfo, int bookId) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValue = new ContentValues();
            contentValue.put(UserDataDBContract.AccessInformationEntry.COLUMN_NAME_BOOK_ID, bookId);
            db.insertWithOnConflict(UserDataDBContract.AccessInformationEntry.Table_NAME,
                    null,
                    contentValue,
                    SQLiteDatabase.CONFLICT_IGNORE);

            db.execSQL(" UPDATE " +
                            UserDataDBContract.AccessInformationEntry.Table_NAME +
                            " SET " + UserDataDBContract.AccessInformationEntry.LAST_OPENED_PAGE_ID + "=?" + SQL.COMMA +
                            UserDataDBContract.AccessInformationEntry.LAST_OPENED_PART_NUMBER + "=?" + SQL.COMMA +
                            UserDataDBContract.AccessInformationEntry.LAST_OPENED_PAGE_NUMBER + "=?" + SQL.COMMA +
                            UserDataDBContract.AccessInformationEntry.LAST_OPENED_TIME_STAMP + " = datetime('now','localtime')" +
                            SQL.WHERE + UserDataDBContract.AccessInformationEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(pageInfo.pageId),
                            String.valueOf(pageInfo.partNumber),
                            String.valueOf(pageInfo.pageNumber),
                            String.valueOf(bookId)});
        }

        public PageInfo getLastPageInfo(int bookId, Context context) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.query(UserDataDBContract.AccessInformationEntry.Table_NAME,
                    new String[]{UserDataDBContract.AccessInformationEntry.LAST_OPENED_PAGE_ID,
                            UserDataDBContract.AccessInformationEntry.LAST_OPENED_PART_NUMBER,
                            UserDataDBContract.AccessInformationEntry.LAST_OPENED_PAGE_NUMBER
                    },
                    UserDataDBContract.AccessInformationEntry.COLUMN_NAME_BOOK_ID + "=?",
                    new String[]{String.valueOf(bookId)}, null, null, null
            );
            final int LAST_OPENED_COULMN_INDEX = c.getColumnIndex(UserDataDBContract.AccessInformationEntry.LAST_OPENED_PAGE_ID);

            PageInfo pageInfo;
            if (c.moveToFirst() && !c.isNull(LAST_OPENED_COULMN_INDEX)) {

                pageInfo = new PageInfo(
                        c.getInt(LAST_OPENED_COULMN_INDEX),
                        c.getInt(c.getColumnIndex(UserDataDBContract.AccessInformationEntry.LAST_OPENED_PART_NUMBER)),
                        c.getInt(c.getColumnIndex(UserDataDBContract.AccessInformationEntry.LAST_OPENED_PAGE_NUMBER))
                );
            } else {
                BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(context, bookId);
                pageInfo = bookDatabaseHelper.getFirstPageInfo();
            }
            c.close();
            return pageInfo;

        }
    }


}
