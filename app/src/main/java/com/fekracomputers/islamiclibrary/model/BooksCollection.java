package com.fekracomputers.islamiclibrary.model;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.fekracomputers.islamiclibrary.databases.UserDataDBHelper;

/**
 * Created by Mohammad on 23/10/2017.
 */

public class BooksCollection implements Comparable<BooksCollection> {

    private int order;
    private boolean visibility;
    private int automaticID;
    private String name;
    private Cursor cursor;
    private int booksCollectionId;

    public BooksCollection(int order, boolean visibility, int automaticID, String name, int booksCollectionId) {
        this.order = order;
        this.visibility = visibility;
        this.automaticID = automaticID;
        this.name = name;
        this.booksCollectionId = booksCollectionId;
    }

    public String getName() {
        return name;
    }

    public Cursor getCursor(Context context) {
        if (cursor == null || cursor.isClosed()) {
            cursor = UserDataDBHelper.getInstance(context).getBooksCollectionCursor(this);
        }
        return cursor;
    }

    public Cursor reAcquireCursor(Context context) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = UserDataDBHelper.getInstance(context).getBooksCollectionCursor(this);
        return cursor;
    }


    @Override
    public int hashCode() {
        return booksCollectionId;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BooksCollection) && (booksCollectionId == ((BooksCollection) obj).booksCollectionId);
    }

    @Override
    public int compareTo(@NonNull BooksCollection o) {
        //if you cant understand this then press alt-enter and convert ?: ti if else recursivly until it is full expanded :)
        return order != o.order ? order < o.order ? -1 : 1 : booksCollectionId < o.booksCollectionId ? -1 : booksCollectionId == o.booksCollectionId ? 0 : 1;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public boolean isAutomatic() {
        return automaticID != 0;
    }

    public int getCollectionsId() {
        return booksCollectionId;
    }

    public int getAutomaticId() {
        return automaticID;
    }
}
