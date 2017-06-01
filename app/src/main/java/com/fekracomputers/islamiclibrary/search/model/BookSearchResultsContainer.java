package com.fekracomputers.islamiclibrary.search.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;

import java.util.ArrayList;
import java.util.List;

public class BookSearchResultsContainer implements Parent<SearchResult> {

    // a recipe contains several ingredients
    private ArrayList<SearchResult> mSearchResults;
    private boolean isInitiallyExpanded;
    private String bookName;
    public BookPartsInfo bookPartsInfo;
    public int bookId;


    public BookSearchResultsContainer(boolean isInitiallyExpanded,
                                      int bookId,
                                      String bookName,
                                      BookPartsInfo bookPartsInfo,
                                      ArrayList<SearchResult> searchResults) {
        this.isInitiallyExpanded = isInitiallyExpanded;
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookPartsInfo = bookPartsInfo;
        mSearchResults= searchResults;
    }

    @Override
    public List<SearchResult> getChildList() {
        return mSearchResults;
    }
    public ArrayList<SearchResult> getChildArrayList() {
        return mSearchResults;
    }
    @Override
    public boolean isInitiallyExpanded() {
        return isInitiallyExpanded;
    }

    public String getBookName() {
        return bookName;
    }


    public int getChildCount() {
        return mSearchResults.size();
    }

    public void setChildList(ArrayList<SearchResult> childList) {
        mSearchResults = childList;
    }
}
