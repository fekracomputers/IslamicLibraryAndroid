package com.fekracomputers.islamiclibrary.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fekracomputers.islamiclibrary.utility.ArabicUtilities;

import java.util.ArrayList;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 19/2/2017.
 */
public class SearchRequest implements Parcelable {

    public String searchString;
    public SearchOptions searchOptions;
    public boolean expandAll;
    private ArrayList<Integer> booksToBeSearchedIds;
    public SearchRequest(String searchString, SearchOptions searchOptions, ArrayList<Integer> booksToBeSearchedIds, boolean expandAll) {
        this.searchString = searchString;
        this.searchOptions = searchOptions;
        this.booksToBeSearchedIds = booksToBeSearchedIds;
        this.expandAll = expandAll;
    }

    public ArrayList<Integer> getSearchBleBooksId() {
        return booksToBeSearchedIds;
    }

    public String getCleanedSearchString() {
        return ArabicUtilities.cleanTextForSearchingWthStingBuilder(searchString);
    }

    public boolean isExpanded() {
        return expandAll;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.searchString);
        dest.writeParcelable(this.searchOptions, flags);
        dest.writeByte(this.expandAll ? (byte) 1 : (byte) 0);
        dest.writeList(this.booksToBeSearchedIds);
    }

    protected SearchRequest(Parcel in) {
        this.searchString = in.readString();
        this.searchOptions = in.readParcelable(SearchOptions.class.getClassLoader());
        this.expandAll = in.readByte() != 0;
        this.booksToBeSearchedIds = new ArrayList<>();
        in.readList(this.booksToBeSearchedIds, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchRequest> CREATOR = new Parcelable.Creator<SearchRequest>() {
        @Override
        public SearchRequest createFromParcel(Parcel source) {
            return new SearchRequest(source);
        }

        @Override
        public SearchRequest[] newArray(int size) {
            return new SearchRequest[size];
        }
    };
}
