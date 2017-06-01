package com.fekracomputers.islamiclibrary.download.view;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.dialog.CancelDownloadDialogFragment;
import com.fekracomputers.islamiclibrary.download.model.DownloadInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.fekracomputers.islamiclibrary.browsing.activity.BrowsingActivity.KEY_NUMBER_OF_BOOKS_TO_DONLOAD;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.BROADCAST_ACTION;
import static com.fekracomputers.islamiclibrary.download.model.DownloadsConstants.EXTRA_NOTIFY_WITHOUT_BOOK_ID;


public class DownloadProgressActivity extends AppCompatActivity implements CancelDownloadDialogFragment.CancelDownloadDialogFragmentListener {

    private static final int CANCELLED_DOWNLOAD_TYPE = 0;
    private static final int FINISHED_DOWNLOAD_TYPE = 1;
    private static final int ZERO_DOWNLOAD_TYPE = 2;
    DownlandProgressRecyclerViewAdapter bookmarkRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private DownloadProgressAsncTask downloadProgressAsncTask;
    private Menu mMenu;
    private BooksInformationDbHelper booksInformationDbHelper;
    private boolean mShowCancelAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_progress);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            supportActionBar.setTitle(R.string.title_activity_download_progress);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        booksInformationDbHelper = BooksInformationDbHelper.getInstance(this);
        ArrayList<Long> downloads = booksInformationDbHelper.getPendingDownloads();

        if (downloads.size() != 0) {
            mShowCancelAll=true;
            downloadProgressAsncTask = new DownloadProgressAsncTask();
            downloadProgressAsncTask.execute(downloads.toArray(new Long[0]));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        } else {
            showAlternativeView(ZERO_DOWNLOAD_TYPE);
        }
    }

    void showAlternativeView(int viewType) {
        FrameLayout recyclerFrame = (FrameLayout) findViewById(R.id.recycler_frame);
        recyclerFrame.setVisibility(View.GONE);
        mShowCancelAll = false;

        switch (viewType) {
            case ZERO_DOWNLOAD_TYPE:
                ViewStub zeroView = (ViewStub) findViewById(R.id.zero_downloads);
                zeroView.setVisibility(View.VISIBLE);
                break;
            case FINISHED_DOWNLOAD_TYPE:
                ViewStub finishedView = (ViewStub) findViewById(R.id.finished_downloads);
                finishedView.setVisibility(View.VISIBLE);


                break;
            case CANCELLED_DOWNLOAD_TYPE:
                ViewStub cancelledView = (ViewStub) findViewById(R.id.cancelled_downloads);
                cancelledView.setVisibility(View.VISIBLE);
                break;

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 10000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowCancelAll) {
            getMenuInflater().inflate(R.menu.activity_download_progress, menu);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
            case R.id.cancel_all_downloads:
                Bundle CancelDownloadDialogFragmentBundle = new Bundle();
                CancelDownloadDialogFragmentBundle.putInt(KEY_NUMBER_OF_BOOKS_TO_DONLOAD, bookmarkRecyclerViewAdapter.getItemCount());
                DialogFragment CancelDownloadDialogFragment = new CancelDownloadDialogFragment();
                CancelDownloadDialogFragment.setArguments(CancelDownloadDialogFragmentBundle);
                CancelDownloadDialogFragment.show(getSupportFragmentManager(), "CancelDownloadDialogFragment");
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onCancelAllDialogPositiveClick() {
        downloadProgressAsncTask.cancel(true);
    }

    private void showProgress() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.removing_downloads_frame);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    private class DownloadProgressAsncTask extends AsyncTask<Long, DownloadInfoUpdate, DownloadInfoUpdate> {

        private List<DownloadInfo> mOldDownloadList;
        private boolean mFirstTime;

        public DownloadProgressAsncTask() {
            this.mFirstTime = true;
        }

        @Override
        protected DownloadInfoUpdate doInBackground(Long... ids) {
            //Convert Long[] to long[]
            long candidateWatchedIds[] = new long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                candidateWatchedIds[i] = ids[i];
            }

            while (true) {

                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Query BooksDownloadQuery = new DownloadManager.Query();
                BooksDownloadQuery.setFilterById(candidateWatchedIds);
                BooksDownloadQuery.setFilterByStatus(DownloadManager.STATUS_RUNNING |
                        DownloadManager.STATUS_PAUSED |
                        DownloadManager.STATUS_PENDING);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = downloadManager.query(BooksDownloadQuery);
                int progressingDownloadCount = cursor.getCount();

                List<DownloadInfo> progressDownloadInfo = new ArrayList<>();
                if (progressingDownloadCount != 0) {
                    for (int i = 0; i < progressingDownloadCount; i++) {
                        cursor.moveToPosition(i);
                        DownloadInfo downloadInfo = new DownloadInfo(cursor);
                        progressDownloadInfo.add(i, downloadInfo);
                        Log.d("download_iter", downloadInfo.toString());
                    }
                    Collections.sort(progressDownloadInfo, new Comparator<DownloadInfo>() {
                        @Override
                        public int compare(DownloadInfo o1, DownloadInfo o2) {
                            if (o1.getProgressPercent() != 0 && o2.getProgressPercent() != 0) {
                                return o1.compareTo(o2);
                            } else if (o1.getProgressPercent() != 0 && o2.getProgressPercent() == 0) {
                                return -1;

                            } else if (o1.getProgressPercent() == 0 && o2.getProgressPercent() != 0) {
                                return 1;

                            } else //(o1.getProgressPercent() == 0 && o2.getProgressPercent() == 0)
                            {
                                return o1.compareTo(o2);
                            }

                        }
                    });
                }
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                        new DownloadInfo.DownloadInfoDiffCallback(mOldDownloadList, progressDownloadInfo));
                mOldDownloadList = progressDownloadInfo;
                cursor.close();


                if (progressingDownloadCount != 0) {
                    publishProgress(new DownloadInfoUpdate(progressDownloadInfo, diffResult));
                } else {
                    return new DownloadInfoUpdate(progressDownloadInfo, diffResult);
                }

                if (isCancelled()) {
                    publishProgress(new DownloadInfoUpdate(DownloadInfoUpdate.TYPE_CANCEL));
                    DownloadManager.Query non_complete_query = new DownloadManager.Query();
                    non_complete_query.setFilterByStatus(DownloadManager.STATUS_FAILED |
                            DownloadManager.STATUS_PENDING |
                            DownloadManager.STATUS_RUNNING);
                    non_complete_query.setFilterById(candidateWatchedIds);
                    Cursor c = downloadManager.query(non_complete_query);
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_ID);
                    //TODO this loop may cause problems if a download completed and the broadcast is triggered before we cancel it
                    while (c.moveToNext()) {
                        long enquId = c.getLong(columnIndex);
                        downloadManager.remove(enquId);
                    }
                    booksInformationDbHelper.cancelMultipleDownloads(c, columnIndex);
                    Intent localIntent =
                            new Intent(BROADCAST_ACTION)
                                    .putExtra(EXTRA_NOTIFY_WITHOUT_BOOK_ID, true);
                    DownloadProgressActivity.this.sendOrderedBroadcast(localIntent, null);
                    c.close();

                    return null;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(DownloadInfoUpdate... diffResult) {
            if (diffResult[0].type == CANCELLED_DOWNLOAD_TYPE) {
                showProgress();
            } else if (!mFirstTime) {
                updateAdaperAndDipatchChanges(diffResult[0]);
            } else {
                bookmarkRecyclerViewAdapter = new DownlandProgressRecyclerViewAdapter(DownloadProgressActivity.this, mOldDownloadList);
                bookmarkRecyclerViewAdapter.setHasStableIds(true);
                recyclerView.setAdapter(bookmarkRecyclerViewAdapter);
                mFirstTime = false;
            }
        }


        @Override
        protected void onPostExecute(DownloadInfoUpdate diffResult) {
            if (!mFirstTime) {
                updateAdaperAndDipatchChanges(diffResult);
                showAlternativeView(FINISHED_DOWNLOAD_TYPE);
            } else {
                showAlternativeView(ZERO_DOWNLOAD_TYPE);
                mFirstTime = false;
            }
        }

        @Override
        protected void onCancelled() {
            showAlternativeView(CANCELLED_DOWNLOAD_TYPE);
        }

        private void updateAdaperAndDipatchChanges(DownloadInfoUpdate newItems) {
            bookmarkRecyclerViewAdapter.updateItems(newItems);
        }
    }


}