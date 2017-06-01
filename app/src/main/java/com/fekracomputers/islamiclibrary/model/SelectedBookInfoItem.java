package com.fekracomputers.islamiclibrary.model;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 6/3/2017.
 */

public class SelectedBookInfoItem {
    public int bookId;
    public int categoryId;
    public int authorId;

    public SelectedBookInfoItem(int bookId, int categoryId, int authorId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelectedBookInfoItem that = (SelectedBookInfoItem) o;
        return bookId == that.bookId;
    }

    @Override
    public int hashCode() {
        return bookId;
    }
}
