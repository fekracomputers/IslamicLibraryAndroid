package com.fekracomputers.islamiclibrary.browsing.controller;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fekracomputers.islamiclibrary.homeScreen.HomeScreenCallBack;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.model.BookCollectionInfo;
import com.fekracomputers.islamiclibrary.model.BooksCollection;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Mohammad on 31/10/2017.
 */

public class BookCollectionsController {
    private Context context;
    @Nullable
    private BookCollectionsControllerCallback bookCollectionsControllerCallback;

    public BookCollectionsController(Context context, @Nullable BookCollectionsControllerCallback bookCollectionsControllerCallback) {
        this.context = context;
        this.bookCollectionsControllerCallback = bookCollectionsControllerCallback;
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
        if (bookCollectionsControllerCallback != null) {
            bookCollectionsControllerCallback.notifyBookCollectionCahnged(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
        }

    }


    public void addToFavourite(BookCollectionInfo bookCollectionInfo) {
        addToCollection(bookCollectionInfo, UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
        if (bookCollectionsControllerCallback != null) {
            bookCollectionsControllerCallback.notifyBookCollectionCahnged(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
        }


    }

    private void addToCollection(BookCollectionInfo bookCollectionInfo, int collectionId) {
        if (!bookCollectionInfo.doBelongTo(collectionId)) {
            UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).addToCollection(collectionId);
            bookCollectionInfo.addToCollection(collectionId);
            if (bookCollectionsControllerCallback != null) {
                bookCollectionsControllerCallback.notifyBookCollectionCahnged(collectionId);
            }
        }
    }

    private void removeFromCollection(BookCollectionInfo bookCollectionInfo, int collectionId) {
        if (bookCollectionInfo.doBelongTo(collectionId)) {
            UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).removeFromCollection(collectionId);
            bookCollectionInfo.removeFromCollection(collectionId);
            if (bookCollectionsControllerCallback != null) {
                bookCollectionsControllerCallback.notifyBookCollectionCahnged(collectionId);
            }

        }
    }

    public ArrayList<BooksCollection> getBookCollections(BookCollectionInfo bookCollectionInfo, Context context, boolean viewdOnly) {
        return UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).getBookCollections(viewdOnly);
    }

    public ArrayList<BooksCollection> getAllBookCollections(Context context, boolean viewdOnly, boolean nonAutomaticOnly) {
        return UserDataDBHelper.getInstance(context).getBooksCollections(viewdOnly, nonAutomaticOnly);
    }

    public BooksCollection createNewCollection(String string) {
        BooksCollection booksCollection = UserDataDBHelper.getInstance(context).addBookCollection(string);
        bookCollectionsControllerCallback.notifyCollectuinAdded(booksCollection.getCollectionsId());
        return booksCollection;
    }

    public void updateCollectionStatus(BookCollectionInfo bookCollectionInfo, HashSet<Integer> oldBookIdCollectionSet) {
        UserDataDBHelper.getInstance(context).
                updateCollectionStatus(bookCollectionInfo.getBookId(), bookCollectionInfo.getBooksCollectionIds());
        HashSet<Integer> toBeNotified = new HashSet<>(oldBookIdCollectionSet);
        toBeNotified.addAll(bookCollectionInfo.getBooksCollectionIds());
        if (bookCollectionsControllerCallback != null) {
            for (Integer collectionId : toBeNotified) {
                bookCollectionsControllerCallback.notifyBookCollectionCahnged(collectionId);
            }
        }
    }

    public interface BookCollectionsControllerCallback {
        void notifyBookCollectionCahnged(int collectionId);

        void registerHomeScreen(HomeScreenCallBack homeScreenCallBack);

        void unRegisterHomeScreen(HomeScreenCallBack homeScreenCallBack);

        void notifyCollectuinAdded(int collectionsId);
    }
}
