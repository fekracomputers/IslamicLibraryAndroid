package com.fekracomputers.islamiclibrary.search.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;

import com.fekracomputers.islamiclibrary.model.PageInfo;
import com.fekracomputers.islamiclibrary.model.Title;
import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammad Yahia on 20/2/2017.
 */
public class SearchResult implements Parcelable, Comparable<SearchResult> {

    private PageInfo pageInfo;
    private int bookId;
    //private Title chapterTitle;
    private String unformatedPage;
    private String searchString;
    public Title parentTitle;
    private SearchOptions searchOptions;

    public SearchResult(int bookId, PageInfo pageInfo) {

        this.bookId = bookId;
        this.pageInfo = pageInfo;
    }


    public CharSequence getformatedSearchSnippet() {
        //TODO implement improved snippet function
        String CleanedSearchString = " " + ArabicUtilities.cleanTextForSearchingWthStingBuilder(searchString) + " ";
        StringBuilder cleanedUnformattedPage = new StringBuilder(ArabicUtilities.cleanTextForSearchingWthStingBuilder(unformatedPage));
        int firstMatchStart = cleanedUnformattedPage.indexOf(CleanedSearchString);
        cleanedUnformattedPage.delete(0, Math.max(firstMatchStart - 100, 0));
        cleanedUnformattedPage.delete(
                Math.min(firstMatchStart + CleanedSearchString.length() + 100, cleanedUnformattedPage.length())
                , cleanedUnformattedPage.length());
        cleanedUnformattedPage.insert(0, "...");
        cleanedUnformattedPage.append("...");

        Spannable snippet = SpannableString.
                valueOf(cleanedUnformattedPage.toString());
        int index = TextUtils.indexOf(snippet, CleanedSearchString);
        while (index >= 0) {

            snippet.setSpan(new BackgroundColorSpan(0xFF8B008B), index, index
                    + CleanedSearchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = TextUtils.indexOf(snippet, CleanedSearchString, index + CleanedSearchString.length());
        }

        return snippet;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }



    public SearchResult(int bookId, int pageId, int partNumber, int pagrNumber, String page, SearchOptions searchOptions, String searchString, Title title) {
        this.bookId = bookId;
        this.unformatedPage = page;
        this.searchOptions = searchOptions;
        this.searchString = searchString;
        this.parentTitle = title;
        pageInfo = new PageInfo(pageId, partNumber, pagrNumber);
    }

    public boolean isRequired() {
        //TODO : implement filtering based on options
        return true;
    }

    public int getBookId() {
        return bookId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pageInfo, flags);
        dest.writeInt(this.bookId);
        dest.writeString(this.unformatedPage);
        dest.writeString(this.searchString);
        dest.writeParcelable(this.searchOptions, flags);
    }

    protected SearchResult(Parcel in) {
        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        this.bookId = in.readInt();
        this.unformatedPage = in.readString();
        this.searchString = in.readString();
        this.searchOptions = in.readParcelable(SearchOptions.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel source) {
            return new SearchResult(source);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };


    @Override
    public int compareTo(@NonNull SearchResult o) {
        return this.pageInfo.pageId - o.pageInfo.pageId;
    }
}
