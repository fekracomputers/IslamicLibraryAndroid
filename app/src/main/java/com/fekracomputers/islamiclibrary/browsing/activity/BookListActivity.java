package com.fekracomputers.islamiclibrary.browsing.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;

import static com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment.FILTERALL;
import static com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment.FILTERBYAuthour;
import static com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment.FILTERBYCATEGORY;


/**
 * Activity used when multi-pane is not available to display filtered book list
 * it is just a host for  {@link BookListFragment}
 * <p>
 * pass  to the intent
 * </p>
 * <p>
 * {@link BookListFragment#FILTERTYPE} :
 * either {@link BookListFragment#FILTERBYAuthour}
 * or {@link BookListFragment#FILTERBYCATEGORY}
 * or {@link BookListFragment#FILTERALL} returns all books
 * </p>
 * <p>
 * {@link BookListFragment#KEY_AUTOUR_ID} autour id in case {@link BookListFragment#FILTERTYPE} = {@link BookListFragment#FILTERBYAuthour}
 * or {@link BookListFragment#KEY_CAT_ID} in case {@link BookListFragment#FILTERTYPE} = {@link BookListFragment#FILTERBYCATEGORY}
 * neglected otherwise
 * </p>
 */
public class BookListActivity extends BrowsingActivity {


    @Override
    protected void inflateUi(Bundle savedInstanceState) {
        setContentView(R.layout.activity_book_catalog_display);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mPaneNumber = 1;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        int filterType = intent.getIntExtra(BookListFragment.FILTERTYPE, FILTERALL);
        int id;
        String title = "";
        switch (filterType) {
            case FILTERBYCATEGORY:
                id = intent.getIntExtra(BooksInformationDBContract.BooksCategories.COLUMN_NAME_CATEGORY_ID, 0);
                title = getIntent().getStringExtra(BooksInformationDBContract.CategotyEntry.COLUMN_NAME_CATEGORY_TITLE);
                break;
            case FILTERBYAuthour:
                id = intent.getIntExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_AUTHOR_ID, 54);
                title = getIntent().getStringExtra(BooksInformationDBContract.AuthorEntry.COLUMN_NAME_NAME);

                break;
            case FILTERALL:
            default:
                id = 0;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        mDownloadOnlyBanner = findViewById(R.id.browsing_header_banner);
        mDownloadOnlyBanner.setOnClickListener(v -> switchDownloadOnlyFilter(!shouldDisplayDownloadedOnly()));
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mShouldDisplayDownloadOnly = sharedPref.getBoolean(KEY_DOWNLOADED_ONLY, false);
        setDownloadOnlyBannerText(mShouldDisplayDownloadOnly);

        Fragment bookListFragment = BookListFragment.newInstance(filterType, id);
        fragmentTransaction.replace(R.id.book_list_container, bookListFragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void setDownloadOnlySwitchNoCallBack(boolean showDownloadedOnly) {
//do nothing
    }


}
