package com.fekracomputers.islamiclibrary.homeScreen.controller;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;
import com.fekracomputers.islamiclibrary.homeScreen.callbacks.BookCollectionsCallBack;
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
        bookCollectionsControllerCallback.notifyCollectionAdded(booksCollection.getCollectionsId());
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

    public PopupMenu.OnMenuItemClickListener getMoreMenuListener(BooksCollection booksCollection,
                                                                 Context context
    ) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_show_all:
                    return true;
                case R.id.menu_select_all:
                    return true;
                case R.id.menu_item_rename: {
                    bookCollectionsControllerCallback.showRenameDialog(booksCollection);
                    return true;
                }
                case R.id.menu_item_clear:
                    UserDataDBHelper.getInstance(context).
                            clearCollection(booksCollection.getCollectionsId());
                    bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection.getCollectionsId());
                    return true;
                case R.id.menu_item_hide:
                    UserDataDBHelper.getInstance(context).
                            hideCollectionDown(booksCollection.getCollectionsId());
                    bookCollectionsControllerCallback.notifyCollectionRemoved(booksCollection.getCollectionsId());
                    return true;

                case R.id.menu_delete_collection:
                    UserDataDBHelper.getInstance(context).
                            deleteCollection(booksCollection.getCollectionsId());
                    bookCollectionsControllerCallback.notifyCollectionRemoved(booksCollection.getCollectionsId());
                    return true;
                case R.id.menu_move_up: {
                    int oldPosition = booksCollection.getOrder();
                    int newPosition = UserDataDBHelper.getInstance(context).
                            moveCollectionUp(booksCollection.getCollectionsId(), oldPosition);
                    bookCollectionsControllerCallback.notifyBookCollectionMoved(
                            booksCollection.getCollectionsId(),
                            oldPosition,
                            newPosition);
                    return true;
                }
                case R.id.menu_move_down: {
                    int oldPosition = booksCollection.getOrder();
                    int newPosition = UserDataDBHelper.getInstance(context).
                            moveCollectionDown(booksCollection.getCollectionsId(), oldPosition);
                    bookCollectionsControllerCallback.notifyBookCollectionMoved(
                            booksCollection.getCollectionsId(),
                            oldPosition,
                            newPosition);
                    return true;

                }
                default:
                    return false;
            }

        };
    }

    public void renameCollection(int collectionId, String newName) {
        UserDataDBHelper.getInstance(context).
                renameCollection(collectionId, newName);
    }

    public interface BookCollectionsControllerCallback {
        void notifyBookCollectionCahnged(int collectionId);

        void registerHomeScreen(BookCollectionsCallBack bookCollectionsCallBack);

        void unRegisterHomeScreen(BookCollectionsCallBack bookCollectionsCallBack);

        void notifyCollectionAdded(int collectionsId);

        void notifyCollectionRemoved(int collectionsId);

        void notifyBookCollectionMoved(int collectionsId, int oldPosition, int newPosition);

        void showRenameDialog(BooksCollection booksCollection);
    }
}
