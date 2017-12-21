package com.fekracomputers.islamiclibrary.search.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.SQLException;

import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.reciver.BookDownloadCompletedReceiver;
import com.fekracomputers.islamiclibrary.download.service.UnZipIntentService;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_FTS_INDEXING_ENDED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_FTS_INDEXING_STARTED;

/**
 * A service which index sqlite database for books using fts4
 */
public class FtsIndexingService extends IntentService {


    public FtsIndexingService() {
        super("FtsIndexingService");
        setIntentRedelivery(true);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        int bookId = intent.getIntExtra(EXTRA_DOWNLOAD_BOOK_ID, 0);
        if (bookId != DownloadsConstants.BOOK_INFORMATION_DUMMY_ID) {
            Intent ftsIndexingStartedBroadCast =
                    new Intent(BROADCAST_ACTION)
                            .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_FTS_INDEXING_STARTED)
                            .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
            sendOrderedBroadcast(ftsIndexingStartedBroadCast, null);
            BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(this, bookId);
            try {
                if (bookDatabaseHelper.isValidBook()) {
                    if (bookDatabaseHelper.indexFts()) {
                        Intent ftsIndexingEndedBroadCast =
                                new Intent(BROADCAST_ACTION)
                                        .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_FTS_INDEXING_ENDED)
                                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                        sendOrderedBroadcast(ftsIndexingEndedBroadCast, null);
                    } else {//the indexing failed

                        Intent ftsIndexingEndedBroadCast =
                                new Intent(BROADCAST_ACTION)
                                        .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_UNZIP_ENDED)
                                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                        sendOrderedBroadcast(ftsIndexingEndedBroadCast, null);
                    }
                } else {
                    if (intent.hasExtra(UnZipIntentService.EXTRA_FILE_PATH)) {
                        String filePath = intent.getStringExtra(UnZipIntentService.EXTRA_FILE_PATH);
                        //delete the file from file system
                        if (!new File(filePath).delete()) {
                            Timber.e("Deleting file: ", new IOException("error deleting file at" + filePath));
                        }
                        BookDownloadCompletedReceiver.broadCastBookDownloadFailed(bookId, "invalidDatabase", this);
                    }
                }
            } catch (SQLException e) {
                Timber.e(e);
            }

        } else { //Index book Information Database
            String filePath = intent.getStringExtra(UnZipIntentService.EXTRA_FILE_PATH);
            Intent ftsIndexingStartedBroadCast =
                    new Intent(BROADCAST_ACTION)
                            .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_BOOKINFORMATION_FTS_INDEXING_STARTED)
                            .putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath);
            sendOrderedBroadcast(ftsIndexingStartedBroadCast, null);
            BooksInformationDbHelper booksInformationDbHelper = BooksInformationDbHelper.getInstance(this);
            if (booksInformationDbHelper != null) {
                if (booksInformationDbHelper.indexFts()) {
                    Intent ftsIndexingEndedBroadCast =
                            new Intent(BROADCAST_ACTION)
                                    .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_BOOKINFORMATION_FTS_INDEXING_ENDED)
                                    .putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath);
                    sendOrderedBroadcast(ftsIndexingEndedBroadCast, null);
                } else { //the indexing failed
                    Timber.e("indexing book information failed");
                    if (intent.hasExtra(UnZipIntentService.EXTRA_FILE_PATH)) {
                        //delete the file from file system
                        if (!new File(filePath).delete()) {
                            Timber.e("Deleting BookInformation: ", new IOException("error deleting file at" + filePath));
                        }
                    }
                    Intent ftsIndexingEndedBroadCast =
                            new Intent(BROADCAST_ACTION)
                                    .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_BOOKINFORMATION_FAILED)
                                    .putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath);
                    sendOrderedBroadcast(ftsIndexingEndedBroadCast, null);
                }
            }
        }


        /*TODO Closing could cause serious concurrency issues ,or will not ?!
         * http://stackoverflow.com/questions/2493331/what-are-the-best-practices-for-sqlite-on-android
         */
        //bookDatabaseHelper.close();


    }
}
