package com.fekracomputers.islamiclibrary.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Mohammad Yahia on 13/11/2016.
 */

public class Title implements Parcelable {

    public static final Parcelable.Creator<Title> CREATOR = new Parcelable.Creator<Title>() {
        @Override
        public Title createFromParcel(@NonNull Parcel source) {
            return new Title(source);
        }

        @Override
        public Title[] newArray(int size) {
            return new Title[size];
        }
    };
    public PageInfo pageInfo;
    public int id;
    public int parentId;
    public String title;
    public boolean isParent = false;

    public Title(int id, int parentId, int PageNumber, int partNumber, int pageId, String title, boolean isParent) {

        pageInfo = new PageInfo(pageId, partNumber, PageNumber);
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.isParent = isParent;
    }

    protected Title(@NonNull Parcel in) {
        this.pageInfo = in.readParcelable(PageInfo.class.getClassLoader());
        this.id = in.readInt();
        this.parentId = in.readInt();
        this.title = in.readString();
        this.isParent = in.readByte() != 0;
    }

    /**
     * Factory Method to construct root title should only be used to be expanded not to navigate to
     *
     * @param title should be the book name
     * @return new root title with id=0
     */
    public static Title createRootTitle(@NonNull String title) {
        return new Title(0, -1, 0, 0, 0, title, true);
    }

    public boolean isRootTitle() {
        return parentId == -1 && id == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(this.pageInfo, flags);
        dest.writeInt(this.id);
        dest.writeInt(this.parentId);
        dest.writeString(this.title);
        dest.writeByte(this.isParent ? (byte) 1 : (byte) 0);
    }


}
