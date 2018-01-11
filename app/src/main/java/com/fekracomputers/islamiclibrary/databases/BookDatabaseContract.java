package com.fekracomputers.islamiclibrary.databases;

/**
 * Created by Mohammad Yahia on 10/11/2016.
 */
public class BookDatabaseContract {

    private BookDatabaseContract() {
    }

    public static class searchResultPageTableAlias {
        public static final String TABLE_NAME = "searchResult";
        public static final String SEARCH_RESULT_PARTNUMBER = "searchResult_partnumber";
        public static final String SEARCH_RESULT_PAGENUMBER = "searchResult_pagenumber";
        public static final String SEARCH_RESULT_PAGE = "searchResult_page";
        protected static final String SEARCH_RESULT_PAGE_ID = "searchResult_pageId";
    }

    public static class searchResultParentTitleTableAlias {
        public static final String TABLE_NAME = "titlePage";
        public static final String PARENT_TITLE_ID = "parent_title_id";
        public static final String PARENT_TITLE_TITLE = "parent_title_title";
        public static final String PARENT_TITLE_PAGE_ID = "parent_title_pageid";
    }

    /**
     * CREATE TABLE `info` (
     * `name`	VARCHAR(64) NOT NULL,
     * `value`	TEXT,
     * PRIMARY KEY(`name`)
     * );
     */
    public static class InfoEntry {
        public static final String TABLE_NAME = "info";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_VALUE = "value";

        public static final String KEY_AUTHOR_DEATH_HIGRI_YEAR = "author0deathhigriyear";
        public static final String KEY_DONE = "done";
        public static final String KEY_AUTHOUR_BIRH_HIJRI_YEAR = "author0birthhigriyear";
        public static final String KEY_AUTHOR_INFORMATION = "author0information";
        public static final String KEY_AUTHOR_LONG_NAME = "author0longname";
        public static final String KEY_AUTHOUR_NAME = "author0name";
        public static final String KEY_AUTHOR_ID = "author0id";
        public static final String KEY_CATEGORY_LEVEL = "category0level";
        public static final String KEY_CATEGORY_ID = "category0level";
        public static final String KEY_CATEGORY_TITLE = "category0title";
        public static final String KEY_BOOK_INFORMATION = "bookinformation";
        public static final String KEY_BOOK_CARD = "bookcard";
        public static final String KEY_BOOK_TITLE = "booktitle";
    }


    // CREATE TABLE pages (id INTEGER NOT NULL PRIMARY KEY, partnumber INTEGER NOT NULL, pagenumber INTEGER NOT NULL, page TEXT)

    public static class PageEntry {
        public static final String TABLE_NAME = "pages";
        public static final String COLUMN_NAME_PART_NUMBER = "partnumber";
        public static final String COLUMN_NAME_PAGE = "page";
        public static final String COLUMN_NAME_PAGE_NUMBER = "pagenumber";
        public static final String COLUMN_NAME_PAGE_ID = "id";
    }

    // CREATE TABLE titles (id INTEGER NOT NULL PRIMARY KEY, parentid INTEGER NOT NULL, pageid INTEGER NOT NULL, title TEXT)
    public static class TitlesEntry {
        public static final String TABLE_NAME = "titles";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PARENT_ID = "parentid";
        public static final String COLUMN_NAME_PAGE_ID = "pageid";
        public static final String COLUMN_NAME_TITLE = "title";
    }


    // CREATE TABLE tafseer (pageid INTEGER NOT NULL, tafseerbookid INTEGER NOT NULL, tafseerpageid INTEGER NOT NULL, PRIMARY KEY(pageid, tafseerbookid, tafseerpageid))

    public static class HadithSharhEntry {
        public static final String TABLE_NAME = "tafseer";
        public static final String COLUMN_NAME_PAGE_id = "pageid";
        public static final String COLUMN_NAME_SHARH_BOOK_PAGE_ID = "tafseerpageid";
        public static final String COLUMN_NAME_SHARH_BOOK_ID = "tafseerbookid";


    }

    //insert into pageTextSearch(docid,page) select id,page from pages
    public static class pageTextSearch {
        public static final String TABLE_NAME = "pagestextsearch";
        public static final String TABLE_NAME_V3 = "pageTextSearch";
        public static final String COLUMN_NAME_DOC_id = "docid";
        public static final String COLUMN_NAME_PAGE = "page";
    }

    //insert into titlesTextSearch(docid,page) select id,page from pages

    public static class titlesTextSearch {

        public static final String COLUMN_NAME_DOC_id = "docid";
        public static final String TABLE_NAME = "titlestextsearch";
        public static final String TABLE_NAME_V3 = "titlesTextSearch";
        public static final String COLUMN_NAME_TITLE = "title";
    }

}