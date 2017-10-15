package com.fekracomputers.islamiclibrary.browsing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.utility.Util;

import java.util.ArrayList;

/**
 * Activity used to display a {@link BookInformationFragment} used when no room for multi-pams
 */
public class BookInformationActivity extends AppCompatActivity implements BookCardEventListener {

    private boolean mIsArabic;
    private ArrayList<BrowsingActivityListingFragment> mDownloadStatusUpdateListener = new ArrayList<>();
    private BookCardEventsCallback bookCardEventsCallback = new BookCardEventsCallback(this) {
        @Override
        public synchronized void notifyBookDownloadStatusUpdate(int bookId, int downloadStatus) {
            for (BrowsingActivityListingFragment browsingActivityListingFragment : mDownloadStatusUpdateListener) {
                browsingActivityListingFragment.BookDownloadStatusUpdate(bookId, downloadStatus);
            }
        }

        @Override
        public void notifyBookDownloadStatusUpdate() {
            for (BrowsingActivityListingFragment browsingActivityListingFragment : mDownloadStatusUpdateListener) {
                browsingActivityListingFragment.reAcquireCursors();
            }
        }


    };

    @Override
    public void registerListener(BrowsingActivityListingFragment browsingActivityListingFragment) {
        mDownloadStatusUpdateListener.add(browsingActivityListingFragment);

    }

    @Override
    public void unRegisterListener(BrowsingActivityListingFragment browsingActivityListingFragment) {
        mDownloadStatusUpdateListener.remove(browsingActivityListingFragment);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_information);
        Intent callingIntent = getIntent();
        int bookId = callingIntent.getIntExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, 0);
        String bookName = callingIntent.getStringExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE);

        setSupportActionBar(findViewById(R.id.toolbar));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.book_information_fragment_container, BookInformationFragment.newInstance(bookId)).commit();

        mIsArabic = Util.isArabicUi(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);

    }

    @Override
    public void onStart() {
        super.onStart();
        bookCardEventsCallback.intializeListener();

    }

    @Override
    public void onStop() {
        super.onStop();
        bookCardEventsCallback.removeBookDownloadBroadcastListener();
    }


    @Override
    public BookCardEventsCallback getBookCardEventCallback() {
        return bookCardEventsCallback;
    }
}
