package com.fekracomputers.islamiclibrary.databases;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.service.UnZipIntentService;
import com.fekracomputers.islamiclibrary.model.AuthorInfo;
import com.fekracomputers.islamiclibrary.model.BookCategory;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.search.services.FtsIndexingService;
import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_FTS_INDEXING_ENDED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_NOT_DOWNLOAD;


/**
 * Created by Mohammad Yahia on 02/10/2016.
 */

public class BooksInformationDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_EXTENTION = "sqlite";
    public static final String DATABASE_COMPRESSED_EXTENTION = "zip";
    public static final String DATABASE_NAME = "BooksInformationDB";
    public static final String DATABASE_FULL_NAME = DATABASE_NAME + "." + DATABASE_EXTENTION;
    public static final String DATABASE_EXTENSION = "sqlite";
    public static final String DATABASE__JOURNAL_EXTENSION = "sqlite-journal";
    public static final Pattern uncompressedBookFileRegex = Pattern.compile("(^\\d+)\\." + DATABASE_EXTENSION + "$");
    public static final String COMPRESSION_EXTENSION = "zip";
    public static final Pattern compressedBookFileRegex = Pattern.compile("(^\\d+)\\." + COMPRESSION_EXTENSION + "$");
    public static final Pattern repeatedCompressedBookFileRegex = Pattern.compile("(^\\d+-\\d+)\\." + COMPRESSION_EXTENSION + "$");
    public static final String CREATE_INDEX_IF_NOT_EXISTS = " CREATE INDEX IF NOT EXISTS ";
    public static final String POPULATE_BOOKS_TITLES_FTS_SQL = "INSERT OR REPLACE INTO " + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME +
            "(" +
            BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_DOC_id + SQL.COMMA +
            BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_TITLE +
            ")" +
            "VALUES (" + "?" + SQL.COMMA + " ?" + ")";
    public static final String POPULATE_AUTHORS_NAMES_FTS_SQL = "INSERT OR REPLACE INTO " + BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME +
            "(" +
            BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_DOC_id + SQL.COMMA +
            BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_NAME +
            ")" +
            "VALUES (" + "?" + SQL.COMMA + " ?" + ")";
    private static final String COMMEMORATOR = " , ";
    private static final String DOTSEPARATOR = ".";
    public static final String[] BOOK_LISTING_PROJECTION = new String[]{BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID,
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME};
    private static final String AS = " as ";
    private static final String[] AUTHOUR_LISTING_COLUMNS_ARRAY_STORED = {
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR,
            "count(" + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + ")" + AS + BooksInformationDBContract.AuthorEntry.COUNT_OF_BOOKS,
            "1" + AS + BooksInformationDBContract.AuthorEntry.HAS_DOWNLOADED_BOOKS
    };
    private static final String[] AUTHOUR_LISTING_COLUMNS_ARRAY = {
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR,
            "count(" + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + ")" + AS + BooksInformationDBContract.AuthorEntry.COUNT_OF_BOOKS,
            "sum(" + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + ")" + " is not null " + AS + BooksInformationDBContract.AuthorEntry.HAS_DOWNLOADED_BOOKS
    };
    private static final String[] BOOK_INFORMATION_COLUMNS_ARRAY = new String[]{
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID,
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME,
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID,
            BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS

    };
    private static final String SELECT = "select ";
    private static final String JOIN = " join ";
    private static final String ON = " on ";
    private static final String WHERE = " where ";
    private static final String Equals = " = ";

    private static final String AUTHOURS_JOIN_BOOKS_AUTHORS =
            BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    " join " + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    ON + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    " left join " + BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
                    +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID;
    private static final String JOIN_AUTHOUR_NAME_FTS_SEARCH =
            JOIN + BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME +
                    ON +
                    BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_DOC_id
                    + Equals
                    + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID;
    private static final String AUTHOURS_JOIN_AUTHORS_FTS_JOIN_BOOKS_AUTHORS =
            BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    " join " + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    ON + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    JOIN_AUTHOUR_NAME_FTS_SEARCH +
                    " left join " + BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
                    +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID;

    private static final String STORED_BOOKS_LEFT_JOIN_BOOKS = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            " Left " + JOIN + BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
            ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;
    private static final String STORED_BOOKS_LEFT_JOIN_BOOKS_JOIN_AUTHRS = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            " Left " + JOIN + BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
            ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

            JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

            JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
            ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +
            JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
            ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
            Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;
    private static final String BOOKS_LEFTJOIN_STORED_BOOKS_JOIN_AUTHRS =
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
                    " Left " + JOIN +
                    BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
                    Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
                    Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    Equals +
                    BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +

                    JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
                    ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
                    Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;
    private static final String[] CATEGORY_COLUMNS = {
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER,
            "count(" + BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + ")" + AS + BooksInformationDBContract.CategotyEntry.COUNT_OF_BOOKS,
            "sum(" + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + ")" + " is not null " + AS + BooksInformationDBContract.CategotyEntry.HAS_DOWNLOADED_BOOKS

    };
    private static final String[] CATEGORY_COLUMNS_STORED = {
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER,
            "count(" + BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + ")" + AS + BooksInformationDBContract.CategotyEntry.COUNT_OF_BOOKS
    };
    private static final String CATEGORIES_TABLES = BooksInformationDBContract.CategotyEntry.TABLE_NAME +
            " join " + BooksInformationDBContract.BooksCategories.TABLE_NAME +
            ON + BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID +
            "=" + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
            " left join " + BooksInformationDBContract.StoredBooks.TABLE_NAME +
            ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
            +
            "=" + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID;
    private static final String STORED_CATEGORIES_TABLES = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
            ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            Equals +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
            JOIN + BooksInformationDBContract.CategotyEntry.TABLE_NAME + ON +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
            Equals +
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID;
    private static final String STORED_AUTHORS_TABLES = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME + ON +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID;


    private static final String STORED_AUTHORS_TABLES_JOIN_AUTHOR_FTS = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME + ON +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID
            + JOIN_AUTHOUR_NAME_FTS_SEARCH;
    private static final String BOOKS_JOIN_AUTHORS = BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            JOIN + BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
            ON +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

            JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
            ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID;
    private static final String BOOKS_JOIN_AUTHORS_JOIN_CAT = BOOKS_JOIN_AUTHORS + JOIN +
            BooksInformationDBContract.BooksCategories.TABLE_NAME +
            ON +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
            Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            JOIN + BooksInformationDBContract.CategotyEntry.TABLE_NAME +
            ON +
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID +
            Equals +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID;
    private static final String JOIN_DOWNLOADED_FOR_AUTHORS = JOIN +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + ON +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
            + Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID;
    private static final String JOIN_DOWNLOADED_FOR_CATEGORIES = JOIN +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + ON +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
            + Equals +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID;
    private static final String FROM = " from ";
    private static final String FROM_BOOKS_JOIN_AUTHORS =
            FROM + BOOKS_JOIN_AUTHORS;
    private static final String FROM_BOOKS_JOIN_AUTHORS_JOIN_CAT = FROM + BOOKS_JOIN_AUTHORS_JOIN_CAT;
    private static final String ORDER_BY = " order by ";
    private static final String[] DEFULT_CATEGORIES = new String[]{"العقيدة",
            "الفرق والردود",
            "التفاسير",
            "علوم القران",
            "التجويد والقراءات",
            "متون الحديث",
            "الاجزاء الحديثية",
            "مخطوطات حديثية",
            "كتب ابن ابي الدنيا",
            "شروح الحديث",
            "كتب التخريج والزوائد",
            "كتب الالباني",
            "العلل والسؤالات",
            "علوم الحديث",
            "اصول الفقه والقواعد الفقهية",
            "فقه حنفي",
            "فقه مالكي",
            "فقه شافعي",
            "فقه حنبلي",
            "فقه عام",
            "بحوث ومسائل",
            "السياسة الشرعية والقضاء",
            "الفتاوى",
            "كتب ابن تيمية",
            "كتب ابن القيم",
            "الرقاق والاداب والاذكار",
            "السيرة والشمائل",
            "التاريخ",
            "التراجم والطبقات",
            "الانساب",
            "البلدان والجغرافيا والرحلات",
            "كتب اللغة",
            "الغريب والمعاجم ولغة الفقه",
            "النحو والصرف",
            "الادب والبلاغة",
            "الدواوين الشعرية",
            "الجوامع والمجلات ونحوها",
            "فهارس الكتب والادلة",
            "محاضرات مفرغة",
            "الدعوة واحوال المسلمين",
            "كتب اسلامية عامة", "علوم اخرى"};
    private static final String INTEGER = " integer ";
    private static final String TEXT = " TEXT ";
    private static final String COMMA = " , ";
    private static final String UNIQUE = " unique ";
    private static final String CREATE_STORED_INFO = " CREATE TABLE IF NOT EXISTS "
            + BooksInformationDBContract.StoredBooks.TABLE_NAME + "( " +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + " integer primary key" + COMMA +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + INTEGER + UNIQUE + COMMA +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + INTEGER + COMMA +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG + INTEGER + ")";
    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_BOOK_TITLES_FTS_TABLE = "CREATE VIRTUAL TABLE IF NOT EXISTS " +
            BooksInformationDBContract.BookNameTextSearch.TABLE_NAME +
            " USING fts4(content=\"\"" + SQL.COMMA + BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_TITLE + ")";
    private static final String OPTIMIZE_BOOK_TITLES_FTS = " INSERT INTO " +
            BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + "(" + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + ")" +
            "VALUES('optimize')";
    private static final String CREATE_AUTHORS_NAMES_FTS_TABLE = "CREATE VIRTUAL TABLE IF NOT EXISTS " +
            BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME +
            " USING fts4(content=\"\"" + SQL.COMMA + " " + BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_NAME + ")";
    private static final String OPTIMIZE_AUTHORS_NAME_FTS = " INSERT INTO " +
            BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME + "(" + BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME + ")" +
            "VALUES('optimize')";

    private static final String JOIN_BOOKS_TITLES_FTS_SEARCH = JOIN + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + ON +
            BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_DOC_id
            + Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;

    private static final String BOOKS_LEFT_JOIN_STORED_BOOKS_JOIN_AUTHORS =
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
                    " Left " + JOIN +
                    BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
                    Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
                    Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    Equals +
                    BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +

                    JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
                    ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
                    Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;


    private static BooksInformationDbHelper sInstance;
    private static String sDatabasePath;
    private final String TAG = "InfoDbHelper";

    private BooksInformationDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Static Factory method for this singleton class
     * <p>
     * Consider using {@link #databaseExists()} before calling this method
     *
     * @return the instance if the Book Information Database already exists else returns  null
     */
    @Nullable
    public synchronized static BooksInformationDbHelper getInstance(@NonNull Context context) {
        if (sDatabasePath == null) {
            sDatabasePath = StorageUtils.getIslamicLibraryShamelaBooksDir(context) + File.separator +
                    DATABASE_FULL_NAME;
        }
        if (sInstance == null) {
            if (!databaseExists()) return null;
            sInstance = new BooksInformationDbHelper(context, sDatabasePath, null, DATABASE_VERSION);
        }
        return sInstance;
    }

    public synchronized static void clearInstance(Context context) {
        sDatabasePath = null;
        sInstance = null;
        getInstance(context);
    }

    /**
     * @return true if a file named as the bookInformation database exists, doesn't check its content
     */
    public static boolean databaseExists() {
        return new File(sDatabasePath).exists();
    }

    public static boolean databaseExists(@NonNull Context context) {
        if (sDatabasePath == null) {
            sDatabasePath = StorageUtils.getIslamicLibraryShamelaBooksDir(context) + File.separator +
                    DATABASE_FULL_NAME;
        }
        return new File(sDatabasePath).exists();
    }

    @NonNull
    public static String getFTSSelectionStringForBooks() {
        //books.id in (select BookNameTextSearch.docid from BookNameTextSearch where title match "ا*")
        return
                BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID
                        + SQL.IN + "(" + SQL.SELECT
                        + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + "." + BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_DOC_id
                        + SQL.FROM
                        + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME
                        + SQL.WHERE
                        + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + "." + BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_TITLE + " match ? "
                        + ")";
    }

    public String getBookName(int book_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(BooksInformationDBContract.BookInformationEntery.TABLE_NAME,
                new String[]{BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE},
                BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID + "=?",
                new String[]{String.valueOf(book_id)},
                null,
                null,
                null);
        String name = "";
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }

    public int getBookCategoryId(int bookId) {

        return (int) DatabaseUtils.longForQuery(getReadableDatabase(),
                " SELECT " +
                        BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
                        " FROM " +
                        BooksInformationDBContract.BooksCategories.TABLE_NAME +
                        " WHERE " +
                        BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID + "=?",
                new String[]{String.valueOf(bookId)}
        );

    }

    public BookInfo getBookDetails(int book_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor bookInformationCursor;
        try {

            bookInformationCursor = db.query(BooksInformationDBContract.BookInformationEntery.TABLE_NAME,
                    new String[]{
                            BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE,
                            BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_INFORMATION,
                            BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_CARD
                    },
                    BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID + "=?",
                    new String[]{String.valueOf(book_id)},
                    null,
                    null,
                    null
            );
            BookCategory category = getBookCategory(book_id);
            AuthorInfo authorInfo = getAuthorInfoByBookId(book_id);
            if (bookInformationCursor.moveToFirst()) {
                BookInfo bookInfo = new BookInfo(
                        book_id,
                        bookInformationCursor.getString(bookInformationCursor.getColumnIndex(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE)),
                        bookInformationCursor.getString(bookInformationCursor.getColumnIndex(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_INFORMATION)),
                        bookInformationCursor.getString(bookInformationCursor.getColumnIndex(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_CARD)),
                        authorInfo,
                        category

                );
                bookInformationCursor.close();
                return bookInfo;
            }
            bookInformationCursor.close();

        } catch (SQLException e) {
            Log.e(TAG, "Catch a SQLiteException when queryBookListing: ", e);
        }
        return null;
    }

    private AuthorInfo getAuthorInfoByBookId(int book_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor authorInformationCursor;
        try {

            authorInformationCursor = db.rawQuery(
                    SELECT +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + COMMA +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR + COMMA +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_INFORMATION + COMMA +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME +

                            FROM + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                            WHERE + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + " = " +
                            "(" + SELECT + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                            FROM + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                            WHERE + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID + "=?" +
                            ")"
                    , new String[]{String.valueOf(book_id)}
            );
            if (authorInformationCursor.moveToFirst()) {
                AuthorInfo authorInfo = new AuthorInfo(
                        authorInformationCursor.getInt(authorInformationCursor.getColumnIndex(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID)),
                        authorInformationCursor.getString(authorInformationCursor.getColumnIndex(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME)),
                        authorInformationCursor.getString(authorInformationCursor.getColumnIndex(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_INFORMATION)),
                        authorInformationCursor.getInt(authorInformationCursor.getColumnIndex(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR))
                );
                authorInformationCursor.close();
                return authorInfo;
            }
            authorInformationCursor.close();

        } catch (SQLException e) {
            Log.e(TAG, "Catch a SQLiteException when getAuthorInfoByBookId: ", e);
        }
        return null;

    }

    private BookCategory getBookCategory(int book_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor categoryInformationCursor;
        try {
            categoryInformationCursor = db.rawQuery(
                    SELECT +
                            BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + COMMA +
                            BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE +

                            FROM + BooksInformationDBContract.CategotyEntry.TABLE_NAME +
                            WHERE + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + " = " +
                            "(" + SELECT + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
                            FROM + BooksInformationDBContract.BooksCategories.TABLE_NAME +
                            WHERE + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID + "=?" +
                            ")"
                    , new String[]{String.valueOf(book_id)}
            );
            if (categoryInformationCursor.moveToFirst()) {
                BookCategory category = new BookCategory(
                        categoryInformationCursor.getInt(categoryInformationCursor.getColumnIndex(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID)),
                        categoryInformationCursor.getString(categoryInformationCursor.getColumnIndex(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE))
                );

                categoryInformationCursor.close();
                return category;
            }
            categoryInformationCursor.close();

        } catch (SQLException e) {
            Log.e(TAG, "Catch a SQLiteException when getBookCategory: ", e);
        }

        return null;
    }

    /**
     * @param selection     A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows
     * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
     * @return a cursor over the result set coulms are those specified by {@link BooksInformationDbHelper#BOOK_LISTING_PROJECTION}
     */
    private Cursor queryBookListing(String selection, String[] selectionArgs) {

        SQLiteDatabase db = this.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(BOOKS_JOIN_AUTHORS);


        return builder.query(db,
                BOOK_LISTING_PROJECTION,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public List<BookCategory> getCategoriesFiltered(String selection, String[] selectionArgs, String orderBy, boolean downloadedOnly) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<BookCategory> bookCategories = new ArrayList<>();
        int idIdx;
        int nameIdx;
        int orderIdx;
        int numberOfBooksIndex;
        int has_downloaded_books_index = -1;
        Cursor c = null;
        try {
            if (!downloadedOnly) {
                c = db.query(false, CATEGORIES_TABLES,
                        CATEGORY_COLUMNS,
                        selection,
                        selectionArgs,
                        BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
                        null,
                        orderBy,
                        null
                );


            } else {
                c = db.query(false,
                        STORED_CATEGORIES_TABLES,
                        CATEGORY_COLUMNS_STORED,
                        selection,
                        selectionArgs,
                        BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
                        null,
                        orderBy,
                        null
                );
            }
            idIdx = c.getColumnIndexOrThrow(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID);
            nameIdx = c.getColumnIndexOrThrow(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE);
            orderIdx = c.getColumnIndexOrThrow(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER);
            numberOfBooksIndex = c.getColumnIndexOrThrow(BooksInformationDBContract.CategotyEntry.COUNT_OF_BOOKS);
            if (!downloadedOnly)
                has_downloaded_books_index = c.getColumnIndexOrThrow(BooksInformationDBContract.CategotyEntry.HAS_DOWNLOADED_BOOKS);
            while (c.moveToNext()) {
                BookCategory bookCategory = new BookCategory(
                        c.getInt(idIdx),
                        c.getInt(orderIdx),
                        c.getString(nameIdx),
                        c.getInt(numberOfBooksIndex), downloadedOnly || c.getInt(has_downloaded_books_index) == 1);
                bookCategories.add(bookCategory);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Catch a SQLiteException when queryBookListing: ", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bookCategories;
    }

    public HashSet<Integer> getBooksIdsFilteredOnDownloadStatus(String selection, String[] selectionArgs) {
        HashSet<Integer> selectedBookInfoItems = new HashSet<>();
        Cursor c = getReadableDatabase().query(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID},
                selection,
                selectionArgs,
                null,
                null,
                null);
        while (c.moveToNext()) {
            selectedBookInfoItems.add(c.getInt(0));
        }
        c.close();
        return selectedBookInfoItems;
    }

    public HashSet<Integer> getBooksIdsSetByCategoryId(int categoryId, boolean downloadedOnly) {
        HashSet<Integer> selectedBookInfoItems = new HashSet<>();
        Cursor c = getReadableDatabase().query(BooksInformationDBContract.BooksCategories.TABLE_NAME +
                        (downloadedOnly ? JOIN_DOWNLOADED_FOR_CATEGORIES : ""),

                new String[]{BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID},
                BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)}, null, null, null);
        while (c.moveToNext()) {
            selectedBookInfoItems.add(c.getInt(0));
        }
        c.close();
        return selectedBookInfoItems;
    }

    public HashSet<Integer> getBooksSetAuthorId(int authorId, boolean downloadedOnly) {
        HashSet<Integer> selectedBookInfoItems = new HashSet<>();
        Cursor c = getReadableDatabase().query(BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                        (downloadedOnly ? JOIN_DOWNLOADED_FOR_AUTHORS : ""),
                new String[]{BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID},
                BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + "=?",
                new String[]{String.valueOf(authorId)}, null, null, null);
        while (c.moveToNext()) {
            selectedBookInfoItems.add(c.getInt(0));
        }
        c.close();
        return selectedBookInfoItems;
    }

    public Cursor getBooksFiltered(String selection, String[] selectionArgs, String orderBy, boolean downloadedOnly, String limit) {
        Cursor c = null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (orderBy != null && orderBy.equals(BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR))
            orderBy = AuthorHijriDeathYearOrderByHandling();

        boolean joinFTS = (selection != null && selection.contains(BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_TITLE));
        String tables = "";
        if (!downloadedOnly && !joinFTS) {
            tables = BOOKS_LEFTJOIN_STORED_BOOKS_JOIN_AUTHRS;
        } else if (downloadedOnly && !joinFTS) {
            tables = STORED_BOOKS_LEFT_JOIN_BOOKS_JOIN_AUTHRS;
        } else if (!downloadedOnly && joinFTS) {
            tables = BOOKS_LEFTJOIN_STORED_BOOKS_JOIN_AUTHRS;

        } else if (downloadedOnly && joinFTS) {
            tables = STORED_BOOKS_LEFT_JOIN_BOOKS_JOIN_AUTHRS;

        }

        try {
            c = db.query(false, tables
                    ,
                    BOOK_INFORMATION_COLUMNS_ARRAY,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderBy,
                    limit
            );

        } catch (SQLException e) {
            Log.e(TAG, "Catch a SQLiteException when queryBookListing: ", e);
        }
        return c;
    }

    /**
     * @param selection      selection a
     * @param selectionArgs
     * @param orderBy        How to order the rows, formatted as an SQL ORDER BY clause
     *                       (excluding the ORDER BY itself). Passing null will use the default sort order,
     *                       which may be unordered.
     *                       This should be an array containing coulmn names from {@link BooksInformationDBContract.AuthorEntry}
     * @param downloadedOnly whether to include only the downloaded books
     * @return a cursor with coulmns of {@link BooksInformationDbHelper#AUTHOUR_LISTING_COLUMNS_ARRAY}
     */
    public Cursor getAuthorsFiltered(String selection, String[] selectionArgs, String[] orderBy, boolean downloadedOnly) {
        String orderByString = authorOrderByString(orderBy);
        boolean joinFTS = (selection != null && selection.contains(BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_NAME));
        Cursor c = null;
        String tables = "";
        String[] selectionCoulnsArray = new String[]{};
        SQLiteDatabase db = this.getReadableDatabase();
        if (!downloadedOnly && !joinFTS) {
            tables = AUTHOURS_JOIN_BOOKS_AUTHORS;
            selectionCoulnsArray = AUTHOUR_LISTING_COLUMNS_ARRAY;
        } else if (downloadedOnly && !joinFTS) {
            tables = STORED_AUTHORS_TABLES;
            selectionCoulnsArray = AUTHOUR_LISTING_COLUMNS_ARRAY_STORED;

        } else if (!downloadedOnly && joinFTS) {
            tables = AUTHOURS_JOIN_AUTHORS_FTS_JOIN_BOOKS_AUTHORS;
            selectionCoulnsArray = AUTHOUR_LISTING_COLUMNS_ARRAY;

        } else if (downloadedOnly && joinFTS) {
            tables = STORED_AUTHORS_TABLES_JOIN_AUTHOR_FTS;
            selectionCoulnsArray = AUTHOUR_LISTING_COLUMNS_ARRAY_STORED;

        }
        try {

            c = db.query(false, tables,
                    selectionCoulnsArray,
                    selection,
                    selectionArgs,
                    BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
                    null,
                    orderByString,
                    null);

        } catch (SQLException e) {
            Log.e(TAG, "Catch a SQLiteException when queryBookListing: ", e);
        }
        return c;
    }

    private boolean orderByContainsNumberOfBooks(String[] orderBy) {
        for (String s : orderBy) {
            if (s.equals(BooksInformationDBContract.AuthorEntry.ORDER_BY_NUMBER_OF_BOOKS))
                return true;
        }
        return false;
    }

    private String AuthorHijriDeathYearOrderByHandling() {
        return "case when " +
                BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR +
                "=-1 then 1 else 0 end " +
                "," +
                BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR;
    }

    @Nullable
    private String authorOrderByString(String[] orderBy) {
        String orderByString;
        if (orderBy != null && orderBy.length != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String anOrderBy : orderBy) {
                switch (anOrderBy) {
                    //this is special handle to make the authours who didn't die marked with -1 death year appear last in ascending order
                    case BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR:
                        stringBuilder
                                .append(AuthorHijriDeathYearOrderByHandling());
                        break;
                    case BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME:
                    case BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID:
                    case BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_INFORMATION:
                        stringBuilder.append(anOrderBy);
                        break;
                    case BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.ORDER_BY_NUMBER_OF_BOOKS:
                        stringBuilder.append("count(" + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + ") Desc");
                        break;
                    default:
                        throw new IllegalArgumentException(anOrderBy + " is Not a legal order paraeter");
                }
                stringBuilder.append(',');
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.setLength(stringBuilder.length() - 1);
                orderByString = stringBuilder.toString();
            } else {
                orderByString = null;
            }
        } else {
            orderByString = null;
        }
        return orderByString;
    }

    /**
     * Checks whether the file with the specified id otherwise it adds its information to stored books database
     *
     * @param db
     * @param bookId book to check
     */
    private void checkFileInDbOrInsert(SQLiteDatabase db, int bookId, Context context
    ) {

        Cursor c = db.query(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS},
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{Integer.toString(bookId)},
                null, null, null
        );
        if (c.moveToFirst()) {//the book already added
            int status = c.getInt(0);
            if (status >= DownloadsConstants.STATUS_FTS_INDEXING_ENDED) {//this book is alrready in the db and fully confiured
                ContentValues contentValues = new ContentValues();
                contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG, BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_PRESENT);
                db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                        contentValues, BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                        new String[]{String.valueOf(bookId)});
            } else if (status <= DownloadsConstants.STATUS_UNZIP_ENDED) {
                BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(context, bookId);
                if (bookDatabaseHelper.isFtsSearchable()) {
                    updateStoredBookStatus(db, bookId, DownloadsConstants.STATUS_FTS_INDEXING_ENDED);
                } else {
                    queryIndexing(bookId, context);
                    updateStoredBookStatus(db, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
                }
                bookDatabaseHelper.close();
            } else if (status == DownloadsConstants.STATUS_FTS_INDEXING_STARTED)
            //it started but was not marked as finished may be it is now being indexed or may be it was corrupted
            //This must not happen since the database transaction should roll back
            {
                queryIndexing(bookId, context);
                updateStoredBookStatus(db, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
            }


        } else//book wasn't added before
        {
            BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(context, bookId);
            if (bookDatabaseHelper.isFtsSearchable()) {
                insertStoredBook(db, bookId, DownloadsConstants.STATUS_FTS_INDEXING_ENDED);
            } else {
                insertStoredBook(db, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
                queryIndexing(bookId, context);
            }
            bookDatabaseHelper.close();
        }
        c.close();
    }

    private void queryIndexing(int bookId, Context context) {
        Intent ftsIndexingServiceIntent = new Intent(context, FtsIndexingService.class);
        ftsIndexingServiceIntent.putExtra(EXTRA_DOWNLOAD_BOOK_ID, bookId);
        context.startService(ftsIndexingServiceIntent);
    }

    private void updateStoredBookStatus(SQLiteDatabase db, int bookId, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG, BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_PRESENT);
        db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                contentValues, BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{String.valueOf(bookId)});
    }

    private void insertStoredBook(SQLiteDatabase db, int bookId, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID, bookId);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG, BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_PRESENT);
        db.insertWithOnConflict(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                null,
                contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Scan the program directory and check each book data base file against the StoredBooks Database
     *
     * @return true if the directory  already exist and files were refreshed , false if the directory didn't exist or was empty in yhis case the directory is created
     */

    public boolean refreshBooksDbWithDirectory(Context context) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG,
                VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT);
        db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                contentValues, null, null);

        File booksDir = new File(StorageUtils.getIslamicLibraryShamelaBooksDir(context));
        if (!(booksDir.exists() && booksDir.isDirectory())) {
            booksDir.mkdirs();
            return false;
        } else {
            String[] files = booksDir.list();
            if (files.length == 0) {
                return false;
            }

            db.beginTransaction();
            try {
                for (String file : files) {
                    //validate file name against <integer>.sqlite
                    Matcher matcher = uncompressedBookFileRegex.matcher(file);
                    if (matcher.matches()) {
                        int book_id = Integer.parseInt(matcher.group(1));
                        checkFileInDbOrInsert(db, book_id, context);
                    } else {
                        Matcher compressedMatcher = compressedBookFileRegex.matcher(file);
                        if (compressedMatcher.matches()) {
                            int bookId = Integer.parseInt(compressedMatcher.group(1));

                            Intent localIntent =
                                    new Intent(BROADCAST_ACTION)
                                            // Puts the status into the Intent
                                            .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_WAITING_FOR_UNZIP)
                                            .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                            context.sendOrderedBroadcast(localIntent, null);

                            Intent serviceIntent = new Intent(context, UnZipIntentService.class);
                            serviceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, booksDir + File.separator + file);
                            context.startService(serviceIntent);
                            // Broadcasts the Intent to receivers in this app.

                        }
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            //delete book entries that doesn't have files in file system
            db.delete(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG + "=?",
                    new String[]{String.valueOf(BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT)}
            );

        }
        return true;
    }

    public ArrayList<Long> getPendingDownloads() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(STORED_BOOKS_LEFT_JOIN_BOOKS,
                new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID},
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + "<?",
                new String[]{Integer.toString(DownloadsConstants.STATUS_DOWNLOAD_COMPLETED)},
                null,
                null,
                null);
        final int ENQU_ID_INDEX = c.getColumnIndex(BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID);
        ArrayList<Long> pendingDownloadsId = new ArrayList<>();
        while (c.moveToNext()) {
            pendingDownloadsId.add(c.getLong(ENQU_ID_INDEX));
        }
        c.close();
        return pendingDownloadsId;
    }

    public void addDownload(int bookId, long enqueueId, int downloadStatus) {
        //TODO what if the book id already exisited ?
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID, enqueueId);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, downloadStatus);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID, bookId);
        db.insertWithOnConflict(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                null,
                contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long getBookIdByDownloadId(long enquId) {
        return DatabaseUtils.longForQuery(getReadableDatabase(),
                SELECT +
                        BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
                        SQL.FROM +
                        BooksInformationDBContract.StoredBooks.TABLE_NAME +
                        SQL.WHERE +
                        BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "=?",
                new String[]{Long.toString(enquId)}
        );
    }

    /**
     * @param enqueueId      The download reference got from DownloadManager
     * @param downloadStatus the status to register see {@link DownloadsConstants}
     * @return true if this download reference already exist in the database i.e this download was requsted by this app
     */
    public boolean setDownloadStatusByEnquId(long enqueueId, int downloadStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, downloadStatus);
        int i = db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME, contentValues,
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "=?",
                new String[]{Long.toString(enqueueId)});
        return i == 1;
    }

    /**
     * Sets the book status bu book id
     * <br>
     * this method can't be used to set status for the book for the first time
     *
     * @param bookId The Book id
     * @param status the status to register see {@link DownloadsConstants}
     * @return true if this download reference already exist in the database
     */
    public boolean setStatus(int bookId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        int i = db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME, contentValues,
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{Long.toString(bookId)});
        return i == 1;
    }

    /**
     * @param bookId book id to search for
     * @return download status as specified in {@link DownloadsConstants}
     */
    public int getBookDownloadStatus(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS},
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{Integer.toString(bookId)},
                null,
                null,
                null);
        int downloadStatus;
        if (c.moveToFirst()) {
            if (c.isNull(0))
                downloadStatus = STATUS_NOT_DOWNLOAD;
            else
                downloadStatus = c.getInt(0);
        } else {
            downloadStatus = STATUS_NOT_DOWNLOAD;
        }
        c.close();
        return downloadStatus;
    }

    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, db.getVersion(), DATABASE_VERSION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2 && newVersion == 2) {
            db.beginTransaction();
            try {
                //create the stored books table
                db.execSQL(CREATE_STORED_INFO);
                //add coulmn in categories for ordering
                db.execSQL("ALTER TABLE " + BooksInformationDBContract.CategotyEntry.TABLE_NAME + " ADD COLUMN " + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER + " INTEGER");

                //create indexes on each coulmn of join tables
                db.execSQL(CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "_i1" + ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "(" + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID + ")");
                db.execSQL(CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "_i2" + ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "(" + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + ")");
                db.execSQL(CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksCategories.TABLE_NAME + "_i1" + ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + "(" + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID + ")");
                db.execSQL(CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksCategories.TABLE_NAME + "_i2" + ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + "(" + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID + ")");

                //adding the default category order
                for (int i = 0; i < DEFULT_CATEGORIES.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER, i + 1);
                    db.update(BooksInformationDBContract.CategotyEntry.TABLE_NAME,
                            contentValues,
                            BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE + "=?",
                            new String[]{DEFULT_CATEGORIES[i]});
                }
                db.setVersion(newVersion);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    public Cursor searchBookNamesFromAuthor(String query, int authorId) {
        return queryBookListing(
                BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE + " like ? and " +
                        BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + " = ? ",

                new String[]{"%" + query + "%", String.valueOf(authorId)}
        );
    }

    public boolean indexFts() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor allBookTitleCursor = null;
        Cursor allAuthorNameCursor = null;
        db.beginTransaction();
        try {
            allBookTitleCursor = db.query(BooksInformationDBContract.BookInformationEntery.TABLE_NAME,
                    new String[]{BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID,
                            BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            db.execSQL(CREATE_BOOK_TITLES_FTS_TABLE);
            SQLiteStatement populateFTS_Statement = db.compileStatement(POPULATE_BOOKS_TITLES_FTS_SQL); //pre-compiled sql statement
            while (allBookTitleCursor.moveToNext()) {
                String cleanedText = ArabicUtilities.cleanTextForSearchingWithRegex(allBookTitleCursor.getString(1));
                populateFTS_Statement.clearBindings();
                populateFTS_Statement.bindLong(1, allBookTitleCursor.getLong(0));
                populateFTS_Statement.bindString(2, cleanedText);
                populateFTS_Statement.executeInsert();
            }
            db.rawQuery(OPTIMIZE_BOOK_TITLES_FTS, null);


            allAuthorNameCursor = db.query(BooksInformationDBContract.AuthorEntry.TABLE_NAME,
                    new String[]{BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME},
                    null,
                    null,
                    null,
                    null,
                    null
            );
            db.execSQL(CREATE_AUTHORS_NAMES_FTS_TABLE);
            SQLiteStatement populateAuthorsNamesFTS_Statement = db.compileStatement(POPULATE_AUTHORS_NAMES_FTS_SQL); //pre-compiled sql statement
            while (allAuthorNameCursor.moveToNext()) {
                String cleanedText = ArabicUtilities.cleanTextForSearchingWithRegex(allAuthorNameCursor.getString(1));
                populateAuthorsNamesFTS_Statement.clearBindings();
                populateAuthorsNamesFTS_Statement.bindLong(1, allAuthorNameCursor.getLong(0));
                populateAuthorsNamesFTS_Statement.bindString(2, cleanedText);
                populateAuthorsNamesFTS_Statement.executeInsert();
            }
            db.rawQuery(OPTIMIZE_AUTHORS_NAME_FTS, null);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            Log.e(TAG, "indexFts: ", e);
            return false;
        } finally {
            db.endTransaction();
            if (allBookTitleCursor != null) {
                allBookTitleCursor.close();
            }
            if (allAuthorNameCursor != null) {
                allAuthorNameCursor.close();
            }

        }
    }


    /**
     * @param c           cursor moved byond its last position
     * @param columnIndex the index for enqueId in the Cursor
     */
    public void cancelMultipleDownloads(Cursor c, int columnIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            while (c.moveToPrevious()) {
                long enquId = c.getLong(columnIndex);
                db.delete(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                        BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "=?",
                        new String[]{Long.toString(enquId)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteBook(int bookId, Context context) {
        //TODO Stop the indexing service if it was running
        File book = new File(StorageUtils.getIslamicLibraryShamelaBooksDir(context) + bookId + "." + DATABASE_EXTENSION);
        book.delete();

        //journal file for book database
        File journal = new File(StorageUtils.getIslamicLibraryShamelaBooksDir(context) + bookId + "." + DATABASE__JOURNAL_EXTENSION);
        journal.delete();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{String.valueOf(bookId)});

        Intent bookDeleteBroadCast =
                new Intent(BROADCAST_ACTION)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_NOT_DOWNLOAD)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
        context.sendOrderedBroadcast(bookDeleteBroadCast, null);
    }


    public HashSet<Integer> getBookIdsDownloadedOnly() {
        return getBooksIdsFilteredOnDownloadStatus(
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + ">=?",
                new String[]{String.valueOf(DownloadsConstants.STATUS_FTS_INDEXING_ENDED)}
        );
    }

    public HashSet<Integer> getAllBookIds() {
        HashSet<Integer> selectedBookInfoItems = new HashSet<>();
        Cursor c = getReadableDatabase().query(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID},
                null,
                null,
                null,
                null,
                null);
        while (c.moveToNext()) {
            selectedBookInfoItems.add(c.getInt(0));
        }
        c.close();
        return selectedBookInfoItems;

    }
}
