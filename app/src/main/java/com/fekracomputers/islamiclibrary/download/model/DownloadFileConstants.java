package com.fekracomputers.islamiclibrary.download.model;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 4/6/2017.
 */

public class DownloadFileConstants {
    public static final String URL_SEPARATOR = "/";
    public static final String DATABASE_FILE_EXTENSTION = "sqlite";
    public static final String ONLINE_DATABASE_NAME = "main" + "." + DATABASE_FILE_EXTENSTION;
    public static final String COMPRESSION_EXTENTION = "zip";
    public static final String ISLAMIC_LIBRARY_BASE_DIRECTORY = "IslamicLibrary";
    private static final String domain = "http://booksapi.islam-db.com";
    private static final String baseUrl = domain + "/data";
    public static final String uncompressedBaseBookUrl = baseUrl + URL_SEPARATOR + "books";
    public static final String bookInformationUrl = baseUrl + URL_SEPARATOR + ONLINE_DATABASE_NAME;
    public static final String compressedBaseBookUrl = baseUrl + URL_SEPARATOR + "cbooks";
    // http://books.islam-db.com/data/cbooks/main.zip
    public static final String compressedBookInformationUrl = compressedBaseBookUrl + URL_SEPARATOR + "main" + "." + COMPRESSION_EXTENTION;
    public static final String PREF_APP_LOCATION = "custom_app_location_pref";

    public static final String PREF_SDCARDPERMESSION_DIALOG_DISPLAYED ="PREF_SDCARD_PERMISSION_DIALOG_DISPLAYED";
    public static final String SHAMELA_BOOKS_DIR = "shamela_books";
}
