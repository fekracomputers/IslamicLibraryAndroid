package com.fekracomputers.islamiclibrary.search.view.viewHolder;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.model.BookPartsInfo;
import com.fekracomputers.islamiclibrary.search.model.SearchResult;
import com.fekracomputers.islamiclibrary.search.view.SearchResultRecyclerViewAdapter;
import com.fekracomputers.islamiclibrary.tableOFContents.TableOfContentsUtils;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 22/2/2017.
 */
public class SearchResultViewHolder extends ChildViewHolder {
    private final SearchResultRecyclerViewAdapter.SearchResultOnClickDelegateListener mListener;
    private TextView searchSnippetTextView;
    private TextView pageNumberTextView;
    private TextView chapterTitleTextView;
    private View searchResultView;
    private Resources mResources;

    public SearchResultViewHolder(@NonNull final View searchResult,
                                  final SearchResultRecyclerViewAdapter.SearchResultOnClickDelegateListener mListener,
                                  Resources mResources) {
        super(searchResult);
        searchSnippetTextView = searchResult.findViewById(R.id.search_snippet_text_view);
        pageNumberTextView = searchResult.findViewById(R.id.part_page_number_tv);
        chapterTitleTextView = searchResult.findViewById(R.id.chapter_title);


        this.searchResultView = searchResult;
        this.mListener = mListener;
        this.mResources = mResources;
    }

    public void bind(@NonNull final SearchResult searchResult, BookPartsInfo bookPartsInfo) {

        searchSnippetTextView.setText(searchResult.getformatedSearchSnippet());
        pageNumberTextView.setText(TableOfContentsUtils.formatPageAndPartNumber(
                bookPartsInfo,
                searchResult.getPageInfo(),
                R.string.page_slash_part,
                R.string.page_number_with_label,
                mResources
        ));
        pageNumberTextView.setText(String.valueOf(searchResult.getPageInfo().pageNumber));
        chapterTitleTextView.setText(searchResult.parentTitle.title);


        searchResultView.setOnClickListener(v -> mListener.onSearchResultClicked(getParentAdapterPosition(), getChildAdapterPosition()));

    }

}
