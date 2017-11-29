package com.fekracomputers.islamiclibrary.model;

import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Mohammad on 31/10/2017.
 */

public class BookCollectionInfo {
    private Set<BooksCollection> booksCollections;
    private int bookId;

    public BookCollectionInfo() {
    }

    public BookCollectionInfo(Collection<BooksCollection> booksCollections, int bookId) {
        this.booksCollections = new TreeSet<>(
                (o1, o2) -> Integer.valueOf(o1.getCollectionsId()).compareTo(o2.getCollectionsId())
        );
        this.booksCollections.addAll(booksCollections);
        this.bookId = bookId;
    }

    public Set<BooksCollection> getBooksCollections() {
        return booksCollections;
    }

    public void setBooksCollections(Collection<BooksCollection> booksCollections) {
        this.booksCollections = new TreeSet<>(
                (o1, o2) -> Integer.valueOf(o1.getCollectionsId()).compareTo(o2.getCollectionsId())
        );
        this.booksCollections.addAll(booksCollections);
    }

    public Set<Integer> getBooksCollectionsIds() {
        HashSet<Integer> Ids = new HashSet<>(booksCollections.size());
        for (BooksCollection booksCollectionId : booksCollections) {
            Ids.add(booksCollectionId.getCollectionsId());
        }


        return Ids;
    }

    public boolean doBelongTo(BooksCollection booksCollection) {
        return booksCollections.contains(booksCollection);
    }

    public boolean doesBelongToColletionOtherTHanFavourite() {
        return !getNonFavouriteCollections().isEmpty();
    }

    @NonNull
    private HashSet<BooksCollection> getNonFavouriteCollections() {
        HashSet<BooksCollection> nonFavouriteCollections = new HashSet<>(booksCollections);

        nonFavouriteCollections.remove(BooksCollection.fakeCollection(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID));
        return nonFavouriteCollections;
    }

    public boolean isFavourite() {
        return booksCollections.contains(BooksCollection.fakeCollection(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID));
    }

    public int getBookId() {
        return bookId;
    }

    public void removeFromCollection(BooksCollection booksCollection) {
        booksCollections.remove(booksCollection);
    }

    public void addToCollection(BooksCollection booksCollection) {
        booksCollections.add(booksCollection);
    }


    public void setBelongToCollection(BooksCollection booksCollection, boolean b) {
        if (b)
            addToCollection(booksCollection);
        else
            removeFromCollection(booksCollection);
    }

    public int getNonFavouriteCollectionCount() {
        return getNonFavouriteCollections().size();
    }
}

