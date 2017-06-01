package com.fekracomputers.islamiclibrary.model;

import android.content.Context;
import android.os.Parcel;
import android.support.v4.app.Fragment;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 7/3/2017.
 */

public class AllBooksTab implements BookCatalogElement {
    private final String name;

    public AllBooksTab(Context c) {
        name=c.getString(R.string.all);
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Fragment getNewFragment() {
        return BookListFragment.newInstance(BookListFragment.FILTERALL, 0);

    }

    @Override
    public int getIconDrawableId() {
        return R.drawable.ic_all_books;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected AllBooksTab(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<AllBooksTab> CREATOR = new Creator<AllBooksTab>() {
        @Override
        public AllBooksTab createFromParcel(Parcel source) {
            return new AllBooksTab(source);
        }

        @Override
        public AllBooksTab[] newArray(int size) {
            return new AllBooksTab[size];
        }
    };
}
