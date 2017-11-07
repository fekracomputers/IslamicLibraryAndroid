package com.fekracomputers.islamiclibrary.reading;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseContract;
import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.model.PageInfo;
import com.fekracomputers.islamiclibrary.model.PartInfo;
import com.fekracomputers.islamiclibrary.model.Title;
import com.fekracomputers.islamiclibrary.reading.dialogs.PageNumberPickerDialogFragment;
import com.fekracomputers.islamiclibrary.reading.fragments.BookPageFragment;
import com.fekracomputers.islamiclibrary.reading.widget.SearchScrubBar;
import com.fekracomputers.islamiclibrary.search.model.BookSearchResultsContainer;
import com.fekracomputers.islamiclibrary.search.model.SearchResult;
import com.fekracomputers.islamiclibrary.search.view.SearchResultFragment;
import com.fekracomputers.islamiclibrary.settings.SettingsActivity;
import com.fekracomputers.islamiclibrary.settings.SettingsFragment;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsBookmarksActivity;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;
import com.fekracomputers.islamiclibrary.utility.AppConstants;
import com.fekracomputers.islamiclibrary.utility.Util;
import com.fekracomputers.islamiclibrary.widget.KeyboardAwareEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.fekracomputers.islamiclibrary.R.id.chapter_title;
import static com.fekracomputers.islamiclibrary.search.view.SearchResultFragment.ARG_IS_GLOBAL_SEARCH;

public class ReadingActivity extends AppCompatActivity implements
        BookPageFragment.PageFragmentListener,
        DisplayOptionsPopupFragment.OnPrefDialogInteractionListener,
        SearchResultFragment.OnSearchResultFragmentInteractionListener,
        PageNumberPickerDialogFragment.PageNumberPickerDialogFragmentListener {

    public static final String KEY_TAB_NAME = "Tab_Name";
    public static final String KEY_SEARCH_RESULT_ARRAY_LIST = "KEY_SEARCH_RESULT_ARRAY_LIST";
    public static final String KEY_SEARCH_RESULT_CHILD_POSITION = "KEY_SEARCH_RESULT_CHILD_POSITION";
    public static final String KEY_CURRENT_PAGE_INFO = "KEY_CURRENT_PAGE_INFO";
    public static final String KEY_CURRENT_PARTS_INFO = "KEY_CURRENT_PARTS_INFO";
    public static final String KEY_BOOK_ID = "KEY_BOOK_ID";
    private static final int PICK_TITLE_REQUEST = 1;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    //  private GestureDetectorCompat mDetector;
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String SEARCH_FRAGMENT_TAG = "search Fragment";
    private static final String SHOW_SEARCH_FRAGMENT_BACKSTACK_ENTRY = "Show_Search_fragment";
    private static final String ADD_SEARCH_FRAGMENT_BACK_STACK_ENTRY = "ADD_SEARCH_FRAGMENT_BACK_STACK_ENTRY";
    private static final int FLOATING_PAGE_NUMBER_DELAY_MILLIS = 5000;
    private static final int FADE_ANIMATION_DURATION = 500;
    private final String TAG = this.getClass().getSimpleName();
    private final Handler mHideHandler = new Handler();

    private final Handler mFloatingPageNumberHandler = new Handler();
    private final List<DisplayPrefChangeListener> displayPrefChangeListeners = new ArrayList<>();
    private final List<ActionModeChangeListener> mActionModeChangeListener = new ArrayList<>();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS);
        }
        return false;
    };
    boolean mTurnPageByVolumeUpKey = true;
    boolean mTurnPageByVolumeDownKey = true;
    private boolean is_nav_view_inflated = false;
    private boolean isSearchViewInflated = false;
    private ViewStub mNavViewStub;
    private String bookName;
    // private View mCategoryTitleView;
    private ViewPager mPager;
    /**
     * Delayed removal of status and navigation bar
     */
    private final Runnable mHidePart2Runnable = new Runnable() {

        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private SeekBar seekBar;
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

            animateFadeIn(mControlsView, FADE_ANIMATION_DURATION);

        }
    };
    private int bookId;
    private ActionMode mActionMode;
    private PageInfo currentPageInfo;
    private BookDatabaseHelper mBookDatabaseHelper;
    private boolean mIsArabic;
    private ViewStub mSearchViewStub;
    private SearchScrubBar searchScrubBar;
    private int mCurrentSearchResultPosition;
    private ArrayList<SearchResult> mBookSearchResultsArrayList;
    private final ViewPager.OnPageChangeListener searchScrubOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setupSearchScrubOnSearchResultClicked(position);
        }


        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private boolean mHighlightClickedFlag;
    private FrameLayout mFloatingPageNumberFrameLayout;
    private final Runnable mHideFloatingPageNumberRunnable =
            new Runnable() {
                @Override
                public void run() {
                    animateFadeOutHide(mFloatingPageNumberFrameLayout, FADE_ANIMATION_DURATION);
                }
            };
    private TextView mFloatingPageNumberTextView;
    private BookPartsInfo mPartsInfo;
    private int PAGE_COUNT;
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    private Title parentTitle;
    private UserDataDBHelper mUserDataDBHelper;
    private boolean isThemeNightMode;
    private boolean mIsInSearchMode = false;
    private SearchView mSearchView;
    private SharedPreferences defaultSharedPreferences;
    private View.OnClickListener mShowPageNumberPickerDialogClickListener = v -> showPageNumberPickerDialog();

    private void showPageNumberPickerDialog() {
        Bundle PageNumberPickerDialogFragmentBundle = new Bundle();
        PageNumberPickerDialogFragmentBundle.putParcelable(KEY_CURRENT_PAGE_INFO, currentPageInfo);
        PageNumberPickerDialogFragmentBundle.putParcelable(KEY_CURRENT_PARTS_INFO, mPartsInfo);
        PageNumberPickerDialogFragmentBundle.putInt(KEY_BOOK_ID, bookId);
        PageNumberPickerDialogFragment pageNumberPickerDialogFragment = new PageNumberPickerDialogFragment();
        pageNumberPickerDialogFragment.setArguments(PageNumberPickerDialogFragmentBundle);
        pageNumberPickerDialogFragment.show(getSupportFragmentManager(), "PageNumberPickerDialogFragment");
    }

    public synchronized void registerBottomToolBarActionListener(DisplayPrefChangeListener listener) {
        displayPrefChangeListeners.add(listener);
    }

    public synchronized void unregisterBottomToolBarActionListener(DisplayPrefChangeListener listener) {
        displayPrefChangeListeners.remove(listener);
    }

    public synchronized int zoomUpdatedByPercent(int percent) {
        int oldZoom = getDisplayZoom();
        int newZoom = oldZoom + percent;
        if (newZoom <= AppConstants.DISPLAY_PREFERENCES_DEFAULTS.MAX_TEXT_ZOOM &&
                newZoom >= AppConstants.DISPLAY_PREFERENCES_DEFAULTS.MIN_TEXT_ZOOM) {
            DisplayPreferenceUtilities.setDisplayPreference(SettingsFragment.KEY_DISPLAY_TEXT_SIZE, newZoom, defaultSharedPreferences, mUserDataDBHelper);

            for (DisplayPrefChangeListener listener : displayPrefChangeListeners) {
                listener.setZoom(newZoom);
            }
        }
        return newZoom;
    }

    public int getDisplayZoom() {
        return DisplayPreferenceUtilities.getDisplayPreference(SettingsFragment.KEY_DISPLAY_TEXT_SIZE, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.DEFAULT_TEXT_ZOOM, defaultSharedPreferences, mUserDataDBHelper);
    }

    @Override
    public boolean getTashkeelState() {
        return isTashkeel();
    }

    private synchronized void zoomUpdatedByValue(int newZoom) {
        for (DisplayPrefChangeListener listener : displayPrefChangeListeners) {
            listener.setZoom(newZoom);
        }
    }

    @Override
    public void onZoomChangedByPinch(int value) {
        DisplayPreferenceUtilities.setDisplayPreference(SettingsFragment.KEY_DISPLAY_TEXT_SIZE,
                value, defaultSharedPreferences
                , mUserDataDBHelper);
        zoomUpdatedByValue(value);
    }

    @Override
    public boolean isThemeNightMode() {
        return DisplayPreferenceUtilities.getDisplayPreference(SettingsFragment.KEY_IS_THEME_NIGHT_MODE,
                AppConstants.DISPLAY_PREFERENCES_DEFAULTS.IS_THEME_NIGHT_MODE,
                defaultSharedPreferences,

                mUserDataDBHelper);
    }

    @Override
    public void setThemeNightMode(boolean isDesiredThemeLight) {
        DisplayPreferenceUtilities.setDisplayPreference(SettingsFragment.KEY_IS_THEME_NIGHT_MODE,
                isDesiredThemeLight,
                defaultSharedPreferences, mUserDataDBHelper);
        restartOnThemeChange();

    }

    @Override
    public boolean isTashkeel() {
        return DisplayPreferenceUtilities.getDisplayPreference(SettingsFragment.KEY_IS_TASHKEEL_ON,
                AppConstants.DISPLAY_PREFERENCES_DEFAULTS.KEY_IS_TASHKEEL_ON,
                defaultSharedPreferences,
                mUserDataDBHelper);
    }

    @Override
    public synchronized void setTashkeel(boolean checked) {
        DisplayPreferenceUtilities.setDisplayPreference(SettingsFragment.KEY_IS_TASHKEEL_ON,
                checked,
                defaultSharedPreferences, mUserDataDBHelper);
        for (DisplayPrefChangeListener listener : displayPrefChangeListeners) {
            listener.setTashkeel(checked);
        }
    }


    private void restartOnThemeChange() {
        finish();
        Intent intent = getIntent();
        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public synchronized void registerActionModeChangeListener(ActionModeChangeListener listener) {
        mActionModeChangeListener.add(listener);
    }

    public synchronized void unregisterActionModeChangeListener(ActionModeChangeListener listener) {
        mActionModeChangeListener.remove(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reading_activity_action, menu);

        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint(getString(R.string.search_inside_volume_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startLocalSearch(s);
                mSearchView.clearFocus();
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
    public void onBackPressed() {
        if (mIsInSearchMode) {
            reShowSearchResultFragment();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        if (id == R.id.action_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_reader_settings) {
            DisplayOptionsPopupFragment displayOptionsPopupFragment = DisplayOptionsPopupFragment.newInstance(DisplayOptionsPopupFragment.LAYOUT_OPTIONS, getDisplayZoom());
            FragmentManager fm = getSupportFragmentManager();
            displayOptionsPopupFragment.show(fm, DisplayOptionsPopupFragment.TAG_FRAGMENT_DISPLAY_OPTIONS);
            toggle();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            switch (id) {
                case R.id.action_info:
                    intent = new Intent(this, TableOfContentsBookmarksActivity.class);
                    intent.putExtra(KEY_TAB_NAME, TableOfContentsBookmarksActivity.TableOfContentAndNotesTab.TAB_OVERVIEW.ordinal());
                    break;
                case R.id.action_toc:
                    intent = new Intent(this, TableOfContentsBookmarksActivity.class);
                    intent.putExtra(KEY_TAB_NAME, TableOfContentsBookmarksActivity.TableOfContentAndNotesTab.TAB_TABLE_OF_CONTENTS.ordinal());
                    break;
                case R.id.action_bookmark:
                    intent = new Intent(this, TableOfContentsBookmarksActivity.class);
                    intent.putExtra(KEY_TAB_NAME, TableOfContentsBookmarksActivity.TableOfContentAndNotesTab.TAB_BOOKMARKS.ordinal());

                    break;

                default:
                    return super.onOptionsItemSelected(item);
            }
            intent.putExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, bookId);
            intent.putExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE, bookName);
            startActivityForResult(intent, PICK_TITLE_REQUEST);
            return true;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_TITLE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mPager.setCurrentItem(mBookDatabaseHelper.pageId2position(data.getIntExtra(BookDatabaseContract.PageEntry.COLUMN_NAME_PAGE_ID, 0)), false);
            }
        }
    }

    @Override
    public void onSearchResultClicked(BookSearchResultsContainer bookSearchResultsContainer, int childAdapterPosition) {
        //hide the search result fragment without removing it
        getSupportFragmentManager()
                .popBackStack();

        mBookSearchResultsArrayList = bookSearchResultsContainer.getChildArrayList();
        mCurrentSearchResultPosition = childAdapterPosition;
        if (!isSearchViewInflated) {
            mSearchViewStub.inflate();
            isSearchViewInflated = true;
        }
        mPager.addOnPageChangeListener(searchScrubOnPageChangeListener);
        mIsInSearchMode = true;
        searchScrubBar.setVisibility(View.VISIBLE);
        moveToCurrentMatch();
    }

    private void setupSearchScrubOnSearchResultClicked(int position) {
        int pageId = mBookDatabaseHelper.position2PageId(position);

        int i = Collections.binarySearch(mBookSearchResultsArrayList, new SearchResult(bookId, new PageInfo(pageId, 0, 0)));
        boolean isCurrentPageMatch = i >= 0;
        int numberOfMatchesBeforCurrent = isCurrentPageMatch ? i : -i - 1;

        searchScrubBar.setSearchBarMatchText(numberOfMatchesBeforCurrent,
                mBookSearchResultsArrayList.size(),
                isCurrentPageMatch
        );
        searchScrubBar.setPreviousButtonEnabled(numberOfMatchesBeforCurrent != 0);
        searchScrubBar.setNextButtonEnabled(numberOfMatchesBeforCurrent < mBookSearchResultsArrayList.size());
    }

    /**
     * move the pager to the next search match`
     */
    private void moveToNextMatch() {
        if (mCurrentSearchResultPosition < mBookSearchResultsArrayList.size() - 1) {
            mCurrentSearchResultPosition++;
            moveToSearchMatch(mCurrentSearchResultPosition);
        }
    }

    /**
     * move the pager to the previous search match
     */
    private void moveToPreviousMatch() {
        if (mCurrentSearchResultPosition > 0) {
            mCurrentSearchResultPosition--;
            moveToSearchMatch(mCurrentSearchResultPosition);
        }
    }

    /**
     * move the pager to the current search match ; call this after setting {@link #mCurrentSearchResultPosition}
     */
    private void moveToCurrentMatch() {
        moveToSearchMatch(mCurrentSearchResultPosition);
    }

    private void moveToSearchMatch(int matchNumber) {
        if (mBookSearchResultsArrayList != null &&
                matchNumber >= 0 &&
                matchNumber < mBookSearchResultsArrayList.size() &&
                !mBookSearchResultsArrayList.isEmpty()) {
            int targetPosition = mBookDatabaseHelper.pageId2position(mBookSearchResultsArrayList.get(matchNumber).getPageInfo().pageId);
            if (targetPosition != mPager.getCurrentItem()) {
                mPager.setCurrentItem(targetPosition,
                        true);
            } else //the current match is the same as current page
            {
                setupSearchScrubOnSearchResultClicked(targetPosition);
            }
        }
    }

    private void setNumberEditorValid(boolean valid, EditText editText, CharSequence errorString) {
        if (valid) {
            editText.setError(null);
            editText.setTextColor(Util.getThemeColor(this, R.attr.blueThemedText));
        } else {
            editText.setError(errorString, null);
            editText.setTextColor(Util.getThemeColor(this, R.attr.skimPageError));

        }
    }

    private void switchEditTextToTextView(EditText editText, TextView textView) {
        editText.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        mPager.requestFocus();
    }


    @Override
    public void setBookmarkState(boolean Checked) {
        if (is_nav_view_inflated) {
            ImageButton bookmarkImageButton = mControlsView.findViewById(R.id.action_bookmark_this_page);
            if (bookmarkImageButton.isSelected() != Checked)
                bookmarkImageButton.setSelected(Checked);
        }

    }

    @Override
    public boolean isNightMode() {
        return isThemeNightMode;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean navigate = defaultSharedPreferences.
                getBoolean(SettingsFragment.PREF_USE_VOLUME_KEY_NAV, false);
        if (navigate && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mTurnPageByVolumeDownKey) {
                if (isSearchViewInflated && searchScrubBar.getVisibility() == View.VISIBLE) {
                    moveToNextMatch();
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                }
                showFloatingPageNumber();
                mTurnPageByVolumeDownKey = false;
            }
            return true;
        } else if (navigate && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mTurnPageByVolumeUpKey) {
                if (isSearchViewInflated && searchScrubBar.getVisibility() == View.VISIBLE) {
                    moveToPreviousMatch();
                } else {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
                }
                showFloatingPageNumber();
                mTurnPageByVolumeUpKey = false;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        boolean navigate = defaultSharedPreferences.
                getBoolean(SettingsFragment.PREF_USE_VOLUME_KEY_NAV, false);
        boolean b = false;
        if (navigate && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mTurnPageByVolumeDownKey = true;
            b = true;
        } else if (navigate && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mTurnPageByVolumeUpKey = true;
            b = true;
        }
        return b || super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean newIsNightMode = DisplayPreferenceUtilities.getDisplayPreference(SettingsFragment.KEY_IS_THEME_NIGHT_MODE, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.IS_THEME_NIGHT_MODE, defaultSharedPreferences, mUserDataDBHelper);
        if (newIsNightMode != isThemeNightMode)
            restartOnThemeChange();
        int newZoom = getDisplayZoom();
        zoomUpdatedByValue(newZoom);
        Util.restartIfLocaleChanged(this, mIsArabic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        mIsArabic = Util.isArabicUi(this);
        Intent intent = getIntent();
        bookId = intent.getIntExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, 0);
        mUserDataDBHelper = UserDataDBHelper.getInstance(this, bookId);
        mUserDataDBHelper.logBookAccess();

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        isThemeNightMode = DisplayPreferenceUtilities.getDisplayPreference(SettingsFragment.KEY_IS_THEME_NIGHT_MODE, AppConstants.DISPLAY_PREFERENCES_DEFAULTS.IS_THEME_NIGHT_MODE, defaultSharedPreferences, mUserDataDBHelper);
        setTheme(isThemeNightMode ? R.style.ReadingActivityNight : R.style.ReadingActivityDay);

        setContentView(R.layout.activity_reading);
        mNavViewStub = findViewById(R.id.book_nav_view_stub);
        mNavViewStub.setOnInflateListener(new navViewOnInflateListener());

        mSearchViewStub = findViewById(R.id.search_scrub_stub);
        mSearchViewStub.setOnInflateListener(new SearchScrubOnInflateListener());

        mFloatingPageNumberFrameLayout = findViewById(R.id.floating_page_number_frame);
        mFloatingPageNumberTextView = mFloatingPageNumberFrameLayout.
                findViewById(R.id.floating_page_number_text_view);
        mFloatingPageNumberTextView.setOnLongClickListener(v -> {
            showNavView();
            mHideHandler.post(mHideFloatingPageNumberRunnable);
            final KeyboardAwareEditText pageNumberEditor = mControlsView.findViewById(R.id.page_number_editor);
            final TextView pageNumberTextView = mControlsView.findViewById(R.id.part_page_number_tv);
            showEditingPageNumberInPlace(pageNumberTextView, pageNumberEditor);
            return true;
        });
        mFloatingPageNumberTextView.setOnClickListener(mShowPageNumberPickerDialogClickListener);
        bookName = intent.getStringExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(bookName);
        }
        mPager = findViewById(R.id.pager);
        mBookDatabaseHelper = BookDatabaseHelper.getInstance(this, bookId);
        mPartsInfo = mBookDatabaseHelper.getBookPartsInfo();
        PAGE_COUNT = mBookDatabaseHelper.getPageCount();
        PagerAdapter pagerAdapter = new BookPageFragmentStatePagerAdapter(getSupportFragmentManager());

        currentPageInfo = mUserDataDBHelper.getLastPageInfo();
        parentTitle = mBookDatabaseHelper.getParentTitle(currentPageInfo.pageId);
        mFloatingPageNumberTextView.setText(
                getPartPageSingleText());


        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(mBookDatabaseHelper.pageId2position(currentPageInfo.pageId));

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPageInfo = mBookDatabaseHelper.getPageInfoByPagePosition(position);
                parentTitle = mBookDatabaseHelper.getParentTitle(currentPageInfo.pageId);
                mFloatingPageNumberTextView.setText(
                        getPartPageSingleText());
                mUserDataDBHelper.logPageAccess(currentPageInfo);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    showFloatingPageNumber();
                }
            }
        });

        mVisible = true;//This will bw shortly changed

        if (intent.hasExtra(KEY_SEARCH_RESULT_ARRAY_LIST) && intent.hasExtra(KEY_SEARCH_RESULT_CHILD_POSITION)) {
            int searchResultListPosition = intent.getIntExtra(ReadingActivity.KEY_SEARCH_RESULT_CHILD_POSITION, 0);
            mBookSearchResultsArrayList = intent.getParcelableArrayListExtra(ReadingActivity.KEY_SEARCH_RESULT_ARRAY_LIST);
            mCurrentSearchResultPosition = searchResultListPosition;
            if (!isSearchViewInflated) {
                mSearchViewStub.inflate();
                isSearchViewInflated = true;
            }
            moveToCurrentMatch();

        }
        getDelegate().setHandleNativeActionModesEnabled(false);


        //mPager.setOffscreenPageLimit(3);

//mPager.setPageTransformer(true, new DepthPageTransformer());

    }

    @NonNull
    private String getPartPageSingleText() {
        return TableOfContentsUtils.formatPageAndPartNumber(
                mPartsInfo,
                currentPageInfo,
                R.string.page_slash_part,
                R.string.page_number,
                getResources()
        );
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        showFloatingPageNumber();
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            showNavView();
        }
    }

    private void animateFadeIn(final View view, int duration) {
        view.animate().
                alpha(1.0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * shows a floating circle with page number and part number then schedules hiding
     */
    private void showFloatingPageNumber() {
        mFloatingPageNumberHandler.removeCallbacks(mHideFloatingPageNumberRunnable);
        //show the page number
        if (mFloatingPageNumberFrameLayout.getVisibility() != View.VISIBLE)
            animateFadeIn(mFloatingPageNumberFrameLayout, FADE_ANIMATION_DURATION);
        //schedule hiding
        mFloatingPageNumberHandler.postDelayed(mHideFloatingPageNumberRunnable, FLOATING_PAGE_NUMBER_DELAY_MILLIS);
    }

    private void animateFadeOutHide(final View view, int duration) {
        view.animate()
                .alpha(0.0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                });
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (mControlsView != null) {
            animateFadeOutHide(mControlsView, FADE_ANIMATION_DURATION);
        }
        if (searchScrubBar != null) {
            animateFadeOutHide(searchScrubBar, FADE_ANIMATION_DURATION);
        }

        if (mFloatingPageNumberFrameLayout != null) {
            animateFadeOutHide(mFloatingPageNumberFrameLayout, FADE_ANIMATION_DURATION);
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

    }

    private void showNavView() {
        // Show the system bar
        mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        if (!is_nav_view_inflated) {
            mControlsView = mNavViewStub.inflate();
            is_nav_view_inflated = true;
        }
        if (mIsInSearchMode && searchScrubBar != null) {
            animateFadeIn(searchScrubBar, FADE_ANIMATION_DURATION);
        }

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);

    }

    @Override
    public void startSelectionActionMode() {
        if (mActionMode != null) {
            Menu menu = mActionMode.getMenu();
            menu.findItem(R.id.action_add_comment).setVisible(true);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mHighlightClickedFlag = true;
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
            } else {
                startActionMode(new ActionMode.Callback2() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mHighlightClickedFlag = true;
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {


                    }

                    @Override
                    public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
                        super.onGetContentRect(mode, view, outRect);
                    }
                }, ActionMode.TYPE_FLOATING);
            }
        }

    }


    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (mActionMode == null) {
            mActionMode = mode;
            Menu menu = mode.getMenu();

            menu.clear();

            if (!shouldDisplayFloatingSelectionMenu()) {
                MenuInflater inflater = new MenuInflater(this);

                inflater.inflate(R.menu.text_selection_context_menu, menu);
                mode.setTitle(null);

                menu.findItem(R.id.action_copy_text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.action_select_all_text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.action_add_comment).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.action_share_text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.action_add_highlight).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

                if (mHighlightClickedFlag) {
                    menu.findItem(R.id.action_add_comment).setVisible(true);
                    menu.findItem(R.id.highlight_remove).setVisible(true);
                }
            }
        }

        super.onActionModeStarted(mode);
        for (ActionModeChangeListener actionModeChangeListener : mActionModeChangeListener) {
            actionModeChangeListener.actionModeStarted();
        }
    }

    private void notifyBookmarkStateChanged(boolean newState) {
        for (ActionModeChangeListener actionModeChangeListener : mActionModeChangeListener) {
            actionModeChangeListener.onBookmarkStateChange(newState, currentPageInfo.pageId);
        }
    }


    private boolean shouldDisplayFloatingSelectionMenu() {
        //return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        return false;
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        mActionMode = null;
        mHighlightClickedFlag = false;
        super.onActionModeFinished(mode);
        for (ActionModeChangeListener actionModeChangeListener : mActionModeChangeListener) {
            actionModeChangeListener.actionModeFinished();
        }

    }

    public void onContextualMenuItemClicked(MenuItem item) {

        for (ActionModeChangeListener actionModeChangeListener : mActionModeChangeListener) {
            actionModeChangeListener.onContextualMenuItemClicked(item.getItemId(), mPager.getCurrentItem());
        }
    }

    @Override
    public void onPageTapped() {
        toggle();
    }

    @Override
    public void finishActionMode() {
        if (mActionMode != null) mActionMode.finish();
    }


    private void startLocalSearch(String SearchQuery) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_IS_GLOBAL_SEARCH, false);
        ArrayList<Integer> bookIds = new ArrayList<>();
        bookIds.add(bookId);
        bundle.putIntegerArrayList(SearchResultFragment.ARG_SEARCHABLE_BOOKS, bookIds);
        bundle.putString(SearchManager.QUERY, SearchQuery);
        Fragment searchResultFragment = SearchResultFragment.newInstance(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.search_result_fragment_containerr, searchResultFragment, SEARCH_FRAGMENT_TAG)
                .hide(searchResultFragment)
                .addToBackStack(ADD_SEARCH_FRAGMENT_BACK_STACK_ENTRY)
                .commit()
        ;

        getSupportFragmentManager()
                .beginTransaction()
                .show(searchResultFragment)
                .addToBackStack(SHOW_SEARCH_FRAGMENT_BACKSTACK_ENTRY)
                .commit();

    }

    private boolean isInGlobalSearchResult() {
        return getIntent().hasExtra(KEY_SEARCH_RESULT_ARRAY_LIST);
    }

    private void reShowSearchResultFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .show(getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG))
                .addToBackStack(SHOW_SEARCH_FRAGMENT_BACKSTACK_ENTRY)
                .commit();
        searchScrubBar.setVisibility(View.GONE);
        mIsInSearchMode = false;//why?
    }

    private void exitSearchMode() {
        clearSearchMatches();

        mPager.removeOnPageChangeListener(searchScrubOnPageChangeListener);
        mSearchView.setQuery("", false);
        mSearchView.setIconified(true);
        searchScrubBar.setVisibility(View.GONE);
        mIsInSearchMode = false;

    }

    private void clearSearchMatches() {
        mCurrentSearchResultPosition = 0;
        if (mBookSearchResultsArrayList != null) {
            mBookSearchResultsArrayList.clear();
        }
    }

    private void removeSearchResultFragment() {
        getSupportFragmentManager()
                .popBackStack(ADD_SEARCH_FRAGMENT_BACK_STACK_ENTRY,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onPageNumberDialogPositiveClick(int pageNumber, int partNumber) {
        mPager.setCurrentItem((
                mBookDatabaseHelper.pageId2position(
                        mBookDatabaseHelper.getPageId(partNumber,
                                pageNumber))));
    }

    private void showEditingPartNumberInPlace(TextView partNumberTextView, KeyboardAwareEditText partNumberEditor) {
        partNumberTextView.setVisibility(View.INVISIBLE);
        partNumberEditor.setHint(partNumberTextView.getText());
        partNumberEditor.setText("");
        partNumberEditor.setVisibility(View.VISIBLE);
        setNumberEditorValid(true, partNumberEditor, null);
        partNumberEditor.requestFocus();
    }

    private void showEditingPageNumberInPlace(TextView pageNumberTextView, KeyboardAwareEditText pageNumberEditor) {
        pageNumberTextView.setVisibility(View.INVISIBLE);
        pageNumberEditor.setText("");
        pageNumberEditor.setHint(pageNumberTextView.getText());
        setNumberEditorValid(true, pageNumberEditor, null);
        pageNumberEditor.setVisibility(View.VISIBLE);
        pageNumberEditor.requestFocus();
    }

    @NonNull
    private String getPageNumberStringFromCurrentPage() {
        return currentPageInfo.pageNumber == 0 && currentPageInfo.partNumber == 0 ?
                getString(R.string.zero_zero_page_placeholder_single_part) :
                getString(R.string.page_number, currentPageInfo.pageNumber);
    }

    @NonNull
    private String getPartNumberString() {
        return currentPageInfo.pageNumber == 0 && currentPageInfo.partNumber == 0 ?
                getString(R.string.zero_zero_page_placeholder_single_part) :
                getString(R.string.page_number, currentPageInfo.partNumber);

    }

    private class SearchScrubOnInflateListener implements ViewStub.OnInflateListener {

        @Override
        public void onInflate(ViewStub stub, View inflated) {
            searchScrubBar = (SearchScrubBar) inflated;
            searchScrubBar.setupPagingDirection(mIsArabic);
            searchScrubBar.setOnClickListener(view -> {
                if (view == searchScrubBar.getPreviousButton()) {
                    ReadingActivity.this.moveToPreviousMatch();
                } else if (view == searchScrubBar.getNextButton()) {
                    ReadingActivity.this.moveToNextMatch();
                }
            });
            searchScrubBar.setExitSearchListener(v -> {
                if (!isInGlobalSearchResult()) {
                    removeSearchResultFragment();
                }
                exitSearchMode();
            });
            searchScrubBar.setMatchDescriptionOnClickListener(v -> {

                if (!isInGlobalSearchResult()) {
                    reShowSearchResultFragment();

                } else {
                    //TODO May be change the scenario to remove the Search activity from the application backStack
                    finish();
                }

            });

        }


    }

    private class navViewOnInflateListener implements ViewStub.OnInflateListener {


        @Override
        public void onInflate(ViewStub stub, View inflated) {

            final TextView pageNumberTextView = inflated.findViewById(R.id.part_page_number_tv);
            final TextView partNumberTextView = inflated.findViewById(R.id.part_number);
            final KeyboardAwareEditText pageNumberEditor = inflated.findViewById(R.id.page_number_editor);
            final KeyboardAwareEditText partNumberEditor = inflated.findViewById(R.id.part_number_editor);
            final TextView chapterTitleTextView = inflated.findViewById(chapter_title);
            final ImageButton bookmarkImageButton = inflated.findViewById(R.id.action_bookmark_this_page);
            bookmarkImageButton.setVisibility(View.VISIBLE);
            bookmarkImageButton.setSelected(mUserDataDBHelper.isPageBookmarked(currentPageInfo.pageId));

            bookmarkImageButton.setOnClickListener(v -> {
                boolean newBookmarkState = !bookmarkImageButton.isSelected();
                bookmarkImageButton.setSelected(newBookmarkState);
                if (bookmarkImageButton.isSelected()) {
                    mUserDataDBHelper.addBookmark(currentPageInfo.pageId);
                } else {
                    mUserDataDBHelper.RemoveBookmark(currentPageInfo.pageId);
                }
                notifyBookmarkStateChanged(newBookmarkState);
            });


            chapterTitleTextView.setText(mBookDatabaseHelper.getParentTitle(currentPageInfo.pageId).title);

            if (!mPartsInfo.isMultiPart()) {
                //The book has only one part
                partNumberTextView.setVisibility(View.GONE);
                partNumberEditor.setVisibility(View.GONE);
                TextView slash = findViewById(R.id.page_slash);
                slash.setVisibility(View.GONE);
            } else {
                partNumberTextView.setOnClickListener(mShowPageNumberPickerDialogClickListener);
                partNumberTextView.setOnLongClickListener(v -> {
                    showEditingPartNumberInPlace(partNumberTextView, partNumberEditor);
                    return true;
                });

                partNumberEditor.setKeyboardListener(editText -> switchEditTextToTextView(partNumberEditor, partNumberTextView));


                int maxPartLength = String.valueOf(mPartsInfo.lastPart).length();
                char[] maxPart = new char[Math.min(maxPartLength, 6)];
                Arrays.fill(maxPart, '9');
                partNumberTextView.setText(maxPart, 0, maxPart.length);
                partNumberTextView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                ViewGroup.LayoutParams params = partNumberTextView.getLayoutParams();
                int currentPageWidth = partNumberTextView.getMeasuredWidth();
                params.width = currentPageWidth;
                params.height = partNumberTextView.getMeasuredHeight();
                partNumberTextView.setLayoutParams(params);
                partNumberEditor.setText(String.valueOf(currentPageInfo.pageNumber));
                partNumberEditor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxPartLength)});
                boolean numIsRtl = true;
                params = partNumberEditor.getLayoutParams();
                params.width = (currentPageWidth - (partNumberTextView.getPaddingLeft() + partNumberTextView.getPaddingRight())) + (partNumberEditor.getPaddingLeft() + partNumberEditor.getPaddingRight());
                partNumberEditor.setLayoutParams(params);
                partNumberEditor.setTranslationX((float) (partNumberEditor.getPaddingRight() - partNumberTextView.getPaddingRight()));
                if (ViewCompat.getLayoutDirection(partNumberEditor) != View.LAYOUT_DIRECTION_RTL) {
                    numIsRtl = false;
                }
                ViewCompat.setLayoutDirection(partNumberEditor, View.LAYOUT_DIRECTION_LTR);
                partNumberEditor.setGravity(numIsRtl ? Gravity.LEFT : Gravity.RIGHT);

                partNumberTextView.setText(getPartNumberString());
                partNumberEditor.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String requiredpartString = s.toString();
                        if (!TextUtils.isEmpty(requiredpartString)) {
                            int requiredPart = Integer.valueOf(requiredpartString);
                            int firstpartNumber = mPartsInfo.firstPart.partNumber;
                            int lastpartNumber = mPartsInfo.lastPart;
                            if (requiredPart < firstpartNumber || requiredPart > lastpartNumber) {
                                setNumberEditorValid(false, partNumberEditor, getResources().getString(R.string.invalid_part_number,
                                        firstpartNumber, lastpartNumber));

                            } else {
                                setNumberEditorValid(true, partNumberEditor, null);
                            }
                        }
                    }
                });
                partNumberEditor.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus)
                        switchEditTextToTextView(partNumberEditor, partNumberTextView);
                    Util.enableSoftInput(v, hasFocus);
                });
                partNumberEditor.setOnEditorActionListener((v, actionId, event) -> {
                    boolean handled = false;
                    if (v.getError() != null) {
                        return false;
                    } else {
                        if (actionId == EditorInfo.IME_ACTION_GO) {

                            mPager.setCurrentItem((
                                            mBookDatabaseHelper.pageId2position(
                                                    mBookDatabaseHelper.getPageId(Integer.valueOf(v.getText().toString()),
                                                            currentPageInfo.pageNumber))),
                                    true);
                            switchEditTextToTextView(partNumberEditor, partNumberTextView);
                            handled = true;
                        }
                    }
                    return handled;
                });


            }
            pageNumberTextView.setOnClickListener(mShowPageNumberPickerDialogClickListener);
            pageNumberTextView.setOnLongClickListener(v -> {
                showEditingPageNumberInPlace(pageNumberTextView, pageNumberEditor);
                return true;
            });
            pageNumberEditor.setKeyboardListener(editText -> switchEditTextToTextView(pageNumberEditor, pageNumberTextView));


            int maxPageLength = String.valueOf(mPartsInfo.largestPage).length();
            char[] maxPage = new char[Math.min(maxPageLength, 6)];
            Arrays.fill(maxPage, '9');
            pageNumberTextView.setText(maxPage, 0, maxPage.length);
            pageNumberTextView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            ViewGroup.LayoutParams params = pageNumberTextView.getLayoutParams();
            int currentPageWidth = pageNumberTextView.getMeasuredWidth();
            params.width = currentPageWidth;
            params.height = pageNumberTextView.getMeasuredHeight();
            pageNumberTextView.setLayoutParams(params);

            pageNumberEditor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxPageLength)});
            boolean numIsRtl = true;
            params = pageNumberEditor.getLayoutParams();
            params.width = (currentPageWidth - (pageNumberTextView.getPaddingLeft() + pageNumberTextView.getPaddingRight())) + (pageNumberEditor.getPaddingLeft() + pageNumberEditor.getPaddingRight());
            pageNumberEditor.setLayoutParams(params);
            pageNumberEditor.setTranslationX((float) (pageNumberEditor.getPaddingRight() - pageNumberTextView.getPaddingRight()));
            if (ViewCompat.getLayoutDirection(pageNumberEditor) != View.LAYOUT_DIRECTION_RTL) {
                numIsRtl = false;
            }
            ViewCompat.setLayoutDirection(pageNumberEditor, View.LAYOUT_DIRECTION_LTR);
            pageNumberEditor.setGravity(numIsRtl ? Gravity.LEFT : Gravity.RIGHT);


            pageNumberTextView.setText(getPageNumberStringFromCurrentPage());


            pageNumberEditor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String requiredPageString = s.toString();
                    if (!TextUtils.isEmpty(requiredPageString)) {
                        int requiredPage = Integer.parseInt(requiredPageString);
                        PartInfo partInfo = mBookDatabaseHelper.getPartInfo(currentPageInfo.partNumber);
                        int firstPageNumber = partInfo.firstPage;
                        int lastPageNumber = partInfo.lastPage;
                        if (requiredPage < firstPageNumber || requiredPage > lastPageNumber) {
                            setNumberEditorValid(false, pageNumberEditor, getResources().getString(R.string.invalid_page_number,
                                    firstPageNumber, lastPageNumber));

                        } else {
                            setNumberEditorValid(true, pageNumberEditor, null);

                        }
                    }

                }
            });

            pageNumberEditor.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    switchEditTextToTextView(pageNumberEditor, pageNumberTextView);
                }
                Util.enableSoftInput(v, hasFocus);
            });

            pageNumberEditor.setOnEditorActionListener((v, actionId, event) -> {
                boolean handled = false;

                if (v.getError() != null) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    mPager.setCurrentItem((
                                    mBookDatabaseHelper.pageId2position(
                                            mBookDatabaseHelper.getPageId(currentPageInfo.partNumber,
                                                    Integer.valueOf(v.getText().toString())))),
                            true);

                    switchEditTextToTextView(pageNumberEditor, pageNumberTextView);
                    handled = true;

                }
                return handled;
            });

            seekBar = findViewById(R.id.seek_bar);
            ViewCompat.setLayoutDirection(seekBar, ViewCompat.LAYOUT_DIRECTION_RTL);
            seekBar.setMax(PAGE_COUNT);
            seekBar.setProgress(mPager.getCurrentItem());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mPager.setCurrentItem(progress, false);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switchEditTextToTextView(pageNumberEditor, pageNumberTextView);
                    pageNumberTextView.setVisibility(View.VISIBLE);
                    pageNumberTextView.setText(getPageNumberStringFromCurrentPage());
                    chapterTitleTextView.setText(parentTitle.title);
                    if (partNumberEditor.getVisibility() == View.VISIBLE)
                        switchEditTextToTextView(partNumberEditor, partNumberTextView);
                    partNumberTextView.setText(getPartNumberString());
                    UserDataDBHelper userDataDBHelper = UserDataDBHelper.getInstance(ReadingActivity.this, bookId);
                    bookmarkImageButton.setSelected(userDataDBHelper.isPageBookmarked(currentPageInfo.pageId));
                    seekBar.setProgress(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {


                }
            });

            chapterTitleTextView.setOnClickListener(v -> {
                Intent intent = new Intent(ReadingActivity.this, TableOfContentsBookmarksActivity.class);
                intent.putExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, bookId);
                intent.putExtra(BooksInformationDBContract.BookInformationEntery.COLUMN_NAME_TITLE, bookName);
                intent.putExtra(KEY_TAB_NAME, TableOfContentsBookmarksActivity.TableOfContentAndNotesTab.TAB_TABLE_OF_CONTENTS.ordinal());
                intent.putExtra(BookDatabaseContract.TitlesEntry.COLUMN_NAME_PAGE_ID, parentTitle.pageInfo.pageId);
                intent.putExtra(BookDatabaseContract.TitlesEntry.COLUMN_NAME_ID, parentTitle.id);
                startActivityForResult(intent, PICK_TITLE_REQUEST);
            });

        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    public class BookPageFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        private final String TAG = "BookPageFragmentStatePagerAdapter";


        BookPageFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            return BookPageFragment.newInstance(bookId, mBookDatabaseHelper.position2PageId(position), position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

    }
}