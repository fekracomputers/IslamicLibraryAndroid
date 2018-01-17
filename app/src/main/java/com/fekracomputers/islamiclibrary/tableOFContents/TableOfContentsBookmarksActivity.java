package com.fekracomputers.islamiclibrary.tableOFContents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseContract;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.Bookmark;
import com.fekracomputers.islamiclibrary.model.Highlight;
import com.fekracomputers.islamiclibrary.model.Title;
import com.fekracomputers.islamiclibrary.reading.ReadingActivity;
import com.fekracomputers.islamiclibrary.settings.SettingsActivity;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.BookmarkFragment;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.HighlightFragment;
import com.fekracomputers.islamiclibrary.tableOFContents.fragment.TableOfContentFragment;
import com.fekracomputers.islamiclibrary.utility.Util;

import java.util.ArrayList;

/**
 * Activity to display pager for {@link TableOfContentFragment}, {@link HighlightFragment},
 * {@link TableOfContentFragment}
 * and {@link BookmarkFragment}
 */
public class TableOfContentsBookmarksActivity extends AppCompatActivity
        implements BookmarkFragment.onBookmarkClickListener,
        TableOfContentFragment.OnTableOfContentTitleClickListener,
        HighlightFragment.onHighlightClickListener,
        BookCardEventListener {

    private int bookId;
    private String bookName;
    private boolean mIsArabic;
    private int pageId;
    private boolean buildHistory;
    private int titleId;
    private BookPartsInfo mBooksPartInfo;
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

        @Override
        protected void notifyBookDownloadFailed(int bookId, String failurReason) {

        }

    };

    @Override
    public void registerListener(BrowsingActivityListingFragment listener) {
        mDownloadStatusUpdateListener.add(listener);
    }

    @Override
    public void unRegisterListener(BrowsingActivityListingFragment listener) {
        mDownloadStatusUpdateListener.remove(listener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int initial_section = intent.getIntExtra(ReadingActivity.KEY_TAB_NAME, 0);
        bookId = intent.getIntExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, 0);
        bookName = intent.getStringExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE);
        setContentView(R.layout.activity_book_toc_bookmarks);
        bookCardEventsCallback.intializeListener();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(bookName);
        }


        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (intent.hasExtra(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID)) {
            pageId = intent.getIntExtra(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID, 0);
            titleId = intent.getIntExtra(BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID, 0);
            buildHistory = true;
            mViewPager.setCurrentItem(initial_section);
        } else {
            mViewPager.setCurrentItem(initial_section);
        }
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mBooksPartInfo = BookDatabaseHelper.getInstance(this, bookId).getBookPartsInfo();
        mIsArabic = Util.isArabicUi(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_book_toc_bookmarks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;


        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBookmarkClicked(Bookmark bookmark) {
        finishAndGoTo(bookmark.pageInfo.pageId);
    }

    @Override
    public void OnOnTableOfContentTitleClicked(Title title) {
        finishAndGoTo(title.pageInfo.pageId);
    }

    @Override
    public BookPartsInfo getBookPartsInfo() {
        return mBooksPartInfo;
    }

    @Override
    public void onHighlightClicked(Highlight highlight) {
        finishAndGoTo(highlight.pageInfo.pageId);
    }

    private void finishAndGoTo(int pageId) {
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra(BookDatabaseContract.PageEntry.COLUMN_NAME_PAGE_ID, pageId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bookCardEventsCallback.removeBookDownloadBroadcastListener();
    }

    @Override
    public BookCardEventsCallback getBookCardEventCallback() {
        return bookCardEventsCallback;
    }


    public enum TableOfContentAndNotesTab {
        TAB_TABLE_OF_CONTENTS(R.string.tab_Table_of_Contents),
        TAB_Notes_and_Highlights(R.string.tab_Notes_and_Highlights),
        TAB_BOOKMARKS(R.string.tab_Bookmarks),
        TAB_OVERVIEW(R.string.tab_Overview);

        private final int nameResId;

        TableOfContentAndNotesTab(int nameResId) {
            this.nameResId = nameResId;
        }

        public int getNameResId() {
            return nameResId;
        }


    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment;

            if (position == TableOfContentAndNotesTab.TAB_TABLE_OF_CONTENTS.ordinal()) {
                fragment = TableOfContentFragment.newInstance(bookId, bookName, buildHistory, pageId, titleId);

            } else if (position == TableOfContentAndNotesTab.TAB_OVERVIEW.ordinal()) {
                fragment = BookInformationFragment.newInstance(bookId);
            } else if (position == TableOfContentAndNotesTab.TAB_BOOKMARKS.ordinal()) {
                fragment = BookmarkFragment.newInstance(bookId);
            } else if (position == TableOfContentAndNotesTab.TAB_Notes_and_Highlights.ordinal()) {
                fragment = HighlightFragment.newInstance(bookId);
            } else {
                fragment = TableOfContentFragment.newInstance(bookId, bookName);

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TableOfContentAndNotesTab.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TableOfContentAndNotesTab.values()[position].getNameResId());
        }
    }
}
