package com.fekracomputers.islamiclibrary.model;

import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mohammad on 31/10/2017.
 */

public class BookCollectionInfo {
    private Set<Integer> booksCollectionIds;
    private int bookId;

    public BookCollectionInfo(Set<Integer> booksCollectionIds, int bookId) {
        this.booksCollectionIds = booksCollectionIds;
        this.bookId = bookId;
    }

    public Set<Integer> getBooksCollectionIds() {
        return booksCollectionIds;
    }

    public void setBooksCollectionIds(Set<Integer> booksCollectionIds) {
        this.booksCollectionIds = booksCollectionIds;
    }

    public boolean doBelongTo(int collectionId) {
        return booksCollectionIds.contains(collectionId);
    }

    public boolean doesBelongToColletionOtherTHanFavourite() {
        return !getNonFavouriteCollections().isEmpty();
    }

    @NonNull
    private HashSet<Integer> getNonFavouriteCollections() {
        HashSet<Integer> nonFavouriteCollections = new HashSet<>(booksCollectionIds);
        nonFavouriteCollections.remove(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
        return nonFavouriteCollections;
    }

    public boolean isFavourite() {
        return booksCollectionIds.contains(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
    }

    public int getBookId() {
        return bookId;
    }

    public void removeFromCollection(int collectionId) {
        booksCollectionIds.remove(collectionId);
    }

    public void addToCollection(int collectionId) {
        booksCollectionIds.add(collectionId);
    }
}

