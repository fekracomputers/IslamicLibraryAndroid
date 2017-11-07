package com.fekracomputers.islamiclibrary.browsing.controller;

import android.content.Context;

import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BookCollectionInfo;
import com.fekracomputers.islamiclibrary.model.BooksCollection;

import java.util.ArrayList;

/**
 * Created by Mohammad on 31/10/2017.
 */

public class BookCollectionsController {
    private Context context;

    public BookCollectionsController(Context context) {
        this.context = context;
    }

    public void toggleFavourite(BookCollectionInfo bookCollectionInfo) {
        if (!bookCollectionInfo.isFavourite()) {
            addToFavourite(bookCollectionInfo);
        } else {
            removeFromFavourite(bookCollectionInfo);
        }
    }

    public void removeFromFavourite(BookCollectionInfo bookCollectionInfo) {
        removeFromCollection(bookCollectionInfo, UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
    }


    public void addToFavourite(BookCollectionInfo bookCollectionInfo) {
        addToCollection(bookCollectionInfo, UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);

    }

    private void addToCollection(BookCollectionInfo bookCollectionInfo, int collectionId) {
        if (!bookCollectionInfo.doBelongTo(collectionId)) {
            UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).addToCollection(collectionId);
            bookCollectionInfo.addToCollection(collectionId);

        }
    }

    private void removeFromCollection(BookCollectionInfo bookCollectionInfo, int collectionId) {
        if (bookCollectionInfo.doBelongTo(collectionId)) {
            UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).removeFromCollection(collectionId);
            bookCollectionInfo.removeFromCollection(collectionId);

        }
    }

    public ArrayList<BooksCollection> getBookCollections(BookCollectionInfo bookCollectionInfo, Context context, boolean viewdOnly) {
        return UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).getBookCollections(viewdOnly);
    }

    public ArrayList<BooksCollection> getAllBookCollections(Context context, boolean viewdOnly, boolean nonAutomaticOnly) {
        return UserDataDBHelper.getInstance(context).getBooksCollections(viewdOnly, nonAutomaticOnly);
    }

    public BooksCollection createNewCollection(String string) {
        return UserDataDBHelper.getInstance(context).addBookCollection(string);

    }

    public void updateCollectionStatus(BookCollectionInfo bookCollectionInfo) {
        UserDataDBHelper.getInstance(context).updateCollectionStatus(bookCollectionInfo.getBookId(), bookCollectionInfo.getBooksCollectionIds());

    }
}
