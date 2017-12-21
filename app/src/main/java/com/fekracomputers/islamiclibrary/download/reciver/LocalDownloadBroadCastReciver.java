package com.fekracomputers.islamiclibrary.download.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.search.services.FtsIndexingService;

import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BOOK_INFORMATION_DUMMY_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_NOTIFY_WITHOUT_BOOK_ID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_BOOKINFORMATION_FAILED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_DOWNLOAD_COMPLETED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_DOWNLOAD_REQUESTED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_FTS_INDEXING_ENDED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_FTS_INDEXING_STARTED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_INVALID;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_NOT_DOWNLOAD;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_UNZIP_STARTED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_WAITING_FOR_UNZIP;
import static com.fekracomputers.islamiclibrary.download.service.UnZipIntentService.EXTRA_FILE_PATH;

public class LocalDownloadBroadCastReciver extends BroadcastReceiver {
    public LocalDownloadBroadCastReciver() {
    }

    public static void broadCastDownloadCanceled(Context context, long bookId) {
        Intent bookDeleteBroadCast =
                new Intent(BROADCAST_ACTION)
                        .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_NOT_DOWNLOAD)
                        .putExtra(EXTRA_DOWNLOAD_BOOK_ID, (int) BooksInformationDbHelper
                                .getInstance(context)
                                .getBookIdByDownloadId(
                                        bookId
                                )
                        )
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_FAILLED_REASON, "canceled by user");
        context.sendOrderedBroadcast(bookDeleteBroadCast, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean notifyCangeWithotBokId = intent.getBooleanExtra(EXTRA_NOTIFY_WITHOUT_BOOK_ID, false);
        int status = intent.getIntExtra(EXTRA_DOWNLOAD_STATUS, STATUS_INVALID);
        String filePath = intent.getStringExtra(EXTRA_FILE_PATH);


        if (!notifyCangeWithotBokId) {
            int bookId = intent.getIntExtra(EXTRA_DOWNLOAD_BOOK_ID, BOOK_INFORMATION_DUMMY_ID);
            Log.d("LocalBroadCastReceiver", "onReceive: status" + status + " bookId: " + bookId);

            switch (status) {
                case DownloadsConstants.STATUS_BOOKINFORMATION_UNZIP_ENDED:
                    Intent bookInformationFtsIndexingServiceIntent = new Intent(context, FtsIndexingService.class);
                    bookInformationFtsIndexingServiceIntent.putExtra(EXTRA_DOWNLOAD_BOOK_ID, bookId);
                    if (filePath != null) {
                        bookInformationFtsIndexingServiceIntent.putExtra(EXTRA_FILE_PATH, filePath);
                    }
                    context.startService(bookInformationFtsIndexingServiceIntent);
                    break;
                case DownloadsConstants.STATUS_UNZIP_ENDED:
                    Intent ftsIndexingServiceIntent = new Intent(context, FtsIndexingService.class);
                    ftsIndexingServiceIntent.putExtra(EXTRA_DOWNLOAD_BOOK_ID, bookId);
                    if (filePath != null) {
                        ftsIndexingServiceIntent.putExtra(EXTRA_FILE_PATH, filePath);
                    }
                    context.startService(ftsIndexingServiceIntent);
                case STATUS_DOWNLOAD_REQUESTED:
                case STATUS_DOWNLOAD_COMPLETED:
                case STATUS_WAITING_FOR_UNZIP:
                case STATUS_UNZIP_STARTED:
                case STATUS_FTS_INDEXING_STARTED:
                case STATUS_FTS_INDEXING_ENDED:
                case STATUS_NOT_DOWNLOAD:
                case STATUS_BOOKINFORMATION_FAILED:
                    BooksInformationDbHelper booksInformationDbHelper = BooksInformationDbHelper.getInstance(context);
                    if (booksInformationDbHelper != null) {
                        booksInformationDbHelper.setStatus(bookId, status);
                    }
                    break;
            }
        } else {
            Log.d("LocalBroadCastReceiver", "onReceive: status" + status + " bookId: " + "WITHOUT_BOOK_ID");
        }
    }
}
