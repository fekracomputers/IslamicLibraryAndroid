package com.fekracomputers.islamiclibrary.search.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.search.model.BookSearchResultsContainer;
import com.fekracomputers.islamiclibrary.search.model.SearchResult;
import com.fekracomputers.islamiclibrary.search.view.viewHolder.BookResultsHeaderViewHolder;
import com.fekracomputers.islamiclibrary.search.view.viewHolder.SearchResultViewHolder;

import java.util.List;

/**
 * Created by Mohammad Yahia on 27/10/2016.
 */

public class SearchResultRecyclerViewAdapter extends ExpandableRecyclerAdapter<BookSearchResultsContainer, SearchResult, BookResultsHeaderViewHolder, SearchResultViewHolder> {

    private final SearchResultOnClickDelegateListener mListener;
    private LayoutInflater mInflater;
    private Resources mResources;


    public SearchResultRecyclerViewAdapter(List<BookSearchResultsContainer> parentList,
                                           SearchResultFragment mListener,
                                           Context context
                                          ) {
        super(parentList);
        this.mListener = mListener;
        mInflater = LayoutInflater.from(context);
        this.mResources = context.getResources();
    }

    @NonNull
    @Override
    public BookResultsHeaderViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View searchHeaderView = mInflater.inflate(R.layout.search_result_book_name_item, parentViewGroup, false);
        return new BookResultsHeaderViewHolder(searchHeaderView);
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View searchResultView = mInflater.inflate(R.layout.search_result_item, childViewGroup, false);
        return new SearchResultViewHolder(searchResultView,mListener, mResources);
    }

    @Override
    public void onBindParentViewHolder(@NonNull BookResultsHeaderViewHolder parentViewHolder, int parentPosition, @NonNull BookSearchResultsContainer bookSearchResultsContainer) {
        parentViewHolder.bind(bookSearchResultsContainer);
    }

    @Override
    public void onBindChildViewHolder(@NonNull SearchResultViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull SearchResult searchResult) {
        childViewHolder.bind(searchResult,getParentList().get(parentPosition).bookPartsInfo);

    }



    public interface SearchResultOnClickDelegateListener {
         void onSearchResultClicked(int parentAdapterPosition, int childAdapterPosition);
    }
}
