package com.fekracomputers.islamiclibrary.download.reciver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.service.UnZipIntentService;

import java.io.File;

import static android.app.DownloadManager.STATUS_SUCCESSFUL;
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
                if (status == STATUS_SUCCESSFUL) {
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
        else if (
            //we are downloading a book then also set status as download completed
            //by the way this is bad code practice to change something in the if condition
                storedBooksDatabase.setDownloadStatusByEnquId
                        (enqueId,
                                DownloadsConstants.STATUS_DOWNLOAD_COMPLETED)
                        //we are downloading the book information database
                        ||
                        (enqueId == informationDatabaseDownloadEnqueId)

                ) {

            Cursor c = downloadManagerCursor(context, enqueId);

            if (c.moveToFirst()) {
                //Is this condition necessary? since this receiver is already registered on action DownloadInfo complete
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == STATUS_SUCCESSFUL) {
                    String filePath = Uri.parse(c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath();
                    String fileNameWithExtension = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1);
                    String fileNameWithOutExtension = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));

                    if (BooksInformationDbHelper.compressedBookFileRegex.matcher(fileNameWithExtension).matches()) {//.zip file
                        //announce download ended
                        sendBookDownloadStatusChangeBroadCast(context, Integer.parseInt(fileNameWithOutExtension), DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                        //Launch unzipping IntentService
                        Intent unZipServiceIntent = new Intent(context, UnZipIntentService.class);
                        unZipServiceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath);
                        //announce unzip started
                        sendBookDownloadStatusChangeBroadCast(context, Integer.parseInt(fileNameWithOutExtension), DownloadsConstants.STATUS_WAITING_FOR_UNZIP);
                        context.startService(unZipServiceIntent);


                    } else if (BooksInformationDbHelper.uncompressedBookFileRegex.matcher(fileNameWithExtension).matches())//.sqlite File
                    {
                        //announce download ended
                        sendBookDownloadStatusChangeBroadCast(context, Integer.parseInt(fileNameWithOutExtension), DownloadsConstants.STATUS_DOWNLOAD_COMPLETED);
                        storedBooksDatabase.setDownloadStatusByEnquId(enqueId, DownloadsConstants.STATUS_UNZIP_ENDED);
                        sendBookDownloadStatusChangeBroadCast(context, Integer.parseInt(fileNameWithOutExtension), DownloadsConstants.STATUS_UNZIP_ENDED);
                    } else if (fileNameWithExtension.equals(DATABASE_FULL_NAME))//uncompressed book information
                    {
                        broadCastUncompressedBookInformationDatabaseDownloaded(context);
                    } else if (fileNameWithExtension.equals(DATABASE_NAME + ".zip"))//compressed book information
                    {
                        broadcastCompressedBookInformationDatabaseDownloaded(context, filePath, enqueId);
                    }
                    else if(BooksInformationDbHelper.repeatedCompressedBookFileRegex.matcher(fileNameWithExtension).matches())
                    {
                        //TODO why does this happen? wat should we do? should we just replace,
                        //maybe the application crashed whle unzipping
                        //


                        Log.e("DownloaddReceiver", "File at" + filePath + "repeated", new IllegalArgumentException());

                    }
                    else {
                        Log.e("DownloaddReceiver", "File at" + filePath + " not recognized", new IllegalArgumentException());
                    }


                }
            }
            c.close();

        }
    }

    private static Cursor downloadManagerCursor(Context context, long referenceId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(referenceId);
        return downloadManager.query(q);
    }

    private static void broadCastUncompressedBookInformationDatabaseDownloaded(Context context) {

        sendBookDownloadStatusChangeBroadCast(context,DownloadsConstants.BOOK_INFORMATION_DUMMY_ID,STATUS_BOOKINFORMATION_UNZIP_ENDED);

    }

    private static void  broadcastCompressedBookInformationDatabaseDownloaded(Context context, String filePath, long referenceId) {
        //Launch unzipping IntentService
        Intent serviceIntent = new Intent(context, UnZipIntentService.class);
        serviceIntent.putExtra(UnZipIntentService.EXTRA_FILE_PATH, filePath)
                .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_ID, referenceId);
        context.startService(serviceIntent);
        sendBookDownloadStatusChangeBroadCast(context,DownloadsConstants.BOOK_INFORMATION_DUMMY_ID,STATUS_INFORMATION_DATABASE_DOWNLOAD_ONLY_SUCCESSFUL);

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
        context.sendOrderedBroadcast(localIntent,null);
    }
}
