package com.fekracomputers.islamiclibrary.model;


import android.os.Parcel;
import android.support.v4.app.Fragment;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.BookListFragment;

public class AuthorInfo implements BookCatalogElement {
    public int id;
    public int higri_death_year;
    public String name;
    public String info;

    public AuthorInfo(int id, int higri_death_year, String name) {
        this.id = id;
        this.higri_death_year = higri_death_year;
        this.name = name;
    }

 
    public AuthorInfo(int authorId, String authorName) {

        id = authorId;
        name = authorName;
    }

    public AuthorInfo(int id, String name, String info, int higri_death_year) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.higri_death_year = higri_death_year;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Fragment getNewFragment() {
        return BookListFragment.newInstance(BookListFragment.FILTERBYAuthour, id);
    }

    @Override
    public int getIconDrawableId() {
        return R.drawable.ic_author_feather;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorInfo that = (AuthorInfo) o;

        return id == that.id;

    }

    @Override
    public String toString() {
        return "AuthorInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.higri_death_year);
        dest.writeString(this.name);
        dest.writeString(this.info);
    }

    protected AuthorInfo(Parcel in) {
        this.id = in.readInt();
        this.higri_death_year = in.readInt();
        this.name = in.readString();
        this.info = in.readString();
    }

    public static final Creator<AuthorInfo> CREATOR = new Creator<AuthorInfo>() {
        @Override
        public AuthorInfo createFromParcel(Parcel source) {
            return new AuthorInfo(source);
        }

        @Override
        public AuthorInfo[] newArray(int size) {
            return new AuthorInfo[size];
        }
    };

    public String getInfo() {
        return info;
    }
}
