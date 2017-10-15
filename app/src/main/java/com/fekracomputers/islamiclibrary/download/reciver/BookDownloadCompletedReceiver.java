package com.fekracomputers.islamiclibrary.download.reciver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.service.UnZipIntentService;

import java.io.File;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.DATABASE_FULL_NAME;
import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.DATABASE_NAME;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_BOOKINFORMATION_UNZIP_ENDED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_INFORMATION_DATABASE_DOWNLOAD_ONLY_SUCCESSFUL;

public class BookDownloadCompletedReceiver extends BroadcastReceiver {
    public static long informationDatabaseDownloadEnqueId = -1;

    public BookDownloadCompletedReceiver() {
    }

    public static void broadCastBookDownloadFailed(int bookId, String reason, Context context) {
        Timber.e("Download Failed"+reason, new IllegalArgumentException());
        BooksInformationDbHelper storedBooksDatabase = BooksInformationDbHelper.getInstance(context);
        if (storedBooksDatabase != null) {
            storedBooksDatabase.deleteBookFromStoredBooks(bookId, context);
        }
        Intent bookDeleteBroadCast =
                new Intent(BROADCAST_ACTION)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_NOT_DOWNLOAD)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_FAILLED_REASON, reason);
        context.sendOrderedBroadcast(bookDeleteBroadCast, null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long enqueId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        updateBookDownloadStatus(context, enqueId);
    }

    public static void updateBookDownloadStatus(Context context, long enqueId) {

        BooksInformationDbHelper storedBooksDatabase = BooksInformationDbHelper.getInstance(context);
        if (storedBooksDatabase == null) {
            Cursor c = downloadManagerCursor(context, enqueId);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String filePath = Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath();
                    String fileNameWithExtension = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
                    if (fileNameWithExtension.equals(DATABASE_FULL_NAME))//uncompressed book information
                    {
                        broadCastUncompressedBookInformationDatabaseDownloaded(context);
                    } else if (fileNameWithExtension.equals(DATABASE_NAME + ".zip"))//compressed book information
                    {
                        broadcastCompressedBookInformationDatabaseDownloaded(context, filePath, enqueId);
                    }
                }
            }
        }
        //check if this download is from this application comparing the id against database
        else {
            if (storedBooksDatabase.isDownloadEnqueue(enqueId) || (enqueId == informationDatabaseDownloadEnqueId)) {
                int bookId = (int) storedBooksDatabase.getBookIdByDownloadId(enqueId);
                Cursor c = downloadManagerCursor(context, enqueId);
                if (c.moveToFirst()) {

                    //this condition necessary although this receiver is already registered
                    // on action DownloadInfo complete since it my return
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String localUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        String filePath = Uri.parse(localUri).getPath();
                        String fileNameWithExtension = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
                        String fileNameWithOutExtension = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
                        int bookIdFromFile = Integer.parseInt(fileNameWithOutExtension);
                        if (bookIdFromFile != bookId) {
                            broadCastBookDownloadFailed(bookId, "bookIdFromFile:" + bookIdFromFile + "!=" + "bookId:" + "bookId", context);
                            return;
                        }

                        if (BooksInformationDbHelper.compressedBookFileRegex.matcher(fileNameWithExtension).matches()) {//.zip file
                            storedBooksDatabase.setDownloadStatusByEnquId(enqueId,
                                    DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                            //announce download ended
                            sendBookDownloadStatusChangeBroadCast(context, bookId, DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                            //Launch unzipping IntentService
                            Intent unZipServiceIntent = new Intent(context, UnZipIntentService.class);
                            unZipServiceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath);
                            //announce unzip started
                            sendBookDownloadStatusChangeBroadCast(context, bookId, DownloadsConstants.STATUS_WAITING_FOR_UNZIP);
                            context.startService(unZipServiceIntent);


                        } else if (BooksInformationDbHelper.uncompressedBookFileRegex.matcher(fileNameWithExtension).matches())//.sqlite File
                        {
                            storedBooksDatabase.setDownloadStatusByEnquId(enqueId,
                                    DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                            //announce download ended
                            sendBookDownloadStatusChangeBroadCast(context, bookId, DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                            storedBooksDatabase.setDownloadStatusByEnquId(enqueId, DownloadsConstants.STATUS_UNZIP_ENDED);
                            sendBookDownloadStatusChangeBroadCast(context, bookId, DownloadsConstants.STATUS_UNZIP_ENDED);
                        } else if (fileNameWithExtension.equals(DATABASE_FULL_NAME))//uncompressed book information
                        {
                            storedBooksDatabase.setDownloadStatusByEnquId(enqueId,
                                    DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                            broadCastUncompressedBookInformationDatabaseDownloaded(context);
                        } else if (fileNameWithExtension.equals(DATABASE_NAME + ".zip"))//compressed book information
                        {
                            storedBooksDatabase.setDownloadStatusByEnquId(enqueId,
                                    DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                            broadcastCompressedBookInformationDatabaseDownloaded(context, filePath, enqueId);
                        } else if (BooksInformationDbHelper.repeatedCompressedBookFileRegex.matcher(fileNameWithExtension).matches()) {
                            //TODO why does this happen? wat should we do? should we just replace,
                            //maybe the application crashed whle unzipping
                            //
                            broadCastBookDownloadFailed(bookId, "File at" + filePath + "repeated", context);
                        } else {
                            broadCastBookDownloadFailed(bookId, "File at" + filePath + " not recognized", context);
                        }
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        broadCastBookDownloadFailed(bookId, "DownloadManager.STATUS_FAILED", context);
                    }
                }
                c.close();

            }
        }
    }

    private static Cursor downloadManagerCursor(Context context, long referenceId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(referenceId);
        return downloadManager.query(q);
    }

    private static void broadCastUncompressedBookInformationDatabaseDownloaded(Context context) {

        sendBookDownloadStatusChangeBroadCast(context, DownloadsConstants.BOOK_INFORMATION_DUMMY_ID, STATUS_BOOKINFORMATION_UNZIP_ENDED);

    }

    private static void broadcastCompressedBookInformationDatabaseDownloaded(Context context, String filePath, long referenceId) {
        //Launch unzipping IntentService
        Intent serviceIntent = new Intent(context, UnZipIntentService.class);
        serviceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath)
                .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_ID, referenceId);
        context.startService(serviceIntent);
        sendBookDownloadStatusChangeBroadCast(context, DownloadsConstants.BOOK_INFORMATION_DUMMY_ID, STATUS_INFORMATION_DATABASE_DOWNLOAD_ONLY_SUCCESSFUL);

    }

    /**
     * @param context
     * @param bookId               the book id to broadCast download progress with
     * @param localBroadcastStatus one of {@link DownloadsConstants#STATUS_DOWNLOAD_COMPLETED} ,
     *                             {@link DownloadsConstants#STATUS_DOWNLOAD_COMPLETED}
     */
    public static void sendBookDownloadStatusChangeBroadCast(Context context, int bookId, int localBroadcastStatus) {
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(EXTRA_DOWNLOAD_STATUS, localBroadcastStatus)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);

        // Broadcasts the Intent to receivers in this app.
        context.sendOrderedBroadcast(localIntent, null);
    }
}
