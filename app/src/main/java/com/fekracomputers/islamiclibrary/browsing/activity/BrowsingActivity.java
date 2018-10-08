package com.fekracomputers.islamiclibrary.browsing.activity;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.browsing.dialog.ConfirmBatchDownloadDialogFragment;
import com.fekracomputers.islamiclibrary.browsing.dialog.ConfirmBookDeleteDialogFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.AuthorListFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookCategoryFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookInformationFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.browsing.fragment.LibraryFragment;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventListener;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BrowsingActivityListingFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDbHelper;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.download.downloader.BooksDownloader;
import com.fekracomputers.islamiclibrary.download.model.DownloadsConstants;
import com.fekracomputers.islamiclibrary.download.service.RefreshBooksWithDirectoryService;
import com.fekracomputers.islamiclibrary.download.view.DownloadProgressActivity;
import com.fekracomputers.islamiclibrary.homeScreen.callbacks.BookCollectionsCallBack;
import com.fekracomputers.islamiclibrary.homeScreen.controller.BookCollectionsController;
import com.fekracomputers.islamiclibrary.homeScreen.dialog.RenameCollectionDialogFragment;
import com.fekracomputers.islamiclibrary.model.AuthorInfo;
import com.fekracomputers.islamiclibrary.model.BookCategory;
import com.fekracomputers.islamiclibrary.model.BooksCollection;
import com.fekracomputers.islamiclibrary.reminder.AppRateController;
import com.fekracomputers.islamiclibrary.reminder.DonationReminderDialogFragment;
import com.fekracomputers.islamiclibrary.search.view.SearchRequestPopupFragment;
import com.fekracomputers.islamiclibrary.search.view.SearchResultActivity;
import com.fekracomputers.islamiclibrary.search.view.SearchResultFragment;
import com.fekracomputers.islamiclibrary.settings.AboutActivity;
import com.fekracomputers.islamiclibrary.settings.AboutUtil;
import com.fekracomputers.islamiclibrary.settings.HelpActivity;
import com.fekracomputers.islamiclibrary.settings.SettingsActivity;
import com.fekracomputers.islamiclibrary.userNotes.GlobalUserNotesFragment;
import com.fekracomputers.islamiclibrary.utility.Util;
import com.google.gson.Gson;
import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.fekracomputers.islamiclibrary.homeScreen.dialog.RenameCollectionDialogFragment.KEY_COLLECTION_GSON;
import static com.fekracomputers.islamiclibrary.homeScreen.dialog.RenameCollectionDialogFragment.KEY_OLD_NAME;
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
        LibraryFragment.OnBookFilterPagerPageChangedListener,
        SearchRequestPopupFragment.OnSearchPopupFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        BookCardEventListener,
        ConfirmBatchDownloadDialogFragment.BatchDownloadConfirmationListener,
        ConfirmBookDeleteDialogFragment.BookDeleteDialogListener,
        BrowsingActivityNavigationController.BrowsingActivityControllerListener,
        BookCollectionsController.BookCollectionsControllerCallback,
        RenameCollectionDialogFragment.RenameCollectionListener,
        GlobalUserNotesFragment.GlobalUserNotesFragmentListener,
        DonationReminderDialogFragment.DonationReminderDialogFragmentDelegate {

    public static final int AUTHOR_LIST_FRAGMENT_TYPE = 0;
    public static final int BOOK_CATEGORY_FRAGMENT_TYPE = 1;
    public static final int BOOK_LIST_FRAGMENT_TYPE = 2;
    public static final int BOOK_INFORMATION_TYPE = 3;
    public static final int HOME_SCREEN_TYPE = 4;

    public static final int ALL_BOOKS_TYPE = -1;

    public static final String NUMBER_OF_PANS_KEY = "NUMBER_OF_PANS_KEY";
    public static final String BOOK_LIST_FRAGMENT_TAG = "BookListFragment";
    public static final String BOOK_INFORMATION_FRAGMENT_TAG = "BookInformationFragment";
    public static final String KEY_NUMBER_OF_BOOKS_TO_DONLOAD = "KEY_NUMBER_OF_BOOKS_TO_DONLOAD";
    protected static final String TAG = "BrowsingActivity";
    protected static final String KEY_DOWNLOADED_ONLY = "shared_pref_download_only_Key";
    static final String BOOK_LIST_FRAGMENT_ADDED = "BOOK_LIST_FRAGMENT_ADDED";
    static final String BOOK_INFORMATION_FRAGMENT_ADDED = "BOOK_INFORMATION_FRAGMENT_ADDED";
    protected int mPaneNumber;
    @NonNull
    protected HashSet<Integer> selectedBooksIds = new HashSet<>();
    @Nullable
    protected BookSelectionActionModeCallback mActionMode;
    protected boolean mIsArabic;
    protected IconSwitch toolbarDownloadOnlySwitch;
    protected boolean mShouldDisplayDownloadOnly;
    protected SwitchCompat navDownloadedOnlySwitch;
    @NonNull
    protected List<BrowsingActivityListingFragment> pagerTabs = new ArrayList<>();
    protected SearchView mSearchView;
    /**
     * this variable is here to be overrided in subclasses to allow calling activity super constructors
     */
    View bookListContainer;
    @Nullable
    BooksInformationDbHelper mBooksInformationDbHelper;
    @Nullable
    private BrowsingActivityNavigationController browsingActivityNavigationController;
    @NonNull
    private HashSet<Integer> mBooksToDownload = new HashSet<>();
    private BrowsingAnalyticsController browsingAnalyticsController;
    @Nullable
    private BookCardEventsCallback bookCardEventsCallback = new BookCardEventsCallback(this) {
        @Override
        public boolean OnBookItemLongClicked(int bookId) {
            if (mActionMode != null) {
                return false;
            }

            mActionMode = new BookSelectionActionModeCallback();
            mActionMode.startBookSelectionActionMode(BrowsingActivity.this);
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
        public void showAllAuthorBooks(@NonNull AuthorInfo authorInfo) {
            BrowsingActivity.this.OnAuthorItemItemClick(authorInfo);
        }

        @Override
        public void showAllCategoryBooks(@NonNull BookCategory category) {
            BrowsingActivity.this.OnCategoryItemClick(category);
        }

        @Override
        public void showAllCollectionBooks(@NonNull BooksCollection booksCollection) {
            BookListFragment fragment = BookListFragment.newInstance(
                    BookListFragment.FILTER_BY_COLLECTION,
                    booksCollection.getCollectionsId(),
                    booksCollection.getName());
            if (browsingActivityNavigationController != null) {
                browsingActivityNavigationController.showCollectionDetails(fragment);
            }
        }

        @Override
        public void selectAllCategoryBooks(int CategoryId) {
            BrowsingActivity.this.onCategorySelected(CategoryId, true);

        }

        @Override
        public void selectAllAuthorsBooks(int authorId) {
            BrowsingActivity.this.onAuthorSelected(authorId, true);
        }

        @Override
        public void selectAllCollectionBooks(BooksCollection booksCollection) {
            if (mActionMode == null) {
                mActionMode = new BookSelectionActionModeCallback();
                mActionMode.startBookSelectionActionMode(BrowsingActivity.this);
            }
            UserDataDBHelper.GlobalUserDBHelper userDBHelper = UserDataDBHelper.getInstance(context);
            selectedBooksIds.addAll(userDBHelper.getBooksSetCollectionId(booksCollection, shouldDisplayDownloadedOnly()));
            notifySelectionStateChanged();
        }

        @Override
        protected void notifyBookDownloadFailed(int bookId, String failurReason) {
            BrowsingActivity.this.notifyBookDownloadFailed(bookId, failurReason);

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
            if (browsingActivityNavigationController != null) {
                browsingActivityNavigationController.showBookInformationFragment(BookInformationFragment.newInstance(bookId));
            }
            browsingAnalyticsController.logBookSelectionEvent(bookId);
        }


    };
    private AppBarLayout appBarLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @NonNull
    private ArrayList<BookCollectionsCallBack> bookCollectionsCallBack = new ArrayList<>();
    private AppRateController appRateController;

    private void notifyBookDownloadFailed(int bookId, String failurReason) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.browsing_coordinator_layout);
        Snackbar mySnackbar = Snackbar.make(coordinatorLayout,
                getResources().getString(R.string.book_download_failure, mBooksInformationDbHelper.getBookName(bookId)),
                Snackbar.LENGTH_LONG);
        mySnackbar.setAction(R.string.redownload,
                v -> {
                    bookCardEventsCallback.startDownloadingBook(mBooksInformationDbHelper.getBookInfo(bookId));
                    bookCardEventsCallback.notifyBookDownloadStatusUpdate(bookId, DownloadsConstants.STATUS_DOWNLOAD_REQUESTED);
                }
        );
        mySnackbar.show();
    }

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
        bookCardEventsCallback.intializeListener();

        browsingAnalyticsController = new BrowsingAnalyticsController(this);
        inflateUi(savedInstanceState);
    }


    protected void inflateUi(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_browsing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerArrowDrawable drawerArrow = new DrawerArrowDrawable(this);
        drawerArrow.setColor(0xFFFFFF);


        toolbar.setNavigationIcon(drawerArrow);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }
        mBooksInformationDbHelper = BooksInformationDbHelper.getInstance(BrowsingActivity.this);
        appBarLayout = findViewById(R.id.appBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        toolbarDownloadOnlySwitch = findViewById(R.id.toolbar_downloaded_only_switch);
        navDownloadedOnlySwitch = (SwitchCompat) navigationView.getMenu().findItem(R.id.nav_item_downloaded_only).getActionView();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mShouldDisplayDownloadOnly = sharedPref.getBoolean(KEY_DOWNLOADED_ONLY, false);
        setToolbarDownloadOnlySwitchNoCallBack(mShouldDisplayDownloadOnly);
        setDownloadOnlySwitchNoCallBack(mShouldDisplayDownloadOnly);

        toolbarDownloadOnlySwitch.setCheckedChangeListener(v -> switchDownloadOnlyFilter(!shouldDisplayDownloadedOnly()));
        navDownloadedOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> switchDownloadOnlyFilter(isChecked));

        //this is done to prevent motion of drawer when the user tries to slide thes switch
        navDownloadedOnlySwitch.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
            }
            return false;
        });

        View filterPagerContainer = findViewById(R.id.filter_pager_container);
        bookListContainer = findViewById(R.id.book_list_container);
        View bookInfoContainer = findViewById(R.id.book_info_container);
        FragmentManager fragmentManager = getSupportFragmentManager();


        mPaneNumber = getmumberOfpans(filterPagerContainer, bookInfoContainer);

        @Nullable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        int oldPanNumbers = savedInstanceState == null ? 0 : savedInstanceState.getInt(NUMBER_OF_PANS_KEY);

        browsingActivityNavigationController = BrowsingActivityNavigationController.create(
                mPaneNumber,
                oldPanNumbers,
                fragmentManager,
                savedInstanceState != null,
                this,
                bottomNavigationView,
                this);
        if (browsingActivityNavigationController != null) {
            browsingActivityNavigationController.intiializePans();
            if (bottomNavigationView != null) {
                bottomNavigationView.setOnNavigationItemSelectedListener(browsingActivityNavigationController::handleButtomNavigationItem);
            }
        }

        appRateController = new AppRateController(this);

        appRateController
                .monitor()
                .showRateDialogIfMeetsConditions(this);
    }

    public void setAppbarExpanded(boolean expanded) {
        appBarLayout.setExpanded(expanded);
    }

    @Override
    public void setUpNavigation(boolean zeroBackStack) {
        ActionBar actionBar = getSupportActionBar();
        if (zeroBackStack && actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBarDrawerToggle.setToolbarNavigationClickListener(v -> onBackPressed());
        } else {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.setToolbarNavigationClickListener(null);
        }

    }

    private int getmumberOfpans(@Nullable View filterPagerContainer, @Nullable View bookInfoContainer) {
        int paneNumber = 0;
        if (filterPagerContainer != null && filterPagerContainer.getVisibility() == View.VISIBLE) {
            paneNumber = 1;
            if (bookListContainer != null && bookListContainer.getVisibility() == View.VISIBLE) {
                paneNumber = 2;
                if (bookInfoContainer != null && bookInfoContainer.getVisibility() == View.VISIBLE) {
                    paneNumber = 3;
                }
            }
        }
        return paneNumber;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
            mActionMode.getMenu().findItem(R.id.select_all).setVisible(showDownloadedOnly).setEnabled(showDownloadedOnly);

        }
        mShouldDisplayDownloadOnly = showDownloadedOnly;
        setToolbarDownloadOnlySwitchNoCallBack(showDownloadedOnly);
        setDownloadOnlySwitchNoCallBack(showDownloadedOnly);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(KEY_DOWNLOADED_ONLY, showDownloadedOnly);
        editor.apply();


        mShouldDisplayDownloadOnly = sharedPref.getBoolean(KEY_DOWNLOADED_ONLY, false);

        notifyDownloadedOnly(showDownloadedOnly);
    }

    protected void setDownloadOnlySwitchNoCallBack(boolean showDownloadedOnly) {
        if (navDownloadedOnlySwitch != null) {
            navDownloadedOnlySwitch.setOnCheckedChangeListener(null);
            navDownloadedOnlySwitch.setChecked(showDownloadedOnly);
            navDownloadedOnlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> switchDownloadOnlyFilter(isChecked));
        }
    }

    protected void setToolbarDownloadOnlySwitchNoCallBack(boolean showDownloadedOnly) {
        if (toolbarDownloadOnlySwitch != null) {
            toolbarDownloadOnlySwitch.setCheckedChangeListener(null);
            toolbarDownloadOnlySwitch.setChecked(showDownloadedOnly ? IconSwitch.Checked.LEFT : IconSwitch.Checked.RIGHT);
            toolbarDownloadOnlySwitch.setCheckedChangeListener(v -> switchDownloadOnlyFilter(!shouldDisplayDownloadedOnly()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_browsing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
            Toast.makeText(this, R.string.refreshing_on_background, Toast.LENGTH_LONG).show();
            return true;
        } else if (id == android.R.id.home) {
            if (actionBarDrawerToggle != null && !actionBarDrawerToggle.onOptionsItemSelected(item)) {
                super.onBackPressed();

            }
            //Toast.makeText(this, mActionMode == null ? "true" : "false", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnCategoryItemClick(@NonNull BookCategory bookCategory) {
        BookListFragment fragment = BookListFragment.newInstance(
                BookListFragment.FILTERBYCATEGORY,
                bookCategory.getId(),
                bookCategory.getName());
        if (browsingActivityNavigationController != null) {
            browsingActivityNavigationController.showCategoryDetails(fragment);
        }
        browsingAnalyticsController.logCategoryEvent(bookCategory);
    }

    @Override
    public boolean OnCategoryItemLongClicked(int categoryId) {
        if (mActionMode != null) {
            return false;
        }


        mActionMode = new BookSelectionActionModeCallback();
        mActionMode.startBookSelectionActionMode(this);
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

    protected void addAllBooksToSelection(boolean downloadedOnly) {
        if (downloadedOnly) {
            selectedBooksIds.addAll(mBooksInformationDbHelper.getBookIdsDownloadedOnly());
        } else {
            selectedBooksIds.addAll(mBooksInformationDbHelper.getAllBookIds());
        }
        notifySelectionStateChanged(ALL_BOOKS_TYPE);
    }

    protected void removeAllSelectedBooks() {
        selectedBooksIds.clear();
        notifySelectionStateChanged(ALL_BOOKS_TYPE);
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
        mActionMode = new BookSelectionActionModeCallback();
        mActionMode.startBookSelectionActionMode(this);
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
    public void mayBeSetTitle(@Nullable String title) {
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


    @Override
    public void OnAuthorItemItemClick(@NonNull AuthorInfo authorInfo) {
        BookListFragment fragment = BookListFragment.newInstance(BookListFragment.FILTERBYAuthour,
                authorInfo.getId(),
                authorInfo.getName());
        if (browsingActivityNavigationController != null) {
            browsingActivityNavigationController.showAuthorFragment(fragment);
        }
        browsingAnalyticsController.logAuthorBooksList(authorInfo);
    }


    @Override
    public void OnFilterAllSelected(boolean b) {
        if (mPaneNumber > 1) {
            bookListContainer.setVisibility(b ? View.GONE : View.VISIBLE);
        }
    }

    public synchronized void registerPagerFragment(LibraryFragment libraryFragment) {
        if (browsingActivityNavigationController != null) {
            browsingActivityNavigationController.registerPagerFragment(libraryFragment);
        }
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

    private synchronized void notifySelectionStateChanged() {
        for (int tab_number = 0; tab_number < pagerTabs.size(); tab_number++) {
            BrowsingActivityListingFragment pagerTab = pagerTabs.get(tab_number);
            pagerTab.bookSelectionStatusUpdate();
        }
    }

    protected synchronized void notifySelectionActionModeSarted() {
        for (BrowsingActivityListingFragment pagerTab : pagerTabs) {
            pagerTab.actionModeStarted();
        }

    }

    @Override
    public void onCollectionRenamed(BooksCollection bookCollection, String newName) {
        for (BookCollectionsCallBack collectionsCallBack : bookCollectionsCallBack) {
            if (collectionsCallBack != null)
                collectionsCallBack.onBookCollectionRenamed(bookCollection, newName);
        }
    }


    @Override
    public synchronized void notifyBookCollectionCahnged(BooksCollection booksCollection) {
        for (BookCollectionsCallBack collectionsCallBack : bookCollectionsCallBack) {
            if (collectionsCallBack != null)
                collectionsCallBack.onBookCollectionCahnged(booksCollection);
        }
    }

    @Override
    public synchronized void registerBookCollectionCallBack(BookCollectionsCallBack bookCollectionsCallBack) {
        this.bookCollectionsCallBack.add(bookCollectionsCallBack);
    }

    @Override
    public synchronized void unRegisterBookCollectionCallBack(BookCollectionsCallBack bookCollectionsCallBack) {
        this.bookCollectionsCallBack.remove(bookCollectionsCallBack);
    }

    @Override
    public synchronized void notifyCollectionAdded(BooksCollection booksCollection) {
        for (BookCollectionsCallBack collectionsCallBack : bookCollectionsCallBack) {
            if (collectionsCallBack != null)
                collectionsCallBack.onBookCollectionAdded(booksCollection);
        }

    }

    @Override
    public synchronized void notifyCollectionRemoved(BooksCollection booksCollection) {
        for (BookCollectionsCallBack collectionsCallBack : bookCollectionsCallBack) {
            if (collectionsCallBack != null)
                collectionsCallBack.onBookCollectionRemoved(booksCollection);
        }
    }


    @Override
    public synchronized void notifyBookCollectionMoved(int collectionsId, int oldPosition, int newPosition) {
        for (BookCollectionsCallBack collectionsCallBack : bookCollectionsCallBack) {
            if (collectionsCallBack != null)
                collectionsCallBack.onBookCollectionMoved(collectionsId,
                        oldPosition,
                        newPosition);
        }
    }

    @Override
    public void showRenameDialog(@NonNull BooksCollection booksCollection) {
        Bundle confirmBatchDownloadDialogFragmentBundle = new Bundle();
        confirmBatchDownloadDialogFragmentBundle.putString(KEY_OLD_NAME, booksCollection.getName());
        Gson gson = new Gson();
        String json = gson.toJson(booksCollection);
        confirmBatchDownloadDialogFragmentBundle.putString(KEY_COLLECTION_GSON, json);
        DialogFragment renameCollectionDialogFragment = new RenameCollectionDialogFragment();
        renameCollectionDialogFragment.setArguments(confirmBatchDownloadDialogFragmentBundle);
        renameCollectionDialogFragment.show(getSupportFragmentManager(), "renameCollectionDialogFragment");

    }

    @Override
    public synchronized void notifyCollectionVisibilityChanged(BooksCollection booksCollection, boolean isVisible) {
        for (BookCollectionsCallBack collectionsCallBack : bookCollectionsCallBack) {
            if (collectionsCallBack != null)
                collectionsCallBack.onBookCollectionVisibilityChanged(booksCollection,
                        isVisible);
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

    public void unRegisterPagerFragment() {
        if (browsingActivityNavigationController != null) {
            browsingActivityNavigationController.unRegisterPagerFragment();
        }

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
            HashSet<Integer> downloadedHashSet = mBooksInformationDbHelper.getBookIdsDownloadedOnly();
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
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_pay) {
            AboutUtil.pay(this);
        } else if (id == R.id.nav_about_app) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_item_downloaded_only) {
            SwitchCompat downloadedOnlySwitch = (SwitchCompat) item.getActionView();
            downloadedOnlySwitch.toggle();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActionModeFromDrawer() {
        if (mActionMode != null) {
            return;
        }

        mActionMode = new BookSelectionActionModeCallback();
        mActionMode.startBookSelectionActionMode(this);
        notifySelectionActionModeSarted();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isInSelectionMode()) {
            mayBecloseSelectionMode();
        } else {
            super.onBackPressed();
        }
    }

    private void mayBecloseSelectionMode() {
        if (browsingActivityNavigationController != null) {
            if (browsingActivityNavigationController.shouldCloseSelectionMode()) {
                mActionMode.onDestroyActionMode();
            } else {
                super.onBackPressed();
            }
        }


    }

    @Override
    protected void onDestroy() {
        bookCardEventsCallback.removeBookDownloadBroadcastListener();
        if (browsingActivityNavigationController != null) {
            browsingActivityNavigationController.onDestroy();
        }
        notifyActivityStopped();
        super.onDestroy();
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        notifyActivityRestarted();
//    }

    @Nullable
    @Override
    public BookCardEventsCallback getBookCardEventCallback() {
        return bookCardEventsCallback;
    }

    public void startBatchDownload() {
        BooksDownloader booksDownloader = new BooksDownloader(BrowsingActivity.this);
        booksDownloader.downloadBookCollection(mBooksToDownload.toArray(new Integer[mBooksToDownload.size()]));
    }

    @Override
    public void onDialogPositiveClick() {
        startBatchDownload();
    }

    @Override
    public void onBookDeleteDialogDialogPositiveClick(int bookId) {
        bookCardEventsCallback.onBookDeleteConfirmation(bookId);
    }

    @Override
    public void showSnackBarBookNotDownloaded(int bookId) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.browsing_coordinator_layout);
        Snackbar mySnackbar = Snackbar.make(coordinatorLayout,
                getResources().getString(R.string.book_not_download, mBooksInformationDbHelper.getBookName(bookId)),
                Snackbar.LENGTH_LONG);
        mySnackbar.setAction(R.string.redownload,
                v -> {
                    bookCardEventsCallback.startDownloadingBook(mBooksInformationDbHelper.getBookInfo(bookId));
                    bookCardEventsCallback.notifyBookDownloadStatusUpdate(bookId, DownloadsConstants.STATUS_DOWNLOAD_REQUESTED);
                }
        );
        mySnackbar.show();
    }

    @Override
    public DonationReminderDialogFragment.DonationReminderDialogFragmentListener getListener() {
        return appRateController.getListener();
    }


    private class BookSelectionActionModeCallback {

        private Menu menu;
        private Toolbar selectionToolBar;

        boolean onActionItemClicked(@NonNull MenuItem item) {
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
                        onDestroyActionMode();
                    } else {
                        if (isSelectionModified) {
                            Toast.makeText(BrowsingActivity.this, R.string.removed_selection_of_downloaded_books, Toast.LENGTH_LONG).show();
                        }

                        Bundle confirmBatchDownloadDialogFragmentBundle = new Bundle();
                        confirmBatchDownloadDialogFragmentBundle.putInt(KEY_NUMBER_OF_BOOKS_TO_DONLOAD, mBooksToDownload.size());
                        DialogFragment confirmBatchDownloadDialogFragment = new ConfirmBatchDownloadDialogFragment();
                        confirmBatchDownloadDialogFragment.setArguments(confirmBatchDownloadDialogFragmentBundle);
                        confirmBatchDownloadDialogFragment.show(getSupportFragmentManager(), "ConfirmBatchDownloadDialogFragment");
                        onDestroyActionMode();
                    }
                } else {
                    Toast.makeText(BrowsingActivity.this, R.string.toast_all_selected_books_already_downlaoded, Toast.LENGTH_LONG).show();
                }

            } else if (item.getItemId() == R.id.select_all) {
                addAllBooksToSelection(shouldDisplayDownloadedOnly());
            } else if (item.getItemId() == R.id.clear_selection) {
                removeAllSelectedBooks();
            }

            return false;
        }


        void onDestroyActionMode() {
            mActionMode = null;
            selectedBooksIds.clear();
            selectionToolBar.setVisibility(View.GONE);
            notifySelectionActionModeDestroyed();
        }

        void startBookSelectionActionMode(@NonNull final BrowsingActivity browsingActivity) {
            selectionToolBar = browsingActivity.findViewById(R.id.selection_tool_bar);
            menu = selectionToolBar.getMenu();
            if (menu == null || !menu.hasVisibleItems()) {
                selectionToolBar.inflateMenu(R.menu.book_selection_action_menu);
                selectionToolBar.findViewById(R.id.up_button).setOnClickListener(v -> {
                    if (mActionMode != null) mActionMode.onDestroyActionMode();
                });

                menu = selectionToolBar.getMenu();
                selectionToolBar.setOnMenuItemClickListener(this::onActionItemClicked);
            }
            selectionToolBar.showOverflowMenu();

            browsingActivity.notifySelectionActionModeSarted();
            boolean displayDownloadOnly = browsingActivity.shouldDisplayDownloadedOnly();
            menu.findItem(R.id.batch_download)
                    .setEnabled(!displayDownloadOnly)
                    .setVisible(!displayDownloadOnly);
            menu.findItem(R.id.select_all)
                    .setEnabled(displayDownloadOnly)
                    .setVisible(displayDownloadOnly);

            browsingActivity.mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            browsingActivity.mSearchView.setQueryHint(browsingActivity.getString(R.string.hint_search_inside_books));

            selectionToolBar.setVisibility(View.VISIBLE);
            //   mSearchView.setIconifiedByDefault(true);
            browsingActivity.mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    browsingActivity.startSearch(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

        }

        public Menu getMenu() {
            return menu;
        }
    }


}

