package com.fekracomputers.islamiclibrary.download.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fekracomputers.islamiclibrary.SplashActivity;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.reciver.BookDownloadCompletedReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.COMPRESSION_EXTENSION;
import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.DATABASE_NAME;
import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.REPEATED_COMPRESSED_DATABASE_FULL_NAME;
import static com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper.repeatedCompressedBookFileRegex;
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

    private static void deleteFileWithException(String zipFilePath) {
        File file = new File(zipFilePath);
        if (file.exists()) {
            if (!file.delete()) {
                Timber.e(TAG, "Deleting file: ", new IOException("error deleting file at" + zipFilePath));
            }
        }
    }

    public static boolean unzip(@NonNull String zipFilePath, @NonNull String destinationPath) {
        return unzip(zipFilePath, destinationPath, null, null);
    }

    public static boolean unzip(@NonNull InputStream inputStream,
                                @NonNull String destinationPath,
                                @Nullable SplashActivity.DownloadProgressCallBack consumer) {
        return unzip(null, destinationPath, inputStream, consumer);
    }

    private static boolean unzip(@Nullable String zipFilePath,
                                 String destinationPath,
                                 @Nullable InputStream inputStream,
                                 @Nullable SplashActivity.DownloadProgressCallBack consumer) {
        if (zipFilePath == null && inputStream == null)
            throw new IllegalArgumentException("either supply input path or stream");
        if ((zipFilePath != null && new File(zipFilePath).exists()) || inputStream != null) {
            ZipInputStream zipInputStream;
            boolean somethingFound = false;
            try {
                if (inputStream == null)
                    inputStream = new FileInputStream(zipFilePath);
                zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
                try {
                    ZipEntry zipEntry;
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                        somethingFound = true;
                        String filename = zipEntry.getName().substring(zipEntry.getName().lastIndexOf(File.separatorChar) + 1);
                        if (zipEntry.isDirectory() //Just neglect nested directories
                                || !((BooksInformationDbHelper.uncompressedBookFileRegex.matcher(filename).matches()
                                || filename.equals(DownloadFileConstants.ONLINE_DATABASE_NAME)))//Neglect any file which doesn't match <bookId>.sqlite in the zip file
                                )
                            continue;
                        if (filename.equals(DownloadFileConstants.ONLINE_DATABASE_NAME))
                            filename = BooksInformationDbHelper.DATABASE_FULL_NAME;
                        String outputFullPath = destinationPath + File.separator + filename;
                        //delete any existing file at the same path
                        deleteFileWithException(outputFullPath);
                        //All files are outputted to the same directory as the zip folder
                        FileOutputStream unZippedOutputStream = new FileOutputStream(outputFullPath, false);

                        while ((count = zipInputStream.read(buffer)) != -1) {
                            if (consumer != null) {
                                consumer.accept(count);
                            }
                            unZippedOutputStream.write(buffer, 0, count);
                        }
                        unZippedOutputStream.close();
                        zipInputStream.closeEntry();
                    }

                } catch (IOException e) {
                    Timber.e("unpackZip: ", e);
                    return false;
                } finally {
                    zipInputStream.close();
                    inputStream.close();
                }
            } catch (FileNotFoundException e) {
                Timber.e("unpackZip: ", e);
                return false;
            } catch (IOException e) {
                Timber.e(e);
                return false;
            } catch (Exception e) {
                Timber.e(e);
                return false;
            }
            return somethingFound;

        } else {
            Log.e(TAG, "File deleteded before unzip:" + zipFilePath);
            return false;
        }


    }

    /**
     * @param zipFilePath the full qualified path to the zip archive including its extension
     *                    The functions extract all the contents of archive including sub folders to
     *                    its parent directory
     * @return true on success false otherwise
     */
    public static boolean unZipInPlace(String zipFilePath) {
        String destinationPath = new File(zipFilePath).getParent() + File.separator;
        return unzip(zipFilePath, destinationPath);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        String zipFilePath = workIntent.getStringExtra(EXTRA_FILE_PATH);

        try {
            if (zipFilePath != null) {
                String fileName = zipFilePath.substring(zipFilePath.lastIndexOf(File.separatorChar) + 1);
                String fileNameWithOutExtension = fileName.substring(0, fileName.lastIndexOf('.')
                );
                if (BooksInformationDbHelper.compressedBookFileRegex.matcher(fileName).matches()) {
                    //Broadcast unzip Started
                    int bookId = Integer.parseInt(fileNameWithOutExtension);
                    unZipBook(zipFilePath, bookId);
                    deleteFileWithException(zipFilePath);
                } else if (repeatedCompressedBookFileRegex.matcher(fileName).matches()) {
                    Matcher matcher = repeatedCompressedBookFileRegex.matcher(fileName);
                    String correctFilename = matcher.group(1);
                    int bookId = Integer.parseInt(correctFilename);
                    unZipBook(zipFilePath, bookId);
                    int repeatedCount = Integer.parseInt(matcher.group(2));
                    deleteRepeatedBooks(zipFilePath, bookId, repeatedCount);
                } else if (fileName.equals(DATABASE_NAME + ".zip")) {
                    if (unZipInPlace(zipFilePath)) {
                        announceBooksInformationUnzipSuccess(zipFilePath);
                    } else {
                        //Broadcast unzip ended
                        announceBooksInformationUnzipFailed(zipFilePath);
                    }
                    deleteFileWithException(zipFilePath);

                } else if (REPEATED_COMPRESSED_DATABASE_FULL_NAME.matcher(fileName).matches()) {
                    Matcher matcher = REPEATED_COMPRESSED_DATABASE_FULL_NAME.matcher(fileName);
                    matcher.matches();
                    boolean b = unZipInPlace(zipFilePath);
                    int repeatedCount = Integer.parseInt(matcher.group(1));
                    if (b) {
                        //Broadcast unzip ended
                        announceBooksInformationUnzipSuccess(zipFilePath);
                    } else {
                        announceBooksInformationUnzipFailed(zipFilePath);

                    }
                    deleteRepeatedBooksInformation(zipFilePath, repeatedCount);
                }
            }

        } catch (NumberFormatException e) {
            Timber.e(e);
        }
    }

    private void announceBooksInformationUnzipSuccess(String zipFilePath) {
        //Broadcast unzip ended
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_BOOKINFORMATION_UNZIP_ENDED)
                        .putExtra(EXTRA_FILE_PATH, zipFilePath);

        // Broadcasts the Intent to receivers in this app.
        sendOrderedBroadcast(localIntent, null);
        //reset the download id to -1 to prevent the receiver from listening to it
        BookDownloadCompletedReceiver.informationDatabaseDownloadEnqueId = -1;
    }

    private void announceBooksInformationUnzipFailed(String zipFilePath) {
        //Broadcast unzip ended
        Intent localIntent =
                new Intent(BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_BOOKINFORMATION_FAILED)
                        .putExtra(EXTRA_FILE_PATH, zipFilePath);
        // Broadcasts the Intent to receivers in this app.
        sendOrderedBroadcast(localIntent, null);
        BookDownloadCompletedReceiver.informationDatabaseDownloadEnqueId = -1;
    }

    private void deleteRepeatedBooksInformation(String zipFilePath, int repeatedCount) {
        String folder = zipFilePath.substring(0, zipFilePath.lastIndexOf(File.separatorChar));
        for (int i = repeatedCount; i >= 1; i--) {
            String pathname = folder + File.separator + DATABASE_NAME + "-" + i + "." + COMPRESSION_EXTENSION;
            File file = new File(pathname);
            if (file.exists()) {
                if (!file.delete()) {
                    Timber.e(TAG, "Deleting file: ", new IOException("error deleting file at" + pathname));
                }
            }
        }
        String pathname = folder + File.separator + DATABASE_NAME + "." + COMPRESSION_EXTENSION;
        File file = new File(pathname);
        if (file.exists()) {
            if (!file.delete()) {
                Timber.e(TAG, "Deleting file: ", new IOException("error deleting file at" + pathname));
            }
        }
    }

    private void deleteRepeatedBooks(String zipFilePath, int bookId, int repeatedCount) {
        String folder = zipFilePath.substring(0, zipFilePath.lastIndexOf(File.separatorChar));
        for (int i = repeatedCount; i >= 1; i--) {
            String pathname = folder + File.separator + bookId + "-" + i + "." + COMPRESSION_EXTENSION;
            File file = new File(pathname);
            if (file.exists()) {
                if (!file.delete()) {
                    Timber.e(TAG, "Deleting file: ", new IOException("error deleting file at" + pathname));
                }
            }
        }
        String pathname = folder + File.separator + bookId + "." + COMPRESSION_EXTENSION;
        File file = new File(pathname);
        if (file.exists()) {
            if (!file.delete()) {
                Timber.e(TAG, "Deleting file: ", new IOException("error deleting file at" + pathname));
            }
        }
    }

    private void unZipBook(String zipFilePath, int bookId) {
        Intent unzipStartedBroadCast =
                new Intent(BROADCAST_ACTION)
                        .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_UNZIP_STARTED)
                        .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId);
        // Broadcasts the Intent to receivers in this app.
        sendOrderedBroadcast(unzipStartedBroadCast, null);

        if (unZipInPlace(zipFilePath)) {
            if (validateDatabase(bookId)) {
                //Broadcast unzip ended
                Intent unzipEndedBroadCast =
                        new Intent(BROADCAST_ACTION)
                                // Puts the status into the Intent
                                .putExtra(EXTRA_DOWNLOAD_STATUS, STATUS_UNZIP_ENDED)
                                .putExtra(DownloadsConstants.EXTRA_DOWNLOAD_BOOK_ID, bookId)
                                .putExtra(EXTRA_FILE_PATH, zipFilePath);
                // Broadcasts the Intent to receivers in this app.
                sendOrderedBroadcast(unzipEndedBroadCast, null);
            } else {
                BookDownloadCompletedReceiver.broadCastBookDownloadFailed(bookId, "invalidDatabase", this);
            }
        } else {
            BookDownloadCompletedReceiver.broadCastBookDownloadFailed(bookId, "invalid Zip file", this);
        }
    }

    private boolean validateDatabase(int bookId) {
        BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(this, bookId);
        return bookDatabaseHelper.isValidBook();
    }


}

