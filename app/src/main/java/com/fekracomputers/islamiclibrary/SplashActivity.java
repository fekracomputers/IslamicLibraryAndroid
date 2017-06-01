package com.fekracomputers.islamiclibrary;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;

public class SplashActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_PERMESSION = 0;

    private static final long SPLASH_TIME_OUT = 0;
    ProgressBar mProgressBar;
    private TextView mTextView;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mTextView = (TextView) findViewById(R.id.progressTextView);
        // Here, thisActivity is the current activity
        if (!externalStoragePermissionGranted()) {
            requestPermission();
        } else {
            new IntialSetupthread().start();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mReceiver = new BookInformationDownloadReceiver();
        registerReceiver(mReceiver, new IntentFilter(
                DownloadsConstants.BROADCAST_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
        mReceiver = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMESSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    new IntialSetupthread().start();

                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    Toast.makeText(this, "Permission Denied, You cannot use local drive .", Toast.LENGTH_LONG).show();
                    requestPermission();
                    finish();
                }
                break;
        }
    }


    private boolean externalStoragePermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMESSION);
        }


    }

    private class IntialSetupthread extends Thread {
        @Override
        public void run() {
            if (BooksInformationDbHelper.databaseExists()) {

//                try {
//                    sleep(SPLASH_TIME_OUT);
//                } catch (InterruptedException e) {
//                    Log.e("IntialSetupthread", "run: ", e);;
//                }
                finishSplash();

//                BooksInformationDbHelper storedBooksDatabase = BooksInformationDbHelper.getInstance(SplashActivity.this);
//                if (storedBooksDatabase != null && storedBooksDatabase.refreshBooksDbWithDirectory(SplashActivity.this)) {
//                }

            } else//TODO First Run of application
            {
                BooksDownloader booksDownloader = new BooksDownloader(SplashActivity.this);
                long downloadId = booksDownloader.DownloadBookInformationDatabase(true);

                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                boolean downloading = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.VISIBLE);
                        mTextView.setText(R.string.downloading_book_information);
                        mProgressBar.setIndeterminate(false);
                    }
                });

                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = manager.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }
                    cursor.close();

                    final long dl_progress = (bytes_downloaded * 100L / bytes_total);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress((int) dl_progress);

                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.VISIBLE);
                        mProgressBar.setIndeterminate(true);
                        mTextView.setText(R.string.info_preparing_book_information_database);

                    }
                });
            }
        }

    }

    private void finishSplash() {
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
            if (intent.getIntExtra(DownloadsConstants.EXTRA_DOWNLOAD_STATUS, DownloadsConstants.STATUS_INVALID) == DownloadsConstants.STATUS_BOOKINFORMATION_FTS_INDEXING_ENDED) {
                finishSplash();
            }
        }
    }
}
    

