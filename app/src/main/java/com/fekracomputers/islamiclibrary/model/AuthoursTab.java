package com.fekracomputers.islamiclibrary.model;

import android.content.Context;
import android.os.Parcel;
import android.support.v4.app.Fragment;

import com.fekracomputers.islamiclibrary.R;
import com.fekracomputers.islamiclibrary.browsing.fragment.AuthorListFragment;

/**
 * بسم الله الرحمن الرحيم
 * Created by Mohammd Yahia on 7/3/2017.
 */

public class AuthoursTab implements BookCatalogElement {
    private final String name;

    public AuthoursTab(Context context) {
        name = context.getString(R.string.authors);

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
        return  AuthorListFragment.newInstance();

    }

    @Override
    public int getIconDrawableId() {
        return R.drawable.ic_author_tab;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected AuthoursTab(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<AuthoursTab> CREATOR = new Creator<AuthoursTab>() {
        @Override
        public AuthoursTab createFromParcel(Parcel source) {
            return new AuthoursTab(source);
        }

        @Override
        public AuthoursTab[] newArray(int size) {
            return new AuthoursTab[size];
        }
    };
}
