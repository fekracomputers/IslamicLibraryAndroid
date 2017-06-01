package com.fekracomputers.islamiclibrary.download.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.reciver.BookDownloadCompletedReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.DATABASE_NAME;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_DOWNLOAD_STATUS;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_BOOKINFORMATION_UNZIP_ENDED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_UNZIP_ENDED;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.STATUS_UNZIP_STARTED;

/**
 * Created by Mohammad Yahia on 03/11/2016.
 */
public class UnZipIntentService extends IntentService {
    public static final String EXTRA_FILE_PATH = "ZIP_FILE_PATH";
    private static final String TAG = "UnZipIntentService";

    /**
     * An IntentService must always have a constructor that calls the super constructor. The
     * string supplied to the super constructor is used to give a name to the IntentService's
     * background thread.
     */
    public UnZipIntentService() {

        super("UnZipIntentService");
        setIntentRedelivery(true);


    }


    @Override
    protected void onHandleIntent(Intent workIntent) {

        String zipFilePath = workIntent.getStringExtra(EXTRA_FILE_PATH);

        if (zipFilePath != null) {
            String fileName = zipFilePath.substring(zipFilePath.lastIndexOf(File.separatorChar) + 1);
            if (BooksInformationDbHelper.compressedBookFileRegex.matcher(fileName).matches()) {

                //Broadcast unzip Started
                int bookId = Integer.parseInt(fileName.substring(0, fileName.lastIndexOf('.')));
                Intent unzipStartedBroadCast =
                        new Intent(BROADCAST_ACTION)
                                .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_UNZIP_STARTED)
                                .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                // Broadcasts the Intent to receivers in this app.
                sendOrderedBroadcast(unzipStartedBroadCast, null);

                if (unZipInPlace(zipFilePath)) {

                    //Broadcast unzip ended
                    Intent unzipEndedBroadCast =
                            new Intent(BROADCAST_ACTION)
                                    // Puts the status into the Intent
                                    .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_UNZIP_ENDED)
                                    .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
                    // Broadcasts the Intent to receivers in this app.
                    sendOrderedBroadcast(unzipEndedBroadCast, null);

                    if (!new File(zipFilePath).delete()) {
                        Log.e(TAG, "Deleting file: ", new IOException("error deleting file at" + zipFilePath));
                    }
                }
            } else if (fileName.equals(DATABASE_NAME + ".zip")) {
                if (unZipInPlace(zipFilePath)) {
                    //Broadcast unzip ended
                    Intent localIntent =
                            new Intent(BROADCAST_ACTION)
                                    // Puts the status into the Intent
                                    .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_BOOKINFORMATION_UNZIP_ENDED);

                    // Broadcasts the Intent to receivers in this app.
                    sendOrderedBroadcast(localIntent, null);
                    //reset the download id to -1 to prevent the receiver from listening to it
                    BookDownloadCompletedReceiver.informationDatabaseDownloadEnqueId = -1;

                    if (!new File(zipFilePath).delete()) {
                        Log.e(TAG, "Deleting file: ", new IOException("error deleting file at" + zipFilePath));
                    }
                }
            }
        }
    }

    /**
     * @param zipFilePath the full qualified path to the zip archive including its extension
     *                    The functions extract all the contents of archive including sub folders to
     *                    its parent directory
     * @return true on success false otherwise
     */
    private boolean unZipInPlace(String zipFilePath) {
        if (new File(zipFilePath).exists()) {
            InputStream inputStream;
            ZipInputStream zipInputStream;
            try {
                inputStream = new FileInputStream(zipFilePath);
                zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
                String destinationPath = new File(zipFilePath).getParent() + File.separator;
                try {

                    ZipEntry zipEntry;
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        String filename = zipEntry.getName().substring(zipEntry.getName().lastIndexOf(File.separatorChar) + 1);


                        if (zipEntry.isDirectory() //Just neglect nested directories
                                || !((BooksInformationDbHelper.uncompressedBookFileRegex.matcher(filename).matches()
                                || filename.equals(BooksDownloader.ONLINE_DATABASE_NAME)))//Neglect any file which doesn't match <bookId>.sqlite in the zip file

                                )
                            continue;
                        if (filename.equals(BooksDownloader.ONLINE_DATABASE_NAME))
                            filename = BooksInformationDbHelper.DATABASE_FULL_NAME;

                        //All files are outputted to the same directory as the zip folder
                        FileOutputStream unZippedOutputStream = new FileOutputStream(destinationPath + filename);

                        while ((count = zipInputStream.read(buffer)) != -1) {
                            unZippedOutputStream.write(buffer, 0, count);
                        }
                        unZippedOutputStream.close();
                        zipInputStream.closeEntry();
                    }

                } finally {
                    zipInputStream.close();
                    inputStream.close();

                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "unpackZip: ", e);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            return true;
        } else {
            Log.d(TAG, "File deleteded before unzip:"+zipFilePath);

            return false;
        }
    }

}

