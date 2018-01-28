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

import com.fekracomputers.islamiclibrary.SplashActivity;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.reciver.FileDownloadException;
import com.fekracomputers.islamiclibrary.download.service.UnZipIntentService;
import com.fekracomputers.islamiclibrary.model.AuthorInfo;
import com.fekracomputers.islamiclibrary.model.BookCategory;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.search.services.FtsIndexingService;
import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_NOT_DOWNLOAD;


/**
 * Created by Mohammad Yahia on 02/10/2016.
 */

public class BooksInformationDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_EXTENTION = "sqlite";
    public static final String DATABASE_COMPRESSED_EXTENTION = "zip";
    public static final String DATABASE_NAME = "BooksInformationDB";
    public static final Pattern REPEATED_COMPRESSED_DATABASE_FULL_NAME = Pattern.compile("^" + DATABASE_NAME + "-(\\d+)\\." + DATABASE_COMPRESSED_EXTENTION + "$");
    public static final String DATABASE_FULL_NAME = DATABASE_NAME + "." + DATABASE_EXTENTION;
    public static final String DATABASE_EXTENSION = "sqlite";
    public static final String DATABASE__JOURNAL_EXTENSION = "sqlite-journal";
    public static final Pattern uncompressedBookFileRegex = Pattern.compile("(^\\d+)\\." + DATABASE_EXTENSION + "$");
    public static final String COMPRESSION_EXTENSION = "zip";
    public static final Pattern compressedBookFileRegex = Pattern.compile("(^\\d+)\\." + COMPRESSION_EXTENSION + "$");
    public static final Pattern repeatedCompressedBookFileRegex = Pattern.compile("^(\\d+)-(\\d+)\\." + COMPRESSION_EXTENSION + "$");
    public static final String POPULATE_BOOKS_TITLES_FTS_SQL = "INSERT OR REPLACE INTO " + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME +
            "(" +
            BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_DOC_id + "," +
            BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_TITLE +
            ")" +
            "VALUES (" + "?" + "," + " ?" + ")";
    public static final String POPULATE_AUTHORS_NAMES_FTS_SQL = "INSERT OR REPLACE INTO " + BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME +
            "(" +
            BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_DOC_id + "," +
            BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_NAME +
            ")" +
            "VALUES (" + "?" + "," + " ?" + ")";
    private static final String DATABASE_JOURNAL = DATABASE_EXTENSION + "-journal";
    private static final String COMMEMORATOR = " , ";
    private static final String DOTSEPARATOR = ".";
    public static final String[] BOOK_LISTING_PROJECTION = new String[]{BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID,
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME};
    private static final String[] AUTHOUR_LISTING_COLUMNS_ARRAY_STORED = {
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR,
            "count(" + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + ")" + SQL.AS + BooksInformationDBContract.AuthorEntry.COUNT_OF_BOOKS,
            "1" + SQL.AS + BooksInformationDBContract.AuthorEntry.HAS_DOWNLOADED_BOOKS
    };
    private static final String[] AUTHOUR_LISTING_COLUMNS_ARRAY = {
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR,
            "count(" + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + ")" + SQL.AS + BooksInformationDBContract.AuthorEntry.COUNT_OF_BOOKS,
            "sum(" + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + ")" + " is not null " + SQL.AS + BooksInformationDBContract.AuthorEntry.HAS_DOWNLOADED_BOOKS
    };
    private static final String[] BOOK_INFORMATION_COLUMNS_ARRAY = new String[]{
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID,
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE,
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME,
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID,
            BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS

    };

    private static final String AUTHOURS_JOIN_BOOKS_AUTHORS =
            BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    " join " + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    " left join " + BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
                    +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID;
    private static final String JOIN_AUTHOUR_NAME_FTS_SEARCH =
            SQL.JOIN + BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME +
                    SQL.ON +
                    BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_DOC_id
                    + SQL.Equals
                    + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID;
    private static final String AUTHOURS_JOIN_AUTHORS_FTS_JOIN_BOOKS_AUTHORS =
            BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    " join " + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    JOIN_AUTHOUR_NAME_FTS_SEARCH +
                    " left join " + BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
                    +
                    "=" + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID;

    private static final String STORED_BOOKS_LEFT_JOIN_BOOKS = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            " Left " + SQL.JOIN + BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            SQL.Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;
    private static final String STORED_BOOKS_LEFT_JOIN_BOOKS_JOIN_AUTHRS = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            " Left " + SQL.JOIN + BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            SQL.Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

            SQL.JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            SQL.Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

            SQL.JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            SQL.Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +
            SQL.JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
            SQL.Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;
    private static final String BOOKS_LEFTJOIN_STORED_BOOKS =
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
                    " Left " + SQL.JOIN +
                    BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;

    private static final String BOOKS_LEFTJOIN_STORED_BOOKS_JOIN_AUTHRS =
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
                    " Left " + SQL.JOIN +
                    BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    SQL.JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    SQL.JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    SQL.Equals +
                    BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +

                    SQL.JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;
    private static final String[] CATEGORY_COLUMNS = {
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER,
            "count(" + BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + ")" + SQL.AS + BooksInformationDBContract.CategotyEntry.COUNT_OF_BOOKS,
            "sum(" + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + ")" + " is not null " + SQL.AS + BooksInformationDBContract.CategotyEntry.HAS_DOWNLOADED_BOOKS

    };
    private static final String[] CATEGORY_COLUMNS_STORED = {
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE,
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER,
            "count(" + BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + ")" + SQL.AS + BooksInformationDBContract.CategotyEntry.COUNT_OF_BOOKS
    };
    private static final String CATEGORIES_TABLES = BooksInformationDBContract.CategotyEntry.TABLE_NAME +
            " join " + BooksInformationDBContract.BooksCategories.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID +
            "=" + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
            " left join " + BooksInformationDBContract.StoredBooks.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
            +
            "=" + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID;
    private static final String STORED_CATEGORIES_TABLES = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            SQL.JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            SQL.Equals +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
            SQL.JOIN + BooksInformationDBContract.CategotyEntry.TABLE_NAME + SQL.ON +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
            SQL.Equals +
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID;
    private static final String STORED_AUTHORS_TABLES = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            SQL.JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            SQL.Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            SQL.JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME + SQL.ON +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            SQL.Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID;


    private static final String STORED_AUTHORS_TABLES_JOIN_AUTHOR_FTS = BooksInformationDBContract.StoredBooks.TABLE_NAME +
            SQL.JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
            SQL.Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            SQL.JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME + SQL.ON +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            SQL.Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID
            + JOIN_AUTHOUR_NAME_FTS_SEARCH;
    private static final String BOOKS_JOIN_AUTHORS = BooksInformationDBContract.BooksAuthors.TABLE_NAME +
            SQL.JOIN + BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
            SQL.ON +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            SQL.Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

            SQL.JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
            SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
            SQL.Equals +
            BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID;
    private static final String BOOKS_JOIN_AUTHORS_JOIN_CAT = BOOKS_JOIN_AUTHORS + SQL.JOIN +
            BooksInformationDBContract.BooksCategories.TABLE_NAME +
            SQL.ON +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
            SQL.Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
            SQL.JOIN + BooksInformationDBContract.CategotyEntry.TABLE_NAME +
            SQL.ON +
            BooksInformationDBContract.CategotyEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID +
            SQL.Equals +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID;
    private static final String JOIN_DOWNLOADED_FOR_AUTHORS = SQL.JOIN +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + SQL.ON +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
            + SQL.Equals +
            BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID;
    private static final String JOIN_DOWNLOADED_FOR_CATEGORIES = SQL.JOIN +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + SQL.ON +
            BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID
            + SQL.Equals +
            BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID;
    private static final String FROM_BOOKS_JOIN_AUTHORS =
            SQL.FROM + BOOKS_JOIN_AUTHORS;
    private static final String FROM_BOOKS_JOIN_AUTHORS_JOIN_CAT = SQL.FROM + BOOKS_JOIN_AUTHORS_JOIN_CAT;
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
    private static final String CREATE_STORED_INFO = " CREATE TABLE IF NOT EXISTS "
            + BooksInformationDBContract.StoredBooks.TABLE_NAME_V3 + "( " +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + " integer primary key" + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + SQL.INTEGER + SQL.UNIQUE + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + SQL.INTEGER + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG_V3 + SQL.INTEGER + ")";
    private static final String CREATE_STORED_INFO_v4 = " CREATE TABLE IF NOT EXISTS "
            + BooksInformationDBContract.StoredBooks.TABLE_NAME + "( " +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + " integer primary key" + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + SQL.INTEGER + SQL.UNIQUE + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + SQL.INTEGER + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG + SQL.INTEGER + "," +
            BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP + SQL.TEXT + ")";
    ;

    private static final int DATABASE_VERSION = 4;
    private static final String CREATE_BOOK_TITLES_FTS_TABLE = "CREATE VIRTUAL TABLE IF NOT EXISTS " +
            BooksInformationDBContract.BookNameTextSearch.TABLE_NAME +
            " USING fts4(content=\"\"" + "," + BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_TITLE + ")";
    private static final String OPTIMIZE_BOOK_TITLES_FTS = " INSERT INTO " +
            BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + "(" + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + ")" +
            "VALUES('optimize')";
    private static final String CREATE_AUTHORS_NAMES_FTS_TABLE = "CREATE VIRTUAL TABLE IF NOT EXISTS " +
            BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME +
            " USING fts4(content=\"\"" + "," + " " + BooksInformationDBContract.AuthorsNamesTextSearch.COLUMN_NAME_NAME + ")";
    private static final String OPTIMIZE_AUTHORS_NAME_FTS = " INSERT INTO " +
            BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME + "(" + BooksInformationDBContract.AuthorsNamesTextSearch.TABLE_NAME + ")" +
            "VALUES('optimize')";

    private static final String JOIN_BOOKS_TITLES_FTS_SEARCH = SQL.JOIN + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + SQL.ON +
            BooksInformationDBContract.BookNameTextSearch.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookNameTextSearch.COLUMN_NAME_DOC_id
            + SQL.Equals +
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;

    private static final String BOOKS_LEFT_JOIN_STORED_BOOKS_JOIN_AUTHORS =
            BooksInformationDBContract.BookInformationEntery.TABLE_NAME +
                    " Left " + SQL.JOIN +
                    BooksInformationDBContract.StoredBooks.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.StoredBooks.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    SQL.JOIN + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +

                    SQL.JOIN + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                    SQL.Equals +
                    BooksInformationDBContract.AuthorEntry.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID +

                    SQL.JOIN + BooksInformationDBContract.BooksCategories.TABLE_NAME +
                    SQL.ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID +
                    SQL.Equals +
                    BooksInformationDBContract.BookInformationEntery.TABLE_NAME + DOTSEPARATOR + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID;


    @Nullable
    private static BooksInformationDbHelper sInstance;
    @Nullable
    private static String sDatabasePath;
    private final String TAG = "InfoDbHelper";

    private BooksInformationDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Static Factory method for this singleton class
     * <p>
     * Consider using {@link #databaseFileExists(Context)} before calling this method
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
            if (!databaseFileExists(context)) return null;
            sInstance = new BooksInformationDbHelper(context, sDatabasePath, null, DATABASE_VERSION);
        }
        return sInstance;
    }

    public synchronized static void clearInstance(@NonNull Context context) {
        sDatabasePath = null;
        sInstance = null;
        getInstance(context);
    }

    /**
     * @return true if a file named as the bookInformation database exists, doesn't check its content
     */
    public static boolean databaseFileExists(@NonNull Context context) {
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

    public static void broadCastBookDeleted(int bookId, @NonNull Context context) {
        Intent bookDeleteBroadCast =
                new Intent(BROADCAST_ACTION)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_NOT_DOWNLOAD)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
        context.sendOrderedBroadcast(bookDeleteBroadCast, null);
    }

    public static void deleteBookInformationFile() {
        if (!new File(sDatabasePath).delete()) {
            Timber.e("Deleting BookInformation failed after failing to upgrade: ",
                    new IOException("error deleting file at" + sDatabasePath));
        }
    }

    @NonNull
    public static String getPathFromBookId(int bookId, @NonNull Context context, boolean journal) {
        return StorageUtils.getIslamicLibraryShamelaBooksDir(context) + File.separator + bookId + "." + (journal ? DATABASE__JOURNAL_EXTENSION : DATABASE_EXTENSION);
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

    @Nullable
    public BookInfo getBookInfo(int bookId) {
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
                    new String[]{String.valueOf(bookId)},
                    null,
                    null,
                    null
            );
            BookCategory category = getBookCategory(bookId);
            AuthorInfo authorInfo = getAuthorInfoByBookId(bookId);
            if (bookInformationCursor.moveToFirst()) {
                BookInfo bookInfo = new BookInfo(
                        bookId,
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
            Timber.e("Catch a SQLiteException when queryBookListing: ", e);
        }
        return null;
    }

    private AuthorInfo getAuthorInfoByBookId(int book_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor authorInformationCursor;
        try {

            authorInformationCursor = db.rawQuery(
                    SQL.SELECT +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + "," +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_DEATH_HIJRI_YEAR + "," +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_INFORMATION + "," +
                            BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME +

                            SQL.FROM + BooksInformationDBContract.AuthorEntry.TABLE_NAME +
                            SQL.WHERE + BooksInformationDBContract.AuthorEntry.COLUMN_NAME_ID + " = " +
                            "(" + SQL.SELECT + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID +
                            SQL.FROM + BooksInformationDBContract.BooksAuthors.TABLE_NAME +
                            SQL.WHERE + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID + "=?" +
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
            Timber.e("Catch a SQLiteException when getAuthorInfoByBookId: ", e);
        }
        return null;

    }

    private BookCategory getBookCategory(int book_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor categoryInformationCursor;
        try {
            categoryInformationCursor = db.rawQuery(
                    SQL.SELECT +
                            BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + "," +
                            BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE +

                            SQL.FROM + BooksInformationDBContract.CategotyEntry.TABLE_NAME +
                            SQL.WHERE + BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + " = " +
                            "(" + SQL.SELECT + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID +
                            SQL.FROM + BooksInformationDBContract.BooksCategories.TABLE_NAME +
                            SQL.WHERE + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID + "=?" +
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
            Timber.e("Catch a SQLiteException when getBookCategory: ", e);
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

    @NonNull
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
            Timber.e("Catch a SQLiteException when queryBookListing: ", e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return bookCategories;
    }

    @NonNull
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

    @NonNull
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

    @NonNull
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

    /**
     * @param limit number of recently downloaded books to return or 0 for all books
     * @return cursor with books completed download and indexing sorted decending with latest first
     */
    @Nullable
    public Cursor getRecentDownloads(int limit) {
        return getBooksFiltered(BooksInformationDBContract.StoredBooks.TABLE_NAME + SQL.DOT +
                        BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP + SQL.IS_NOT_NULL
                        + SQL.AND + BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP + "!=" + "''"
                        + SQL.AND + BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + ">=" + DownloadsConstants.STATUS_FTS_INDEXING_ENDED
                ,
                null,
                BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP + SQL.DECS,
                true,
                limit == 0 ? null : String.valueOf(limit));


    }

    @Nullable
    public Cursor getBooksFiltered(@Nullable String selection, String[] selectionArgs, @Nullable String orderBy, boolean downloadedOnly, String limit) {
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
        } else if (!downloadedOnly) {
            tables = BOOKS_LEFTJOIN_STORED_BOOKS_JOIN_AUTHRS;

        } else {
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
            Timber.e("Catch a SQLiteException when queryBookListing: ", e);
        }
        return c;
    }

    /**
     * @param selection      selection a
     * @param orderBy        How to order the rows, formatted as an SQL ORDER BY clause
     *                       (excluding the ORDER BY itself). Passing null will use the default sort order,
     *                       which may be unordered.
     *                       This should be an array containing coulmn names from {@link BooksInformationDBContract.AuthorEntry}
     * @param downloadedOnly whether to include only the downloaded books
     * @return a cursor with coulmns of {@link BooksInformationDbHelper#AUTHOUR_LISTING_COLUMNS_ARRAY}
     */
    @Nullable
    public Cursor getAuthorsFiltered(@Nullable String selection, String[] selectionArgs, String[] orderBy, boolean downloadedOnly) {
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

        } else if (!downloadedOnly) {
            tables = AUTHOURS_JOIN_AUTHORS_FTS_JOIN_BOOKS_AUTHORS;
            selectionCoulnsArray = AUTHOUR_LISTING_COLUMNS_ARRAY;

        } else {
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
            Timber.e("Catch a SQLiteException when queryBookListing: ", e);
        }
        return c;
    }

    private boolean orderByContainsNumberOfBooks(@NonNull String[] orderBy) {
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
    private String authorOrderByString(@Nullable String[] orderBy) {
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
    private void checkFileInDbOrInsert(@NonNull SQLiteDatabase db, int bookId, @NonNull Context context, String filePath
    ) {

        Cursor c = db.query(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                new String[]{BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS},
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{Integer.toString(bookId)},
                null, null, null
        );
        if (c.moveToFirst()) {//the book already added
            int status = c.getInt(0);
            ContentValues contentValues = new ContentValues();
            contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG,
                    BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_PRESENT);
            db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                    contentValues, BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                    new String[]{String.valueOf(bookId)});

            if (status <= DownloadsConstants.STATUS_UNZIP_ENDED) {
                BookDatabaseHelper bookDatabaseHelper = null;
                try {
                    bookDatabaseHelper = BookDatabaseHelper.getInstance(context, bookId);
                    if (bookDatabaseHelper != null && bookDatabaseHelper.isFtsSearchable()) {
                        updateStoredBookStatus(db, bookId, DownloadsConstants.STATUS_FTS_INDEXING_ENDED);
                    } else {
                        requestIndexing(bookId, context, filePath);
                        updateStoredBookStatus(db, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
                    }
                    if (bookDatabaseHelper != null) {
                        bookDatabaseHelper.close();
                    }
                } catch (BookDatabaseException bookDatabaseException) {
                    Timber.e(bookDatabaseException);
                }
            } else if (status == DownloadsConstants.STATUS_FTS_INDEXING_STARTED) {
                //it started but was not marked as finished may be it is now being indexed or may be it was corrupted
                //This must not happen since the database transaction should roll back

                requestIndexing(bookId, context, filePath);
                updateStoredBookStatus(db, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
            }
        } else {//book wasn't added before
            try {
                BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(context, bookId);
                if (bookDatabaseHelper.isFtsSearchable()) {//the book is fully configured
                    insertStoredBook(db, bookId, DownloadsConstants.STATUS_FTS_INDEXING_ENDED);
                    Intent localIntent =
                            new Intent(BROADCAST_ACTION)
                                    // Puts the status into the Intent
                                    .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_FTS_INDEXING_ENDED)
                                    .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                    context.sendOrderedBroadcast(localIntent, null);


                } else {
                    insertStoredBook(db, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
                    Intent localIntent =
                            new Intent(BROADCAST_ACTION)
                                    // Puts the status into the Intent
                                    .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_UNZIP_ENDED)
                                    .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                    context.sendOrderedBroadcast(localIntent, null);
                    requestIndexing(bookId, context, filePath);

                }
                bookDatabaseHelper.close();
            } catch (BookDatabaseException bookDatabaseException) {
                Timber.e(bookDatabaseException);
            }
        }

        c.close();
    }

    private void requestIndexing(int bookId, @NonNull Context context, String filePath) {
        Intent ftsIndexingServiceIntent = new Intent(context, FtsIndexingService.class);
        ftsIndexingServiceIntent.putExtra(EXTRA_DOWNLOAD_BOOK_ID, bookId);
        ftsIndexingServiceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath);
        context.startService(ftsIndexingServiceIntent);
    }

    private void updateStoredBookStatus(@NonNull SQLiteDatabase db, int bookId, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG,
                BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_PRESENT);
        db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                contentValues, BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{String.valueOf(bookId)});

    }

    private void insertStoredBook(@NonNull SQLiteDatabase db, int bookId, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID, bookId);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG, BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_PRESENT);
        db.insertWithOnConflict(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                null,
                contentValues, SQLiteDatabase.CONFLICT_IGNORE);
    }


    public int getNumberOfStoredBooks(Context context) {
        File booksDir = new File(StorageUtils.getIslamicLibraryShamelaBooksDir(context));
        if (!(booksDir.exists() && booksDir.isDirectory())) {
            booksDir.mkdirs();
            return 0;
        } else {
            return booksDir.list((dir, name) -> name.endsWith(DATABASE_EXTENSION) && !name.endsWith(DATABASE_JOURNAL)).length - 1;
        }
    }

    /**
     * Scan the program directory and check each book data base file against the StoredBooks Database
     */

    public void refreshBooksDbWithDirectory(@NonNull Context context,
                                            @Nullable SplashActivity.RefreshBooksProgressCallBack refreshBooksProgressCallBack) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG,
                VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT);
        db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                contentValues, null, null);


        File booksDir = new File(StorageUtils.getIslamicLibraryShamelaBooksDir(context));
        if (!(booksDir.exists() && booksDir.isDirectory())) {
            booksDir.mkdirs();
        } else {
            String[] files = booksDir.list((dir, name) ->
                    name.endsWith(DATABASE_EXTENSION)
                            &&
                            !name.endsWith(DATABASE_JOURNAL)
                            && !name.equals(DATABASE_FULL_NAME));
            if (files.length == 0) {
                return;
            }
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                String fullFilePath = booksDir + File.separator + file;

                //validate file name against <integer>.sqlite
                Matcher matcher = uncompressedBookFileRegex.matcher(file);
                if (matcher.matches()) {
                    int book_id = Integer.parseInt(matcher.group(1));
                    checkFileInDbOrInsert(db, book_id, context, fullFilePath);
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
                        serviceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, fullFilePath);
                        context.startService(serviceIntent);
                        // Broadcasts the Intent to receivers in this app.

                    }
                }
                if (refreshBooksProgressCallBack != null) {
                    refreshBooksProgressCallBack.accept(i);
                }
            }


            //delete book entries that doesn't have files in file system
            db.delete(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG + "=?",
                    new String[]{String.valueOf(BooksInformationDBContract.StoredBooks.VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT)}
            );

        }


    }

    public void refreshBooksDbWithDirectory(@NonNull Context context) {
        refreshBooksDbWithDirectory(context, null);
    }

    @NonNull
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
                SQL.SELECT +
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
        //TODO add
        int i = db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME, contentValues,
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "=?",
                new String[]{Long.toString(enqueueId)});
        return i == 1;
    }

    public boolean isDownloadEnqueue(long enqueueId) {
        return 1L == DatabaseUtils.longForQuery(getReadableDatabase(),
                " SELECT COUNT(*)" +
                        " FROM " +
                        BooksInformationDBContract.StoredBooks.TABLE_NAME +
                        " WHERE " +
                        BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "=?",
                new String[]{String.valueOf(enqueueId)}
        );


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
    public void setStatus(int bookId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValuesAddIfNotExist = new ContentValues();
        contentValuesAddIfNotExist.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        contentValuesAddIfNotExist.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID, bookId);
        db.insertWithOnConflict(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                null,
                contentValuesAddIfNotExist,
                SQLiteDatabase.CONFLICT_IGNORE);


        if (status == DownloadsConstants.STATUS_FTS_INDEXING_ENDED) {
            db.execSQL("UPDATE " + BooksInformationDBContract.StoredBooks.TABLE_NAME + " SET " +
                            BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP
                            + "=" + "datetime('now','localtime')" +
                            SQL.WHERE + BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                    new String[]{Long.toString(bookId)}
            );
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS, status);
        db.update(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                contentValues,
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{Long.toString(bookId)});


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

    public void onCreate(@NonNull SQLiteDatabase db) {
        onUpgrade(db, db.getVersion(), DATABASE_VERSION);
    }

    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.d("old datbase version %d and new %d", oldVersion, newVersion);

        try {
            switch (oldVersion) {
                case 0:
                case 1:
                    upgradeToVersion2(db);
                case 2:
                    upgradeToVersion3(db);
                case 3:
                    upgradeToVersion4(db);
            }
        } catch (Exception e) {
            //upgrading the database failed
            Timber.e(e, "upgrading the BookInformation database failed");
            //delete the file from file system
            deleteBookInformationFile();
            throw new FileDownloadException(
                    "upgrading database failed",
                    e,
                    DownloadsConstants.BOOK_INFORMATION_DUMMY_ID,
                    sDatabasePath, 0);

        }
    }

    public boolean isValid() {
        DBValidator dBValidator = new DBValidator(DBValidator.DataBaseType.BOOK_INFORATION_DATABASE_TYPE);
        dBValidator.validate(this);
        return dBValidator.isValid();
    }

    private void upgradeToVersion2(@NonNull SQLiteDatabase db) throws Exception {
        db.beginTransaction();
        try {
            //create the stored books table
            db.execSQL(CREATE_STORED_INFO);
            //add coulmn in categories for ordering
            db.execSQL("ALTER TABLE " + BooksInformationDBContract.CategotyEntry.TABLE_NAME + " ADD COLUMN " +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER_3 + " INTEGER");
            //create indexes on each coulmn of join tables
            db.execSQL(SQL.CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "_i1" + SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "(" + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID + ")");
            db.execSQL(SQL.CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "_i2" + SQL.ON + BooksInformationDBContract.BooksAuthors.TABLE_NAME + "(" + BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID + ")");
            db.execSQL(SQL.CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksCategories.TABLE_NAME + "_i1" + SQL.ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + "(" + BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID + ")");
            db.execSQL(SQL.CREATE_INDEX_IF_NOT_EXISTS + BooksInformationDBContract.BooksCategories.TABLE_NAME + "_i2" + SQL.ON + BooksInformationDBContract.BooksCategories.TABLE_NAME + "(" + BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID + ")");

            //adding the default category order
            for (int i = 0; i < DEFULT_CATEGORIES.length; i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER_3, i + 1);
                db.update(BooksInformationDBContract.CategotyEntry.TABLE_NAME,
                        contentValues,
                        BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE + "=?",
                        new String[]{DEFULT_CATEGORIES[i]});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void upgradeToVersion3(@NonNull SQLiteDatabase db) throws Exception {
        //add downloaded completed timestamp
        db.execSQL(SQL.ALTER_TABLE + BooksInformationDBContract.StoredBooks.TABLE_NAME_V3 + SQL.ADD_Coulmn +
                BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP_V3 + SQL.TEXT
        );
    }

    private void upgradeToVersion4(@NonNull SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE_STORED_INFO_v4);
            db.execSQL("INSERT INTO " + BooksInformationDBContract.StoredBooks.TABLE_NAME + "( " +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP +
                    ")" +

                    " SELECT " +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_ENQID + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS_V3 + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_FILESYSTEM_SYNC_FLAG_V3 + "," +
                    BooksInformationDBContract.StoredBooks.COLUMN_COMPLETED_TIMESTAMP_V3 +
                    " FROM " + BooksInformationDBContract.StoredBooks.TABLE_NAME_V3
            );
            db.execSQL("Drop Table " + BooksInformationDBContract.StoredBooks.TABLE_NAME_V3);

            db.execSQL("ALTER TABLE " + BooksInformationDBContract.CategotyEntry.TABLE_NAME + " RENAME TO " + " tmp_table_name ");
            db.execSQL("CREATE TABLE " + BooksInformationDBContract.CategotyEntry.TABLE_NAME + "(" +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_PARENT_ID + " INTEGER NOT NULL, " +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE + " TEXT," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER + " INTEGER " +
                    ")");
            db.execSQL("INSERT INTO " + BooksInformationDBContract.CategotyEntry.TABLE_NAME + "( " +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + "," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_PARENT_ID + "," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE + "," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER +
                    ")" +

                    " SELECT " +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_ID + "," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_PARENT_ID + "," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_TITLE + "," +
                    BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_ORDER_3 +
                    " FROM " + "tmp_table_name"
            );
            db.execSQL("Drop Table " + " tmp_table_name ");

            db.execSQL("alter table " + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME_V3 +
                    " rename to " + BooksInformationDBContract.BookNameTextSearch.TABLE_NAME);


            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
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
                String cleanedText = ArabicUtilities.cleanTextForSearchingIndexing(allBookTitleCursor.getString(1));
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
                String cleanedText = ArabicUtilities.cleanTextForSearchingIndexing(allAuthorNameCursor.getString(1));
                populateAuthorsNamesFTS_Statement.clearBindings();
                populateAuthorsNamesFTS_Statement.bindLong(1, allAuthorNameCursor.getLong(0));
                populateAuthorsNamesFTS_Statement.bindString(2, cleanedText);
                populateAuthorsNamesFTS_Statement.executeInsert();
            }
            db.rawQuery(OPTIMIZE_AUTHORS_NAME_FTS, null);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            Timber.e("indexFts: ", e);
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
    public void cancelMultipleDownloads(@NonNull Cursor c, int columnIndex) {
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

    public void deleteBook(int bookId, @NonNull Context context) {
        BookDatabaseHelper.closeStatic(bookId, context);
        //TODO Stop the indexing service if it was running
        File book = new File(getPathFromBookId(bookId, context, false));
        if (book.exists())
            if (!book.delete()) {
                Timber.e("error deleting file");
            }

        //journal file for book database
        File journal = new File(getPathFromBookId(bookId, context, true));
        if (book.exists())
            if (!journal.delete()) {
                Timber.e("error deleting journal file");
            }
        deleteBookFromStoredBooks(bookId, context);
        UserDataDBHelper.getInstance(context).deleteAccessLog(bookId);
    }

    public void deleteBookFromStoredBooks(int bookId, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BooksInformationDBContract.StoredBooks.TABLE_NAME,
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_BookID + "=?",
                new String[]{String.valueOf(bookId)});

    }

    @NonNull
    public HashSet<Integer> getBookIdsDownloadedOnly() {
        return getBooksIdsFilteredOnDownloadStatus(
                BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + ">=?",
                new String[]{String.valueOf(DownloadsConstants.STATUS_FTS_INDEXING_ENDED)}
        );
    }

    @NonNull
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


    public Cursor getBooksFilteredwithAttachDatabase(@NonNull String databaseName,
                                                     @NonNull String databasePath,
                                                     @NonNull String joinTableName,
                                                     @NonNull String coulmnBookIdName,
                                                     @Nullable String[] selctionArgs,
                                                     @Nullable String selection,
                                                     @Nullable String orderBy) {

        attachDatabaseIfNedded(databasePath, databaseName);

        return getReadableDatabase().query(BOOKS_LEFTJOIN_STORED_BOOKS_JOIN_AUTHRS +
                        SQL.JOIN
                        + databaseName + SQL.DOT + joinTableName +
                        SQL.ON +
                        BooksInformationDBContract.BookInformationEntery.TABLE_NAME + SQL.DOT + BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_ID +
                        SQL.EQUALS +
                        databaseName + SQL.DOT + joinTableName + SQL.DOT + coulmnBookIdName,
                BOOK_INFORMATION_COLUMNS_ARRAY,
                selection,
                selctionArgs,
                null,
                null,
                orderBy,
                null
        );

    }

    private void attachDatabaseIfNedded(String databasePath, String databaseName) {
        boolean found = false;
        Cursor c = getReadableDatabase().rawQuery("PRAGMA database_list", null);
        while (c.moveToNext()) {
            if (c.getString(1).equals(databaseName) && c.getString(2).equals(databasePath)) {
                found = true;
                break;
            }
        }
        c.close();
        if (!found)
            getReadableDatabase().execSQL("ATTACH DATABASE '" + databasePath + "' AS " + databaseName);

    }


    boolean isBookDownloaded(int bookId) {
        return getBookDownloadStatus(bookId) >= DownloadsConstants.STATUS_FTS_INDEXING_ENDED;
    }


}
