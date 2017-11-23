package com.fekracomputers.islamiclibrary.homeScreen;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.fekracomputers.islamiclibrary.model.BooksCollection;

import java.util.TreeMap;

/**
 * Created by Mohammad on 22/11/2017.
 */

class CollectionDiffCallback extends DiffUtil.Callback {
    private final TreeMap<Integer, BooksCollection> oldBooksCollections;
    private final TreeMap<Integer, BooksCollection> newBookcollections;

    CollectionDiffCallback(TreeMap<Integer, BooksCollection> oldBooksCollections, TreeMap<Integer, BooksCollection> newBookcollections) {
        super();
        this.oldBooksCollections = oldBooksCollections;
        this.newBookcollections = newBookcollections;
    }

    @Override
    public int getOldListSize() {
        return oldBooksCollections.size();
    }

    @Override
    public int getNewListSize() {
        return newBookcollections.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
       return HomeScreenRecyclerViewAdapter.getBookCollectionIDByPosition(oldBooksCollections,oldItemPosition)
               == HomeScreenRecyclerViewAdapter.getBookCollectionIDByPosition(newBookcollections,newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
