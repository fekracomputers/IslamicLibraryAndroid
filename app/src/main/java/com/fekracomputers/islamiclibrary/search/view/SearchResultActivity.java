package com.fekracomputers.islamiclibrary.search.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.appliation.IslamicLibraryApplication;
import com.fekracomputers.islamiclibrary.databases.BooksInformationDBContract;
import com.fekracomputers.islamiclibrary.reading.ReadingActivity;
import com.fekracomputers.islamiclibrary.search.model.BookSearchResultsContainer;
import com.fekracomputers.islamiclibrary.search.model.SearchResult;
import com.fekracomputers.islamiclibrary.utility.Util;

public class SearchResultActivity extends AppCompatActivity implements SearchResultFragment.OnSearchResultFragmentInteractionListener {

    private boolean mIsArabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((IslamicLibraryApplication) getApplication()).refreshLocale(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        mIsArabic = Util.isArabicUi(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment bookListFragment = SearchResultFragment.newInstance(getIntent().getExtras());
            fragmentTransaction.replace(R.id.search_result_fragment_container, bookListFragment);
            fragmentTransaction.commit();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restartIfLocaleChanged(this, mIsArabic);

    }




    @Override
    public void onSearchResultClicked(BookSearchResultsContainer bookSearchResultsContainer, int childAdapterPosition) {
        SearchResult searchResult=bookSearchResultsContainer.getChildList().get(childAdapterPosition);
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra(BooksInformationDBContract.BooksAuthors.COLUMN_NAME_BOOK_ID, searchResult.getBookId());
        intent.putExtra(ReadingActivity.KEY_SEARCH_RESULT_CHILD_POSITION,childAdapterPosition);
        intent.putParcelableArrayListExtra(ReadingActivity.KEY_SEARCH_RESULT_ARRAY_LIST,bookSearchResultsContainer.getChildArrayList());
        startActivity(intent);
    }
}
