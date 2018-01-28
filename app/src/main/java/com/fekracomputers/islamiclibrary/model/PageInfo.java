package com.fekracomputers.islamiclibrary.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Model class representing the data about a oage not its content
 */

public class PageInfo implements Parcelable {

    /**
     * the database id for the book
     */
    public int pageId;
    /**
     * the part number as in the printed book
     */
    public int partNumber;
    /**
     *  the page number as in the printed book
     */
    public int pageNumber;

    public PageInfo(int pageId, int partNumber, int pageNumber) {
        this.pageId = pageId;
        this.partNumber = partNumber;
        this.pageNumber = pageNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.pageId);
        dest.writeInt(this.partNumber);
        dest.writeInt(this.pageNumber);
    }

    protected PageInfo(@NonNull Parcel in) {
        this.pageId = in.readInt();
        this.partNumber = in.readInt();
        this.pageNumber = in.readInt();
    }

    public static final Parcelable.Creator<PageInfo> CREATOR = new Parcelable.Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(@NonNull Parcel source) {
            return new PageInfo(source);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };
}
