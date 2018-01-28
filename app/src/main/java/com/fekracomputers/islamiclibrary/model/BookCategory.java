package com.fekracomputers.islamiclibrary.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;

/**
 * Model that represents a book category
 */
public class BookCategory implements Parcelable {
    public static final Creator<BookCategory> CREATOR = new Creator<BookCategory>() {
        @Override
        public BookCategory createFromParcel(@NonNull Parcel source) {
            return new BookCategory(source);
        }

        @Override
        public BookCategory[] newArray(int size) {
            return new BookCategory[size];
        }
    };
    private int id;
    private int order;
    private String name;
    private int numberOfBooks;
    private boolean hasDownloadedBooks;

    public BookCategory(int id, int order, String name, int numberOfBooks, boolean hasDownloadedBooks) {
        this.id = id;
        this.order = order;
        this.name = name;
        this.numberOfBooks = numberOfBooks;
        this.hasDownloadedBooks = hasDownloadedBooks;
    }

    public BookCategory(int id, String name) {

        this.id = id;
        this.name = name;
    }

    public BookCategory(int id) {
        this.id = id;
    }

    public BookCategory(@NonNull Parcel in) {
        this.id = in.readInt();
        this.order = in.readInt();
        this.name = in.readString();
        this.numberOfBooks = in.readInt();
        this.hasDownloadedBooks = in.readByte() != 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BookCategory && ((BookCategory) obj).getId() == this.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fragment getNewFragment() {
        return BookListFragment.newInstance(BookListFragment.FILTERBYCATEGORY, id);
    }

    public int getIconDrawableId() {
        return R.drawable.ic_specific_category;
    }

    public int getNumberOfBooks() {
        return numberOfBooks;
    }

    public boolean hasDownloadedBooks() {
        return hasDownloadedBooks;
    }

    public void setHasDownloadedBooks(boolean hasDownloadedBooks)
    {
        this.hasDownloadedBooks=hasDownloadedBooks;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.order);
        dest.writeString(this.name);
        dest.writeInt(this.numberOfBooks);
        dest.writeByte(this.hasDownloadedBooks ? (byte) 1 : (byte) 0);
    }
}
