package com.fekracomputers.islamiclibrary.browsing.activity;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.dialog.ConfirmBatchDownloadDialogFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.AuthorListFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookCategoryFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookFilterPagerFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.service.RefreshBooksWithDirectoryService;
import com.fekracomputers.islamiclibrary.download.view.DownloadProgressActivity;
import com.fekracomputers.islamiclibrary.model.AuthorInfo;
import com.fekracomputers.islamiclibrary.model.BookCategory;
import com.fekracomputers.islamiclibrary.search.view.SearchRequestPopupFragment;
import com.fekracomputers.islamiclibrary.search.view.SearchResultActivity;
import com.fekracomputers.islamiclibrary.search.view.SearchResultFragment;
import com.fekracomputers.islamiclibrary.settings.AboutActivity;
import com.fekracomputers.islamiclibrary.settings.AboutUtil;
import com.fekracomputers.islamiclibrary.settings.SettingsActivity;
import com.fekracomputers.islamiclibrary.utility.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.fekracomputers.islamiclibrary.search.view.SearchResultFragment.ARG_IS_GLOBAL_SEARCH;

/**
 * The main Class for browsing books to download
 * its display depends on the display width
 * may display one ,two or three pans each including the suitable fragment
 * fragments that don't have room to be displayed displayed in new activities
 */
public class BrowsingActivity
        extends AppCompatActivity
        implements BookCategoryFragment.OnCategoryItemClickListener,
        AuthorListFragment.OnAuthorItemClickListener,
        BookFilterPagerFragment.OnBookFilterPagerPageChangedListener,
        SearchRequestPopupFragment.OnSearchPopupFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        BookCardEventListener, ConfirmBatchDownloadDialogFragment.BatchDownloadConfirmationListener {

    public static final int AUTHOR_LIST_FRAGMENT_TYPE = 0;
    public static final int BOOK_CATEGORY_FRAGMENT_TYPE = 1;
    public static final int BOOK_LIST_FRAGMENT_TYPE = 2;
    public static final int BOOK_INFORMATION_TYPE = 3;

    public static final String NUMBER_OF_PANS_KEY = "NUMBER_OF_PANS_KEY";
    public static final String BOOK_LIST_FRAGMENT_TAG = "BookListFragment";
    public static final String BOOK_INFORMATION_FRAGMENT_TAG = "BookInformationFragment";
    public static final String KEY_NUMBER_OF_BOOKS_TO_DONLOAD = "KEY_NUMBER_OF_BOOKS_TO_DONLOAD";
    protected static final String TAG = "BrowsingActivity";
    protected static final String KEY_DOWNLOADED_ONLY = "shared_pref_download_only_Key";
    private static final String BOOK_LIST_FRAGMENT_ADDED = "BOOK_LIST_FRAGMENT_ADDED";
    private static final String BOOK_INFORMATION_FRAGMENT_ADDED = "BOOK_INFORMATION_FRAGMENT_ADDED";
    protected int mPaneNumber;
    protected ActionMode.Callback mSelectionActionModeCallBack;
    protected HashSet<Integer> selectedBooksIds = new HashSet<>();
    protected ActionMode mActionMode;
    protected boolean mIsArabic;
    protected TextView mDownloadOnlyBanner;
    protected boolean mShouldDisplayDownloadOnly;
    protected SwitchCompat downloadedOnlySwitch;
    protected List<BrowsingActivityListingFragment> pagerTabs = new ArrayList<>();
    protected SearchView mSearchView;
    /**
     * this variable is here to be overrided in subclasses to allow calling activity super constructors
     */
    View bookListContainer;
    BookFilterPagerFragment pagerFragment;
    BooksInformationDbHelper mBooksInformationDbHelper;
    protected CompoundButton.OnCheckedChangeListener onDownloadSwitchCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switchDownloadOnlyFilter(isChecked);
        }


    };
    private HashSet<Integer> mBooksToDownload = new HashSet<>();
    private BookCardEventsCallback bookCardEventsCallback = new BookCardEventsCallback(this) {
        @Override
        public boolean OnBookItemLongClicked(int bookId) {
            if (mActionMode != null) {
                return false;
            }
            if (mSelectionActionModeCallBack == null) {
                mSelectionActionModeCallBack = new SelectionActionModeCallBack();
            }
            mActionMode = startSupportActionMode(mSelectionActionModeCallBack);
            selectedBooksIds.add(bookId);
            notifySelectionStateChanged(BOOK_LIST_FRAGMENT_TYPE);
            return true;
        }

        @Override
        public boolean isBookSelected(int bookId) {
            return selectedBooksIds.contains(bookId);
        }

        @Override
        public void bookSelected(int bookId, boolean checked) {
            if (checked) {
                selectedBooksIds.add(bookId);
                notifySelectionStateChanged(BOOK_LIST_FRAGMENT_TYPE);
            } else {
                selectedBooksIds.remove(bookId);
                notifySelectionStateChanged(BOOK_LIST_FRAGMENT_TYPE);
            }
        }

        @Override
        public boolean shouldDisplayDownloadedOnly() {
            return BrowsingActivity.this.shouldDisplayDownloadedOnly();
        }

        @Override
        public void mayBeSetTitle(String title) {
            BrowsingActivity.this.mayBeSetTitle(title);
        }

        @Override
        public void onAuthorClicked(AuthorInfo authorInfo) {
            pagerFragment.switchTo(AUTHOR_LIST_FRAGMENT_TYPE);
            for (BrowsingActivityListingFragment browsingActivityListingFragment : pagerTabs) {
                if (browsingActivityListingFragment.getType() == AUTHOR_LIST_FRAGMENT_TYPE) {
                    browsingActivityListingFragment.selecteItem(authorInfo);
                }
            }
        }

        @Override
        public void onCategoryClicked(BookCategory category) {
            BrowsingActivity.this.OnCategoryItemClick(category);
            pagerFragment.switchTo(BOOK_CATEGORY_FRAGMENT_TYPE);

            for (BrowsingActivityListingFragment browsingActivityListingFragment : pagerTabs) {
                if (browsingActivityListingFragment.getType() == BOOK_CATEGORY_FRAGMENT_TYPE) {
                    browsingActivityListingFragment.selecteItem(category);
                }
            }
        }

        @Override
        public void notifyBookDownloadStatusUpdate(int bookId, int downloadStatus) {
            BrowsingActivity.this.notifyBookDownloadStatusUpdate(bookId, downloadStatus);
        }

        @Override
        public void notifyBookDownloadStatusUpdate() {
            notifyActivityRestarted();
        }


        @Override
        public boolean isInSelectionMode() {
            return BrowsingActivity.this.isInSelectionMode();
        }

        @Override
        public void OnBookTitleClick(int bookId, String bookTitle) {
            if (mPaneNumber <= 2) {
                pushBookInformationFragment(BookInformationFragment.newInstance(bookId));

            } else if (mPaneNumber == 3) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.book_info_container, BookInformationFragment.newInstance(bookId), BOOK_INFORMATION_FRAGMENT_TAG);
                fragmentTransaction.commit();
            }


        }


    };

    @Override
    public synchronized void registerListener(BrowsingActivityListingFragment pagerTab) {
        pagerTabs.add(pagerTab);

    }

    @Override
    public synchronized void unRegisterListener(BrowsingActivityListingFragment pagerTab) {
        pagerTabs.remove(pagerTab);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        mIsArabic = Util.isArabicUi(this);
        super.onCreate(savedInstanceState);
        inflateUi(savedInstanceState);

    }

    protected void inflateUi(Bundle savedInstanceState) {
        setContentView(R.layout.activity_browsing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        mBooksInformationDbHelper = BooksInformationDbHelper.getInstance(BrowsingActivity.this);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mDownloadOnlyBanner = (TextView) findViewById(R.id.browsing_header_banner);
        downloadedOnlySwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_item_downloaded_only).getActionView();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mShouldDisplayDownloadOnly = sharedPref.getBoolean(KEY_DOWNLOADED_ONLY, false);
        setDownloadOnlyBannerText(mShouldDisplayDownloadOnly);
        setDownloadOnlySwitchNoCallBack(mShouldDisplayDownloadOnly);

        mDownloadOnlyBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDownloadOnlyFilter(!shouldDisplayDownloadedOnly());

            }
        });

        downloadedOnlySwitch.setOnCheckedChangeListener(onDownloadSwitchCheckedChangeListener);

        //this is done to prevent motion of drawer when the user tries to slide thes switch
        downloadedOnlySwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

        View filterPagerContainer = findViewById(R.id.filter_pager_container);
        bookListContainer = findViewById(R.id.book_list_container);
        View bookInfoContainer = findViewById(R.id.book_info_container);


        if (filterPagerContainer != null && filterPagerContainer.getVisibility() == View.VISIBLE) {
            mPaneNumber = 1;
            if (bookListContainer != null && bookListContainer.getVisibility() == View.VISIBLE) {
                mPaneNumber = 2;
                if (bookInfoContainer != null && bookInfoContainer.getVisibility() == View.VISIBLE) {
                    mPaneNumber = 3;
                }
            }
        }
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.filter_pager_container, new BookFilterPagerFragment());
            fragmentTransaction.commit();

        } else {//after screen rotation
            int oldPanNumbers = savedInstanceState.getInt(NUMBER_OF_PANS_KEY);
            if (oldPanNumbers != mPaneNumber) {
                if (oldPanNumbers == 3 && mPaneNumber == 1) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment oldBookList = fragmentManager.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);
                    //First remove the fragment from its container
                    if (oldBookList != null) {
                        fragmentManager.beginTransaction().remove(oldBookList).commitNow();
                        pushBookListFragment(oldBookList);
                    }

                    Fragment oldBookInfo = fragmentManager.findFragmentByTag(BOOK_INFORMATION_FRAGMENT_TAG);
                    if (oldBookInfo != null) {
                        fragmentManager.beginTransaction().remove(oldBookInfo).commitNow();
                        pushBookInformationFragment(oldBookInfo);
                    }

                } else if (oldPanNumbers == 2 && mPaneNumber == 1) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment oldBookList = fragmentManager.findFragmentByTag(BOOK_INFORMATION_FRAGMENT_TAG);
                    if (oldBookList != null) fragmentTransaction.remove(oldBookList);
                    fragmentTransaction.commit();

                } else if (oldPanNumbers == 1 && mPaneNumber == 2 || oldPanNumbers == 1 && mPaneNumber == 3) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment oldBookInfo = fragmentManager.findFragmentByTag(BOOK_INFORMATION_FRAGMENT_TAG);
                    if (oldBookInfo != null) {
                        fragmentManager.popBackStackImmediate(BOOK_INFORMATION_FRAGMENT_ADDED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction().remove(oldBookInfo).commit();
                        fragmentManager.beginTransaction()
                                .replace(R.id.book_info_container, oldBookInfo, BOOK_INFORMATION_FRAGMENT_TAG)
                                .commit();

                    }
                    Fragment oldBookList = fragmentManager.findFragmentByTag(BOOK_LIST_FRAGMENT_TAG);
                    if (oldBookList != null) {
                        fragmentManager.popBackStackImmediate(BOOK_LIST_FRAGMENT_ADDED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction().remove(oldBookList).commit();
                        fragmentManager.beginTransaction()
                                .replace(R.id.book_list_container, oldBookList, BOOK_LIST_FRAGMENT_TAG)
                                .commit();

                    }
                }
            }

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NUMBER_OF_PANS_KEY, mPaneNumber);
    }

    protected void switchDownloadOnlyFilter(boolean showDownloadedOnly) {
        if (showDownloadedOnly) {
            if (selectedBooksIds != null && !selectedBooksIds.isEmpty()) {
                selectedBooksIds.retainAll(
                        mBooksInformationDbHelper.getBooksIdsFilteredOnDownloadStatus(
                                BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + ">=?",
                                new String[]{String.valueOf(DownloadsConstants.STATUS_FTS_INDEXING_ENDED)}
                        ));
            }

        }

        if (mActionMode != null) {
            mActionMode.getMenu().findItem(R.id.batch_download).setVisible(!showDownloadedOnly).setEnabled(!showDownloadedOnly);
        }
        mShouldDisplayDownloadOnly = showDownloadedOnly;
        setDownloadOnlyBannerText(showDownloadedOnly);
        setDownloadOnlySwitchNoCallBack(showDownloadedOnly);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_DOWNLOADED_ONLY, showDownloadedOnly);
        editor.apply();


        mShouldDisplayDownloadOnly = sharedPref.getBoolean(KEY_DOWNLOADED_ONLY, false);

        notifyDownloadedOnly(showDownloadedOnly);
    }

    protected void setDownloadOnlySwitchNoCallBack(boolean showDownloadedOnly) {
        if (downloadedOnlySwitch != null) {
            downloadedOnlySwitch.setOnCheckedChangeListener(null);
            downloadedOnlySwitch.setChecked(showDownloadedOnly);
            downloadedOnlySwitch.setOnCheckedChangeListener(onDownloadSwitchCheckedChangeListener);
        }
    }

    protected void setDownloadOnlyBannerText(boolean showDownloadedOnly) {
        mDownloadOnlyBanner.setText(showDownloadedOnly ? R.string.side_drawer_downloaded_only : R.string.action_bar_title_all_books);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_browsing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.refresh_with_file_system) {
            RefreshBooksWithDirectoryService.startActionRefreshEveryThing(this);
            Toast.makeText(this,R.string.refreshing_on_background,Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnCategoryItemClick(BookCategory bookCategory) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        int bookCategoryId = bookCategory.getId();
        if (mPaneNumber == 1) {
            BookListFragment fragment = BookListFragment.newInstance(BookListFragment.FILTERBYCATEGORY, bookCategoryId, bookCategory.getName());
            pushBookListFragment(fragment);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
            appBarLayout.setExpanded(true, false);

        } else if (mPaneNumber > 1) {
            BookListFragment fragment = BookListFragment.newInstance(BookListFragment.FILTERBYCATEGORY, bookCategoryId);
            fragmentTransaction.replace(R.id.book_list_container, fragment, BOOK_LIST_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean OnCategoryItemLongClicked(int categoryId) {
        if (mActionMode != null) {
            return false;
        }
        if (mSelectionActionModeCallBack == null) {
            mSelectionActionModeCallBack = new SelectionActionModeCallBack();
        }
        mActionMode = startSupportActionMode(mSelectionActionModeCallBack);
        addSelectedCategory(categoryId);
        return true;

    }

    @Override
    public void onCategorySelected(int categoryId, boolean checked) {
        if (checked) {
            addSelectedCategory(categoryId);
        } else {
            removeSelectedCategory(categoryId);
        }
    }

    protected void removeSelectedCategory(int categoryId) {
        selectedBooksIds.removeAll(mBooksInformationDbHelper.getBooksIdsSetByCategoryId(categoryId, shouldDisplayDownloadedOnly()));
        notifySelectionStateChanged(BOOK_CATEGORY_FRAGMENT_TYPE);
    }

    protected void addSelectedCategory(int categoryId) {
        selectedBooksIds.addAll(mBooksInformationDbHelper.getBooksIdsSetByCategoryId(categoryId, shouldDisplayDownloadedOnly()));
        notifySelectionStateChanged(BOOK_CATEGORY_FRAGMENT_TYPE);
    }

    @Override
    public boolean isCategorySelected(int categoryId) {
        return selectedBooksIds.containsAll(mBooksInformationDbHelper.getBooksIdsSetByCategoryId(categoryId, shouldDisplayDownloadedOnly()));
    }

    protected void addSelectedAuthor(int authorId) {
        selectedBooksIds.addAll(mBooksInformationDbHelper.getBooksSetAuthorId(authorId, shouldDisplayDownloadedOnly()));
        notifySelectionStateChanged(AUTHOR_LIST_FRAGMENT_TYPE);
    }

    protected void removeSelectedAuthor(int authorId) {
        selectedBooksIds.removeAll(mBooksInformationDbHelper.getBooksSetAuthorId(authorId, shouldDisplayDownloadedOnly()));
        notifySelectionStateChanged(AUTHOR_LIST_FRAGMENT_TYPE);

    }

    @Override
    public boolean isAuthorSelected(int authorId) {
        return selectedBooksIds.containsAll(mBooksInformationDbHelper.getBooksSetAuthorId(authorId, shouldDisplayDownloadedOnly()));
    }

    @Override
    public void onAuthorSelected(int authorId, boolean checked) {
        if (checked) {
            addSelectedAuthor(authorId);
        } else {
            removeSelectedAuthor(authorId);
        }
    }

    @Override
    public boolean OnAuthorItemLongClicked(int authorId) {
        if (mActionMode != null) {
            return false;
        }
        if (mSelectionActionModeCallBack == null) {
            mSelectionActionModeCallBack = new SelectionActionModeCallBack();
        }
        mActionMode = startSupportActionMode(mSelectionActionModeCallBack);
        addSelectedAuthor(authorId);
        return true;
    }

    @Override
    public boolean shouldDisplayDownloadedOnly() {
        return mShouldDisplayDownloadOnly;
    }

    @Override
    public boolean isInSelectionMode() {
        return mActionMode != null;
    }

    @Override
    public void mayBeSetTitle(String title) {
        if (mPaneNumber == 1) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (title != null && !title.isEmpty()) {
                    actionBar.setDisplayShowTitleEnabled(true);
                    actionBar.setTitle(title);
                } else {
                    actionBar.setDisplayShowTitleEnabled(false);
                }
            }
        }
    }

    private void pushBookListFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.filter_pager_container, fragment, BrowsingActivity.BOOK_LIST_FRAGMENT_TAG)
                .addToBackStack(BOOK_LIST_FRAGMENT_ADDED)
                .commit();
    }

    private void pushBookInformationFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.filter_pager_container, fragment, BOOK_INFORMATION_FRAGMENT_TAG)
                .addToBackStack(BOOK_INFORMATION_FRAGMENT_ADDED)
                .commit();
    }

    @Override
    public void OnAuthorItemItemClick(AuthorInfo authorInfo) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mPaneNumber == 1) {
            BookListFragment fragment = BookListFragment.newInstance(BookListFragment.FILTERBYAuthour,
                    authorInfo.getId(),
                    authorInfo.getName());
            pushBookListFragment(fragment);

            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBar);
            appBarLayout.setExpanded(true, false);

        } else if (mPaneNumber > 1) {
            BookListFragment fragment = BookListFragment.newInstance(BookListFragment.FILTERBYAuthour, authorInfo.getId());
            fragmentManager.beginTransaction()
                    .replace(R.id.book_list_container, fragment, BOOK_LIST_FRAGMENT_TAG)
                    .commit();
        }


    }

    @Override
    public void OnFilterAllSelected(boolean b) {
        if (mPaneNumber > 1) {
            bookListContainer.setVisibility(b ? View.GONE : View.VISIBLE);
        }
    }

    public void registerPagerFragment(BookFilterPagerFragment bookFilterPagerFragment) {
        this.pagerFragment = bookFilterPagerFragment;
    }

    protected synchronized void notifySelectionActionModeDestroyed() {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.actionModeDestroyed();
        }

    }

    protected synchronized void notifySelectionStateChanged(int type) {
        for (int tab_number = 0; tab_number < pagerTabs.size(); tab_number++) {
            BrowsingActivityListingFragment pagerTab = pagerTabs.get(tab_number);
            if (pagerTab.getType() != type)
                pagerTab.bookSelectionStatusUpdate();
        }


    }

    protected synchronized void notifySelectionActionModeSarted() {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.actionModeStarted();
        }

    }

    private void notifyActivityRestarted() {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.reAcquireCursors();
        }
    }

    private void notifyActivityStopped() {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.closeCursors();
        }
    }

    protected synchronized void notifyDownloadedOnly(boolean checked) {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.switchTodownloadedOnly(checked);
        }

    }

    public synchronized void notifyBookDownloadStatusUpdate(int bookId, int downloadStatus) {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.BookDownloadStatusUpdate(bookId, downloadStatus);
        }

    }

    public void unregisterPagerFragment() {
        pagerFragment = null;
    }

    @Override
    public void onFragmentSearchClicked() {
        Toast.makeText(this, "dialog dismissed", Toast.LENGTH_LONG).show();
    }

    protected void startSearch(String SearchQuery) {
        Intent searchIntent = new Intent(this, SearchResultActivity.class);
        searchIntent.setAction(Intent.ACTION_SEARCH);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_IS_GLOBAL_SEARCH, true);
        ArrayList<Integer> selectedSearchableBooks = new ArrayList<>();

        if (shouldDisplayDownloadedOnly()) {
            selectedSearchableBooks.addAll(selectedBooksIds);
        } else {
            HashSet<Integer> downloadedHashSet = mBooksInformationDbHelper.getBooksIdsFilteredOnDownloadStatus(
                    BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + ">=?",
                    new String[]{String.valueOf(DownloadsConstants.STATUS_FTS_INDEXING_ENDED)}
            );
            downloadedHashSet.retainAll(selectedBooksIds);

            if (downloadedHashSet.size() == 0) {
                Toast.makeText(this, R.string.no_downloaded_selected_books, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (downloadedHashSet.size() < selectedBooksIds.size()) {
                    Toast.makeText(this, R.string.searching_downloaded_only, Toast.LENGTH_SHORT).show();
                }
                selectedSearchableBooks.addAll(downloadedHashSet);
            }
        }

        bundle.putIntegerArrayList(SearchResultFragment.ARG_SEARCHABLE_BOOKS, selectedSearchableBooks);
        bundle.putString(SearchManager.QUERY, SearchQuery);
        searchIntent.putExtras(bundle);

        startActivity(searchIntent);
    }

    @Override
    public boolean onSearchRequested() {
        //https://developer.android.com/guide/topics/search/search-dialog.html#SearchContextData
        Bundle appData = new Bundle();
        appData.putBoolean(ARG_IS_GLOBAL_SEARCH, true);
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.addAll(selectedBooksIds);
        appData.putIntegerArrayList(SearchResultFragment.ARG_SEARCHABLE_BOOKS, arrayList);
        startSearch(null, false, appData, false);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.search_inside_books) {
            startActionModeFromDrawer();
            mSearchView.setIconified(false);
            // Toast.makeText(this, R.string.select_books_authors_or_cat, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_item_in_progress_downloads) {
            Intent i = new Intent(this, DownloadProgressActivity.class);
            this.startActivity(i);
        } else if (id == R.id.start_multi_book_selection) {
            startActionModeFromDrawer();
        } else if (id == R.id.nav_rate_app) {
            AboutUtil.rateApp(this);
        } else if (id == R.id.nav_share_app) {
            AboutUtil.ShareAppLink(this);
        } else if (id == R.id.nav_feedback) {
            AboutUtil.sendFeedBack(this);
        } else if (id == R.id.nav_about_app) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_item_downloaded_only) {
            SwitchCompat downloadedOnlySwitch = (SwitchCompat) item.getActionView();
            downloadedOnlySwitch.toggle();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean startActionModeFromDrawer() {
        if (mActionMode != null) {
            return true;
        }
        if (mSelectionActionModeCallBack == null) {
            mSelectionActionModeCallBack = new SelectionActionModeCallBack();
        }
        mActionMode = startSupportActionMode(mSelectionActionModeCallBack);
        notifySelectionActionModeSarted();
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        notifyActivityStopped();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        notifyActivityRestarted();
    }

    @Override
    public BookCardEventsCallback getBookCardEventCallback() {
        return bookCardEventsCallback;
    }

    public void startBatchDownload() {
        BooksDownloader booksDownloader = new BooksDownloader(BrowsingActivity.this);
        booksDownloader.downloadBookCollection(mBooksToDownload);
    }

    @Override
    public void onDialogPositiveClick() {
        startBatchDownload();
    }

    protected class SelectionActionModeCallBack implements ActionMode.Callback {

        SelectionActionModeCallBack() {

        }


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.book_selection_action_menu, menu);//Inflate the menu over action mode
            notifySelectionActionModeSarted();
            menu.findItem(R.id.batch_download).setEnabled(!shouldDisplayDownloadedOnly()).setVisible(!shouldDisplayDownloadedOnly());
            mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            mSearchView.setQueryHint(getString(R.string.hint_search_inside_books));

            //   mSearchView.setIconifiedByDefault(true);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    startSearch(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mSearchView.requestFocus();
            return true;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.batch_download) {
                mBooksToDownload.clear();
                mBooksToDownload.addAll(selectedBooksIds);
                HashSet<Integer> downloadedHashSet = mBooksInformationDbHelper.getBooksIdsFilteredOnDownloadStatus(
                        BooksInformationDBContract.StoredBooks.COLUMN_NAME_STATUS + ">?",
                        new String[]{String.valueOf(DownloadsConstants.STATUS_DOWNLOAD_STARTED)}
                );
                boolean isSelectionModified = mBooksToDownload.removeAll(downloadedHashSet);

                if (mBooksToDownload.size() != 0) {
                    if (mBooksToDownload.size() == 1) {
                        startBatchDownload();
                    } else {
                        if (isSelectionModified) {
                            Toast.makeText(BrowsingActivity.this, R.string.removed_selection_of_downloaded_books, Toast.LENGTH_LONG).show();
                        }

                        Bundle confirmBatchDownloadDialogFragmentBundle = new Bundle();
                        confirmBatchDownloadDialogFragmentBundle.putInt(KEY_NUMBER_OF_BOOKS_TO_DONLOAD, mBooksToDownload.size());
                        DialogFragment confirmBatchDownloadDialogFragment = new ConfirmBatchDownloadDialogFragment();
                        confirmBatchDownloadDialogFragment.setArguments(confirmBatchDownloadDialogFragmentBundle);
                        confirmBatchDownloadDialogFragment.show(getSupportFragmentManager(), "ConfirmBatchDownloadDialogFragment");
                        mode.finish();
                    }
                } else {
                    Toast.makeText(BrowsingActivity.this, R.string.toast_all_selected_books_already_downlaoded, Toast.LENGTH_LONG).show();
                }

            }

            return false;
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            selectedBooksIds.clear();
            notifySelectionActionModeDestroyed();
        }
    }
}

