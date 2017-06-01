package com.fekracomputers.islamiclibrary.search;

import android.content.Context;

import com.fekracomputers.islamiclibrary.databases.BookDatabaseHelper;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.search.model.BookSearchResultsContainer;
import com.fekracomputers.islamiclibrary.search.model.SearchOptions;
import com.fekracomputers.islamiclibrary.search.model.SearchResult;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 26/2/2017.
 */
public class BookSearcher {


    private final Context context;
    private final boolean isExpanded;
    private final String searchString;
    private final SearchOptions searchOptions;

    public BookSearcher(Context context, boolean isExpanded, String searchString, SearchOptions searchOptions) {
        this.context = context;
        this.isExpanded = isExpanded;
        this.searchString = searchString;
        this.searchOptions = searchOptions;
    }


    public BookSearchResultsContainer getBookSearchResultsContainer(int bookId) {
        BookDatabaseHelper bookDatabaseHelper = BookDatabaseHelper.getInstance(context, bookId);
        ArrayList<SearchResult> results = bookDatabaseHelper.search(searchString,searchOptions);

        BookPartsInfo bookPartsInfo=bookDatabaseHelper.getBookPartsInfo();

        ListIterator<SearchResult> searchResultIterator = results.listIterator();
        while (searchResultIterator.hasNext()) {
            SearchResult searchResult = searchResultIterator.next();
            if (!searchResult.isRequired()) {
                searchResultIterator.remove();
            }
        }
        return new BookSearchResultsContainer(isExpanded, bookId, bookDatabaseHelper.getBookName(),bookPartsInfo, results);
    }
}





