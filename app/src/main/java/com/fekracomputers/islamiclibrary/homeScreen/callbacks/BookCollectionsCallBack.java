package com.fekracomputers.islamiclibrary.homeScreen.callbacks;

import com.fekracomputers.islamiclibrary.model.BooksCollection;

/**
 * Created by Mohammad on 22/11/2017.
 */

public interface BookCollectionsCallBack {
    void onBookCollectionCahnged(BooksCollection booksCollection);
    void onBookCollectionAdded(BooksCollection booksCollection);
    void onBookCollectionRemoved(BooksCollection booksCollection);
    void onBookCollectionRenamed(BooksCollection booksCollection, String newName);
    void onBookCollectionMoved(int collectionsId, int oldPosition, int newPosition);

}
