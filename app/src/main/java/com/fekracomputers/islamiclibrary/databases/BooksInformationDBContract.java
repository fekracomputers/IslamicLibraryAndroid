package com.fekracomputers.islamiclibrary.databases;

/**
 * IMPORTANT NOTE DONT USE THE CONSTANT ID FROM SINGLE TABLES USE THE SPECIFIC COULMN FROM THE JOIN TABLES INSTEAD
 */

public final class BooksInformationDBContract {

    private BooksInformationDBContract() {
    }


    /**
     * The table name and coulmn names for
     * CREATE TABLE `authors` (
     * `id`	INTEGER NOT NULL,
     * `name`	TEXT,
     * `information`	TEXT,
     * `birthhigriyear`	INTEGER NOT NULL,
     * `deathhigriyear`	INTEGER NOT NULL,
     * PRIMARY KEY(`id`)
     * );
     */
    public static class AuthorEntry {
        public static final String TABLE_NAME = "authors";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_INFORMATION = "information";
        public static final String COLUMN_NAME_DEATH_HIJRI_YEAR = "deathhigriyear";
        public static final String COLUMN_NAME_Birth_HIJRI_YEAR = "birthhigriyear";
        public static final String ORDER_BY_NUMBER_OF_BOOKS = "ORDER_BY_NUMBER_OF_BOOKS";
        public static final String COUNT_OF_BOOKS = "count_of_author_books";
        public static final String HAS_DOWNLOADED_BOOKS = "has_downloaded_books";
    }


    /**
     * CREATE TABLE `books` (
     * `id`	INTEGER NOT NULL,
     * `mTitle`	TEXT,
     * `information`	TEXT,
     * `card`	TEXT,
     * `adddate`	DATETIME,
     * `accesscount`	INTEGER,
     * PRIMARY KEY(`id`)
     * );
     */
    public static class BookInformationEntery {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CARD = "card";
        public static final String COLUMN_NAME_INFORMATION = "information";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ADD_DATE = "adddate";
        public static final String COLUMN_NAME_ACCESS_COUNT = "accesscount";
    }

    /**
     * CREATE TABLE `categories` (
     * `id`	INTEGER NOT NULL,
     * `parentid`	INTEGER NOT NULL,
     * `mTitle`	TEXT,
     * PRIMARY KEY(`id`)
     * );
     */
    public static class CategotyEntry {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CATEGORY_TITLE = "category_title";
        public static final String COLUMN_NAME_CATEGORY_ORDER = "catorder";
        public static final String COLUMN_NAME_CATEGORY_ORDER_3 = "catOrder";
        public static final String COLUMN_NAME_PARENT_ID = "parentid";
        public static final String COUNT_OF_BOOKS = "count_of_author_books";
        public static final String HAS_DOWNLOADED_BOOKS = "has_downloaded_books";
    }

    /**
     * CREATE TABLE `booksauthors` (
     * `bookid`	INTEGER NOT NULL,
     * `authorid`	INTEGER NOT NULL,
     * PRIMARY KEY(`bookid`,`authorid`)
     * );
     */
    public static class BooksAuthors {
        public static final String TABLE_NAME = "booksauthors";
        public static final String COLUMN_NAME_BOOK_ID = "bookid";
        public static final String COLUMN_NAME_AUTHOR_ID = "authorid";


    }


    /**
     * CREATE TABLE `bookscategories` (
     * `bookid`	INTEGER NOT NULL,
     * `categoryid`	INTEGER NOT NULL,
     * PRIMARY KEY(`bookid`,`categoryid`)
     * );
     * );
     */
    public static class BooksCategories {
        public static final String TABLE_NAME = "bookscategories";
        public static final String COLUMN_NAME_BOOK_ID = "bookid";
        public static final String COLUMN_NAME_CATEGORY_ID = "categoryid";


    }

    public static class StoredBooks {
        public static final String TABLE_NAME = "mybooks";
        public static final String COLUMN_NAME_BookID = "bookid";
        public static final String COLUMN_NAME_ENQID = "enqueue";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_STATUS_V3 = "STATUS";
        public static final int VALUE_FILESYSTEM_SYNC_FLAG_PRESENT = 1;
        public static final int VALUE_FILESYSTEM_SYNC_FLAG_NOT_PRESENT = 0;
        public static final String COLUMN_NAME_FILESYSTEM_SYNC_FLAG_V3 = "filesystem_sync_flag";
        public static final String COLUMN_NAME_FILESYSTEM_SYNC_FLAG = "syncFlag";
        public static final String COLUMN_COMPLETED_TIMESTAMP_V3 = "DownloadcompletedTimestamp";
        public static final String COLUMN_COMPLETED_TIMESTAMP = "Downloadtimestamp";
        public static final String TABLE_NAME_V3 = "stored_books_table";
    }

    public static class BookNameTextSearch {
        public static final String TABLE_NAME_V3 = "BookNameTextSearch";
        public static final String TABLE_NAME = "bookstitlestextsearch";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DOC_id = "docid";
    }

    public static class AuthorsNamesTextSearch {
        public static final String TABLE_NAME = "AuthorsNamesTextSearch";
        public static final String COLUMN_NAME_NAME = "name";
        public static String COLUMN_NAME_DOC_id="docid";
    }


}
