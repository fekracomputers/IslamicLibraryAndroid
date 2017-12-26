package com.fekracomputers.islamiclibrary.download.model;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 4/6/2017.
 */
//Don't autmatic format
public class DownloadFileConstants {
    public static final String URL_SEPARATOR = "/";
    public static final String DATABASE_FILE_EXTENSTION = "sqlite";
    public static final String ONLINE_DATABASE_NAME = "main" + "." + DATABASE_FILE_EXTENSTION;
    public static final String COMPRESSION_EXTENTION = "zip";
    public static final String ISLAMIC_LIBRARY_BASE_DIRECTORY = "IslamicLibrary";
    private static final String DOMAIN = "http://booksapi.islam-db.com";
    private static final String BASE_URL = DOMAIN + "/data";
    public static final String UNCOMPRESSED_BASE_BOOK_URL = BASE_URL + URL_SEPARATOR + "books";
    public static final String COMPRESSED_BASE_BOOK_URL = BASE_URL + URL_SEPARATOR + "cbooks";
    // http://booksapi.islam-db.com/data/cbooks/main.zip
    public static final String COMPRESSED_BOOK_INFORMATION_URL = COMPRESSED_BASE_BOOK_URL + URL_SEPARATOR + "main" + "." + COMPRESSION_EXTENTION;
    //    public static final String COMPRESSED_BOOK_INFORMATION_URL = COMPRESSED_BASE_BOOK_URL + URL_SEPARATOR + "main111" + "." + COMPRESSION_EXTENTION;
    public static final String PREF_APP_LOCATION = "custom_app_location_pref";
    public static final String PREF_SDCARDPERMESSION_DIALOG_DISPLAYED = "PREF_SDCARD_PERMISSION_DIALOG_DISPLAYED";
    public static final String SHAMELA_BOOKS_DIR = "shamela_books";
    public static final String BOOK_INFORMATION_URL = BASE_URL + URL_SEPARATOR + ONLINE_DATABASE_NAME;

}
