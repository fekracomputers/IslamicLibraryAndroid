package com.fekracomputers.islamiclibrary.homeScreen.controller;

import android.content.Context;
import android.support.annotation.Nullable;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.interfaces.BookCardEventsCallback;
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
        BooksCollection booksCollection = UserDataDBHelper.getInstance(context).getBooksCollection(UserDataDBHelper.GlobalUserDBHelper.FAVOURITE_COLLECTION_ID);
        if (!bookCollectionInfo.isFavourite()) {
            addToFavourite(bookCollectionInfo, booksCollection);
        } else {
            removeFromFavourite(bookCollectionInfo, booksCollection);
        }
    }

    public void removeFromFavourite(BookCollectionInfo bookCollectionInfo, BooksCollection booksCollection) {
        removeFromCollection(bookCollectionInfo, booksCollection);
        if (bookCollectionsControllerCallback != null) {
            bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection);
        }

    }

    public void addToFavourite(BookCollectionInfo bookCollectionInfo, BooksCollection booksCollection) {
        addToCollection(bookCollectionInfo, booksCollection);
        if (bookCollectionsControllerCallback != null) {
            bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection);
        }


    }

    private void addToCollection(BookCollectionInfo bookCollectionInfo, BooksCollection booksCollection) {
        if (!bookCollectionInfo.doBelongTo(booksCollection)) {
            UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId()).addToCollection(booksCollection.getCollectionsId());
            bookCollectionInfo.addToCollection(booksCollection);
            if (bookCollectionsControllerCallback != null) {
                bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection);
            }
        }
    }

    private void removeFromCollection(BookCollectionInfo bookCollectionInfo, BooksCollection booksCollection) {
        if (bookCollectionInfo.doBelongTo(booksCollection)) {
            UserDataDBHelper.getInstance(context, bookCollectionInfo.getBookId())
                    .removeFromCollection(booksCollection.getCollectionsId());
            bookCollectionInfo.removeFromCollection(booksCollection);
            if (bookCollectionsControllerCallback != null) {
                bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection);
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
        if (bookCollectionsControllerCallback != null) {
            bookCollectionsControllerCallback.notifyCollectionAdded(booksCollection);
        }
        return booksCollection;
    }

    public void updateCollectionStatus(BookCollectionInfo bookCollectionInfo,
                                       @Nullable ArrayList<BooksCollection> oldBookIdCollectionSet) {
        UserDataDBHelper.getInstance(context).
                updateCollectionStatus(bookCollectionInfo.getBookId(), bookCollectionInfo.getBooksCollectionsIds());
        HashSet<BooksCollection> toBeNotified;
        if (oldBookIdCollectionSet != null)
            toBeNotified = new HashSet<>(oldBookIdCollectionSet);
        else
            toBeNotified = new HashSet<>();

        toBeNotified.addAll(bookCollectionInfo.getBooksCollections());
        if (bookCollectionsControllerCallback != null) {
            for (BooksCollection booksCollection : toBeNotified) {
                bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection);
            }
        }
    }


    public boolean collectionActionHandler(int actionId,
                                           BooksCollection booksCollection,
                                           Context context,
                                           BookCardEventsCallback bookCardEventsCallback) {
        switch (actionId) {
            case R.id.menu_item_show_all:
                if (bookCardEventsCallback != null)
                    bookCardEventsCallback.showAllCollectionBooks(booksCollection);
                return true;
            case R.id.menu_select_all:
                bookCardEventsCallback.selectAllCollectionBooks(booksCollection);
                return true;
            case R.id.menu_item_rename: {
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.showRenameDialog(booksCollection);
                }
                return true;
            }
            case R.id.menu_item_clear:
                UserDataDBHelper.getInstance(context).
                        clearCollection(booksCollection.getCollectionsId());
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.notifyBookCollectionCahnged(booksCollection);
                }
                return true;
            case R.id.menu_item_hide:
                UserDataDBHelper.getInstance(context).
                        changeCollectionVisibility(booksCollection.getCollectionsId(), false);
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.notifyCollectionRemoved(booksCollection);
                }
                return true;
            case R.id.menu_item_show:
                UserDataDBHelper.getInstance(context).
                        changeCollectionVisibility(booksCollection.getCollectionsId(), true);
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.notifyCollectionAdded(booksCollection);
                }
                return true;

            case R.id.menu_delete_collection:
                UserDataDBHelper.getInstance(context).
                        deleteCollection(booksCollection.getCollectionsId());
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.notifyCollectionRemoved(booksCollection);
                }
                return true;
            case R.id.menu_move_up: {
                int oldPosition = booksCollection.getOrder();
                int newPosition = UserDataDBHelper.getInstance(context).
                        moveCollectionUp(booksCollection.getCollectionsId(), oldPosition);
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.notifyBookCollectionMoved(
                            booksCollection.getCollectionsId(),
                            oldPosition,
                            newPosition);
                }
                return true;
            }
            case R.id.menu_move_down: {
                int oldPosition = booksCollection.getOrder();
                int newPosition = UserDataDBHelper.getInstance(context).
                        moveCollectionDown(booksCollection.getCollectionsId(), oldPosition);
                if (bookCollectionsControllerCallback != null) {
                    bookCollectionsControllerCallback.notifyBookCollectionMoved(
                            booksCollection.getCollectionsId(),
                            oldPosition,
                            newPosition);
                }
                return true;

            }
            default:
                return false;
        }
    }

    public void updateCollectionVisibility(BooksCollection booksCollection, boolean isVisible) {
        UserDataDBHelper.getInstance(context).
                changeCollectionVisibility(booksCollection.getCollectionsId(), isVisible);
        if (bookCollectionsControllerCallback != null) {
            bookCollectionsControllerCallback.notifyCollectionVisibilityChanged(booksCollection, isVisible);
        }

    }

    public void renameCollection(BooksCollection collectionId, String newName) {
        UserDataDBHelper.getInstance(context).
                renameCollection(collectionId, newName);
    }

    public interface BookCollectionsControllerCallback {
        void notifyBookCollectionCahnged(BooksCollection booksCollection);

        void registerBookCollectionCallBack(BookCollectionsCallBack bookCollectionsCallBack);

        void unRegisterBookCollectionCallBack(BookCollectionsCallBack bookCollectionsCallBack);

        void notifyCollectionAdded(BooksCollection collectionsId);

        void notifyCollectionRemoved(BooksCollection collectionsId);

        void notifyBookCollectionMoved(int collectionsId, int oldPosition, int newPosition);

        void showRenameDialog(BooksCollection booksCollection);

        void notifyCollectionVisibilityChanged(BooksCollection booksCollection, boolean isVisible);
    }
}
