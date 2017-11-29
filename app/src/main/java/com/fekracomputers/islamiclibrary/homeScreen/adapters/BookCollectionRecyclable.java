package com.fekracomputers.islamiclibrary.homeScreen.adapters;

import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.model.BooksCollection;

/**
 * Created by Mohammad on 29/11/2017.
 */

public class BookCollectionRecyclable implements Comparable<BookCollectionRecyclable> {
    private HomeScreenRecyclerViewAdapter.UpdatePayload updatePayload;
    private BooksCollection booksCollection;

    public BookCollectionRecyclable(BooksCollection booksCollection) {
        this.booksCollection = booksCollection;
    }

    public BookCollectionRecyclable(BooksCollection booksCollection, HomeScreenRecyclerViewAdapter.UpdatePayload payload) {
        this.booksCollection = booksCollection;
        this.updatePayload = payload;
    }

    public BooksCollection getBooksCollection() {
        return booksCollection;
    }

    public void setBooksCollection(BooksCollection booksCollection) {
        this.booksCollection = booksCollection;
    }

    public boolean isDirty() {
        return updatePayload != null;
    }

    public HomeScreenRecyclerViewAdapter.UpdatePayload getUpdatePayload() {
        return updatePayload;
    }

    public void setUpdatePayload(HomeScreenRecyclerViewAdapter.UpdatePayload updatePayload) {
        this.updatePayload = updatePayload;
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BookCollectionRecyclable) && (booksCollection.getCollectionsId() ==
                ((BookCollectionRecyclable) obj).booksCollection.getCollectionsId());
    }

    @Override
    public int compareTo(@NonNull BookCollectionRecyclable R) {
        return this.booksCollection.compareTo(R.booksCollection);
    }
}
