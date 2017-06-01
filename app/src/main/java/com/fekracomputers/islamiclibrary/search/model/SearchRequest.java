package com.fekracomputers.islamiclibrary.search.model;

import java.util.ArrayList;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 19/2/2017.
 */
public class SearchRequest {
    public SearchRequest(String searchString, SearchOptions searchOptions, ArrayList<Integer> booksToBeSearchedIds, boolean expandAll) {
        this.searchString = searchString;
        this.searchOptions = searchOptions;
        this.booksToBeSearchedIds = booksToBeSearchedIds;
        this.expandAll = expandAll;
    }


    public String searchString;
    public SearchOptions searchOptions;
    private ArrayList<Integer> booksToBeSearchedIds;
    public boolean expandAll;

    public ArrayList<Integer> getSearchBleBooksId() {
        return booksToBeSearchedIds;
    }

}
