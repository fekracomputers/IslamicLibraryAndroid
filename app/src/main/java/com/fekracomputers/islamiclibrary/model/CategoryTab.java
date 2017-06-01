package com.fekracomputers.islamiclibrary.model;

import android.content.Context;
import android.os.Parcel;
import android.support.v4.app.Fragment;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookCategoryFragment;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 7/3/2017.
 */

public class CategoryTab implements BookCatalogElement {


    private final String name;

    public CategoryTab(Context c) {
        name=c.getString(R.string.categoreies);
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
        return  BookCategoryFragment.newInstance();
    }

    @Override
      public int getIconDrawableId() {
        return R.drawable.ic_book_category;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected CategoryTab(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<CategoryTab> CREATOR = new Creator<CategoryTab>() {
        @Override
        public CategoryTab createFromParcel(Parcel source) {
            return new CategoryTab(source);
        }

        @Override
        public CategoryTab[] newArray(int size) {
            return new CategoryTab[size];
        }
    };
}
