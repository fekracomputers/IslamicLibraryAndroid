package com.fekracomputers.islamiclibrary.databases;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Locale;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.databases.DBValidator.DataBaseType.BOOK_DATABASE_TYPE;
import static com.fekracomputers.islamiclibrary.databases.DBValidator.DataBaseType.BOOK_INFORATION_DATABASE_TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by Mohammad on 4/1/2018.
 */

public class DBValidator {

    @NonNull
    private ArrayList<TableInformation> schema = new ArrayList<>();
    private boolean startValue = true;
    private ArrayList<Boolean> result;
    private Exception cause;

    public DBValidator(@DataBaseType int databaseType) {
        if (databaseType == BOOK_INFORATION_DATABASE_TYPE) {
            schema.add(new TableInformation(BooksInformationDBContract.AuthorEntry.TABLE_NAME
                    , new String[]{BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
                    BooksInformationDBContract.AuthorEntry.COLUMN_NAME_INFORMATION,
                    BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR,
                    BooksInformationDBContract.AuthorEntry.COLUMN_NAME_Birth_HIJRI_YEAR}
            ));
            schema.add(new TableInformation(BooksInformationDBContract.BookInformationEntery.TABLE_NAME
                    , new String[]{BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE,
                    BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_CARD,
                    BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_INFORMATION,
                    BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID,
                    BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ADD_DATE,
                    BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ACCESS_COUNT}));
            schema.add(new TableInformation(BooksInformationDBContract.CategotyEntry.TABLE_NAME
                    , new String[]{BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE,
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER,
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_PARENT_ID}));
            schema.add(new TableInformation(BooksInformationDBContract.BooksAuthors.TABLE_NAME
                    , new String[]{BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID,
                    BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID}));
            schema.add(new TableInformation(BooksInformationDBContract.BooksCategories.TABLE_NAME
                    , new String[]{BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID,
                    BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID}));
            schema.add(new TableInformation(BooksInformationDBContract.StoredBooks.TABLE_NAME
                    , new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID,
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID,
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS,
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG,
                    BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP}));
        } else if (databaseType == BOOK_DATABASE_TYPE) {
            schema.add(new TableInformation(BookDatabaseContract.InfoEntry.TABLE_NAME
                    , new String[]{
                    BookDatabaseContract.InfoEntry.COLUMN_NAME_NAME,
                    BookDatabaseContract.InfoEntry.COLUMN_NAME_VALUE}));

            schema.add(new TableInformation(BookDatabaseContract.PageEntry.TABLE_NAME
                    , new String[]{
                    BookDatabaseContract.PageEntry.COLUMN_NAME_PART_NUMBER,
                    BookDatabaseContract.PageEntry.COLUMN_NAME_PAGE_NUMBER,
                    BookDatabaseContract.PageEntry.COLUMN_NAME_PAGE,
                    BookDatabaseContract.PageEntry.COLUMN_NAME_PAGE_ID}));

            schema.add(new TableInformation(BookDatabaseContract.TitlesEntry.TABLE_NAME
                    , new String[]{
                    BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID,
                    BookDatabaseContract.TitlesEntry.COLUMN_NAME_PARENT_ID,
                    BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID,
                    BookDatabaseContract.TitlesEntry.COLUMN_NAME_TITLE}));

        }
    }

    void validate(@Nullable SQLiteOpenHelper sqLiteOpenHelper) {
        try {
            result = new ArrayList<>();
            SQLiteDatabase sqLiteDatabase;
            if (sqLiteOpenHelper != null) {
                sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
                for (int i = 0; i < schema.size(); i++) {
                    TableInformation tableInformation = schema.get(i);
                    Cursor c = null;
                    try {
                        c = sqLiteDatabase
                                .query(tableInformation.name
                                        , tableInformation.tables,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null, "1");
                        result.add(i, c.getCount() > -1);
                    } catch (Exception e) {
                        Timber.e(e, String.format(Locale.US, "exeption while validating table %s", tableInformation.name));
                        cause = e;
                        startValue = false;
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            } else {
                startValue = false;
            }
        } catch (Exception e) {
            Timber.e(e);
            cause = e;
            startValue = false;
        }
    }

    boolean isValid() {
        if (result == null) {
            throw new IllegalStateException("validate before calling this");
        }
        if (!startValue) {
            return false;
        } else {
            boolean resultBoolean = true;
            for (Boolean aBoolean : result) {
                resultBoolean = aBoolean & resultBoolean;
            }
            return resultBoolean;
        }
    }

    public Exception getCause() {
        return cause;
    }


    @Retention(SOURCE)
    @IntDef({BOOK_INFORATION_DATABASE_TYPE, BOOK_DATABASE_TYPE})
    @interface DataBaseType {
        int BOOK_INFORATION_DATABASE_TYPE = 0;
        int BOOK_DATABASE_TYPE = 1;
    }

}
