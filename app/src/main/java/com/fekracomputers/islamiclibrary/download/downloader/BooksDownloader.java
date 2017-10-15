package com.fekracomputers.islamiclibrary.download.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.reciver.BookDownloadCompletedReceiver;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;

import java.io.File;
import java.util.Collection;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE;
import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * Created by Mohammad Yahia on 13/10/2016.
 */

public class BooksDownloader {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private String booksPath;


    public BooksDownloader(Context context) {
        this.mContext = context;
        booksPath = StorageUtils.getIslamicLibraryShamelaBooksDir(mContext);
    }


    public void downloadBookCollection(final Collection<Integer> book_ids) {

        AsyncTask.execute(() -> {
            BooksInformationDbHelper booksInformationDbHelper = BooksInformationDbHelper.getInstance(mContext);
            for (Integer book_id : book_ids) {
                //   if (booksInformationDbHelper.getBookDownloadStatus(book_id) < DownloadsConstants.STATUS_DOWNLOAD_REQUESTED)
                downloadBook(book_id, booksInformationDbHelper.getBookName(book_id), true, VISIBILITY_VISIBLE);
                //
            }
        });


    }

    public long downloadBook(int book_id, String bookTitle, boolean compressed) {
        return downloadBook(book_id, bookTitle, compressed, VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    }

    /**
     * @param book_id
     * @param bookTitle
     * @param compressed whether to download compressed or un compressed
     * @return downloadReference id
     */
    private long downloadBook(int book_id, String bookTitle, boolean compressed, int visbility) {
        String fileName;
        Uri uri;
        if (compressed) {
            fileName = book_id + "." + BooksInformationDbHelper.COMPRESSION_EXTENSION;
            uri = Uri.parse(DownloadFileConstants.COMPRESSED_BASE_BOOK_URL + DownloadFileConstants.URL_SEPARATOR + fileName);
        } else {
            fileName = book_id + "." + BooksInformationDbHelper.DATABASE_EXTENSION;
            uri = Uri.parse(DownloadFileConstants.UNCOMPRESSED_BASE_BOOK_URL + DownloadFileConstants.URL_SEPARATOR + fileName);
        }


        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting Title of request
        request.setTitle(bookTitle);
        //Setting description of request
        //request.setDescription("Android Data download using DownloadManager.");
        request.setDestinationUri(Uri.fromFile(new File(booksPath, fileName)));
        request.setNotificationVisibility(visbility);

        //Enqueue download and save into referenceId
        long downloadReference = downloadManager.enqueue(request);
        Log.v(TAG, "started downloading from " + uri.toString() + " with id =" + Long.toString(downloadReference) + "into" + booksPath + DownloadFileConstants.URL_SEPARATOR + fileName);
        //add the downloadReference to the data base to recognize it from other applications' downloads
        BooksInformationDbHelper.getInstance(mContext).addDownload(book_id, downloadReference, DownloadsConstants.STATUS_DOWNLOAD_REQUESTED);
        return downloadReference;
    }

    public long DownloadBookInformationDatabase(boolean compressed) {
        Uri uri;
        String extension;
        if (compressed) {
            uri = Uri.parse(DownloadFileConstants.COMPRESSED_BOOK_INFORMATION_URL);
            extension = BooksInformationDbHelper.DATABASE_COMPRESSED_EXTENTION;

        } else {
            uri = Uri.parse(DownloadFileConstants.BOOK_INFORMATION_URL);
            extension = BooksInformationDbHelper.DATABASE_EXTENTION;
        }


        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting mTitle of request
        request.setTitle(mContext.getString(R.string.book_information_database));

        //Setting description of request
        //request.setDescription("Android Data download using DownloadManager.");
        request.setDestinationUri(Uri.fromFile(new File(booksPath, BooksInformationDbHelper.DATABASE_NAME + "." + extension)));
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Enqueue download and save into referenceId
        long downloadReference = downloadManager.enqueue(request);

        /* notify the {@link BookDownloadCompletedReceiver} to wait for the download to decompress */
        if (compressed)
            BookDownloadCompletedReceiver.informationDatabaseDownloadEnqueId = downloadReference;

        Log.v(TAG, "started downloading from " + uri.getPath() + "with id =" + Long.toString(downloadReference) + "into" + booksPath + DownloadFileConstants.URL_SEPARATOR + BooksInformationDbHelper.DATABASE_NAME + "." + extension);
        return downloadReference;
    }


}
