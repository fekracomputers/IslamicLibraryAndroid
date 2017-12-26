package com.fekracomputers.islamiclibrary;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadFileConstants;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.settings.SettingsActivity;
import com.fekracomputers.islamiclibrary.utility.StorageUtils;

import java.io.File;

import timber.log.Timber;

import static com.fekracomputers.islamiclibrary.utility.StorageUtils.getIslamicLibraryBaseDirectory;

public class SplashActivity extends AppCompatActivity implements CloseDialogFragment.CloseDialogFragmentListener {
    private static final int WRITE_EXTERNAL_STORAGE_PERMESSION = 0;

    private static final long SPLASH_TIME_OUT = 300;
    ProgressBar mProgressBar;
    AlertDialog permissionsDialog;
    private TextView mTextView;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.getBooleanExtra(SettingsActivity.KEY_KILL_APP, false)) {
            finish();
            System.exit(0);
        }
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mProgressBar = findViewById(R.id.progressBar1);
        mTextView = findViewById(R.id.progressTextView);
        mReceiver = new BookInformationDownloadReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadsConstants.BROADCAST_ACTION));
        checkStorage();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        mReceiver = null;
    }

    private boolean canWriteSdcardAfterPermissions() {
        String location = getIslamicLibraryBaseDirectory(this);
        if (location != null) {
            try {
                if (new File(location).exists() || StorageUtils.makeIslamicLibraryShamelaDirectory(this)) {
                    File f = new File(location, "" + System.currentTimeMillis());
                    if (f.createNewFile()) {
                        f.delete();
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e("SplashActivity", e.getMessage(), e);
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMESSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (!canWriteSdcardAfterPermissions()) {
                    Toast.makeText(this,
                            R.string.storage_permission_please_restart, Toast.LENGTH_LONG).show();
                }
                checkBookInformationDatabase();
            } else {
                final File fallbackFile = getExternalFilesDir(null);
                if (fallbackFile != null) {
                    StorageUtils.setAppCustomLocation(fallbackFile.getAbsolutePath(), this);
                    checkBookInformationDatabase();
                } else {
                    // set to null so we can try again next launch
                    StorageUtils.setAppCustomLocation(null, this);
                    finishSplashAndLaunchMainActivity();
                }
            }
        }
    }


    private void finishSplashAndLaunchMainActivity() {
        Intent intent = new Intent(this, BrowsingActivity.class);
        startActivity(intent);
        finish();
    }

    private String statusMessage(Cursor c) {
        String msg;

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "DownloadInfo failed";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "DownloadInfo paused";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "DownloadInfo pending";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "DownloadInfo in progress";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "DownloadInfo complete";
                break;

            default:
                msg = "DownloadInfo is nowhere in sight";
                break;
        }

        return (msg);
    }

    private void checkStorage() {
        final String path = StorageUtils.getAppCustomLocation(this);
        final File fallbackFile = getExternalFilesDir(null);

        boolean usesExternalFileDir = path != null && path.contains(BuildConfig.APPLICATION_ID);

        if ((path == null) || (usesExternalFileDir && (fallbackFile == null))) {
            // suggests that we're on m+ and getExternalFilesDir returned null at some point
            finishSplashAndLaunchMainActivity();
            return;
        }

        boolean needsPermission = !usesExternalFileDir || !path.equals(fallbackFile.getAbsolutePath());

        if (needsPermission && !StorageUtils.haveWriteExternalStoragePermission(this)) {
            // request permission
            //show permission rationale dialog
            permissionsDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.storage_permission_rationale)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
                        permissionsDialog = null;
                        requestExternalSdcardPermission();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        dialog.dismiss();
                        permissionsDialog = null;

                        // fall back if we can
                        if (fallbackFile != null) {
                            StorageUtils.setAppCustomLocation(fallbackFile.getAbsolutePath(), SplashActivity.this);
                            checkBookInformationDatabase();
                        } else {
                            // set to null so we can try again next launch
                            StorageUtils.setAppCustomLocation(null, SplashActivity.this);
                            finishSplashAndLaunchMainActivity();
                        }
                    })
                    .create();
            permissionsDialog.show();

        } else {
            checkBookInformationDatabase();
        }

    }

    private void checkBookInformationDatabase() {
        //a better condition would be to check the directory
        if (BooksInformationDbHelper.databaseFileExists(SplashActivity.this)) {
            BooksInformationDbHelper instance = BooksInformationDbHelper.getInstance(this);
            try {
                instance.vlidate();
                finishSplashAndLaunchMainActivity();
            } catch (Exception e) {
                Timber.e(e);
                instance.deleteBookInformationFile();
                showBookInformationFailurMessage();
            }

        } else if (StorageUtils.isOldDirectoriesExists(this)) {
            Timber.e("isOldDirectoriesExists");
            new AsyncTask<Void, Integer, Void>() {
                @Override
                protected void onPreExecute() {
                    String oldBooksPath = getIslamicLibraryBaseDirectory(SplashActivity.this);
                    if (oldBooksPath == null) return;
                    File oldPath = new File(oldBooksPath);
                    if (oldPath.exists() && oldPath.isDirectory()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setIndeterminate(false);
                        mTextView.setText(R.string.info_changing_file_structure);
                        mProgressBar.setMax(oldPath.list().length);

                    }
                }

                @Override
                protected Void doInBackground(Void... params) {
                    StorageUtils.makeIslamicLibraryShamelaDirectory(SplashActivity.this);
                    String oldBooksPath = getIslamicLibraryBaseDirectory(SplashActivity.this);
                    if (oldBooksPath == null) return null;
                    File oldPath = new File(oldBooksPath);
                    if (oldPath.exists() && oldPath.isDirectory()) {
                        String[] files = oldPath.list();
                        for (int i = 0; i < files.length; i++) {
                            String book = files[i];
                            File from = new File(oldBooksPath + File.separator + book);
                            if (!from.isDirectory()) {
                                File to = new File(oldBooksPath +
                                        File.separator +
                                        DownloadFileConstants.SHAMELA_BOOKS_DIR +
                                        File.separator +
                                        book);
                                from.renameTo(to);
                            }
                            publishProgress(i);
                        }
                        return null;
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    mProgressBar.setProgress(values[0]);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    SplashActivity.this.finishSplashAndLaunchMainActivity();
                }
            }.execute();

        } else {
            new InitialSetupThread().start();
        }
    }

    private void requestExternalSdcardPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                WRITE_EXTERNAL_STORAGE_PERMESSION);
        StorageUtils.setSdcardPermissionsDialogPresented(this);
    }

    @Override
    public void onOkPressed() {
        finish();
    }

    private void showBookInformationFailurMessage() {
        if (getWindow() != null && getWindow().getDecorView().isShown()) {
            DialogFragment dialogFragment = new CloseDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "CloseDialogFragment");
        } else
            Toast.makeText(this, R.string.book_information_error_close, Toast.LENGTH_LONG).show();
    }

    private class InitialSetupThread extends Thread {
        @Override
        public void run() {
            StorageUtils.makeIslamicLibraryShamelaDirectory(SplashActivity.this);
            BooksDownloader booksDownloader = new BooksDownloader(SplashActivity.this);
            long downloadId = booksDownloader.DownloadBookInformationDatabase(true);

            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            boolean downloading = true;
            runOnUiThread(() -> {
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mTextView.setText(R.string.downloading_book_information);
                mProgressBar.setIndeterminate(false);
            });

            while (downloading) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadId);
                Cursor cursor = manager.query(q);
                if (cursor.moveToFirst()) {
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    } else if (downloadStatus == DownloadManager.STATUS_FAILED) {
                        showBookInformationFailurMessage();
                        break;
                    }
                    cursor.close();
                    long dl_progress;
                    if (bytes_total > 0) {
                        dl_progress = (bytes_downloaded * 100L / bytes_total);
                    } else {
                        dl_progress = 0;
                    }

                    runOnUiThread(() -> mProgressBar.setProgress((int) dl_progress));
                } else {
                    runOnUiThread(SplashActivity.this::showBookInformationFailurMessage);
                    break;
                }
            }
            runOnUiThread(() -> {
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.VISIBLE);
                mProgressBar.setIndeterminate(true);
                mTextView.setText(R.string.info_preparing_book_information_database);

            });
        }
    }

    private class BookInformationDownloadReceiver extends BroadcastReceiver {

        private BookInformationDownloadReceiver() {
        }

        /**
         * This method is called by the system when a broadcast Intent is matched by this class'
         * intent filters
         *
         * @param context An Android context
         * @param intent  The incoming broadcast Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(DownloadsConstants.EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_INVALID);
            if (intExtra == DownloadsConstants.STATUS_BOOKINFORMATION_FTS_INDEXING_ENDED) {
                finishSplashAndLaunchMainActivity();
            } else if (intExtra == DownloadsConstants.STATUS_BOOKINFORMATION_FAILED) {
                showBookInformationFailurMessage();
            }

        }
    }
}
    

