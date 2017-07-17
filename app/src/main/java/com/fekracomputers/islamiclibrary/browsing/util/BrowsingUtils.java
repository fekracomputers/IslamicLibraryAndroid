package com.fekracomputers.islamiclibrary.browsing.util;

import android.content.Context;
import android.content.Intent;

import com.fekracomputers.islamiclibrary.browsing.activity.BookInformationActivity;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;
import com.fekracomputers.islamiclibrary.model.BookInfo;
import com.fekracomputers.islamiclibrary.reading.ReadingActivity;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 24/4/2017.
 */

public class BrowsingUtils {

    public static void openBookForReading(BookInfo bookInfo, Context context) {
        final Intent intent = new Intent(context, ReadingActivity.class);
        intent.putExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, bookInfo.getBookId());
        intent.putExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE, bookInfo.getName());
        intent.putExtra(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME, bookInfo.getAuthorName());
        context.startActivity(intent);
    }
    public static void startDownloadingBook(BookInfo bookInfo, Context context) {
        BooksDownloader booksDownloader = new BooksDownloader(context);
        booksDownloader.downloadBook(bookInfo.getBookId(), bookInfo.getName(), true);
    }

    public static void openBookInformationActivity(Context context, int book_id, String bookTitle)
    {
        Intent intent = new Intent(context, BookInformationActivity.class);
        intent.putExtra(BooksInformationDBContract.BooksCategories.COLUMN_NAME_BOOK_ID, book_id);
        intent.putExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE, bookTitle);
        context.startActivity(intent);
    }

    public static void deleteBook(int bookId, Context context) {
        BooksInformationDbHelper booksInformationDbHelper= BooksInformationDbHelper.getInstance(context);
        if (booksInformationDbHelper != null) {
            booksInformationDbHelper.deleteBook(bookId);
        }
    }
}
