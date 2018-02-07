package com.fekracomputers.islamiclibrary.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by moda_ on 8/2/2017.
 */
public class PartInfo implements Parcelable {

    public int firstPage;
    public  int lastPage;
    public int partNumber;

    public PartInfo(int firstPage, int lastPage,int partNumber) {
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.partNumber = partNumber;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.firstPage);
        dest.writeInt(this.lastPage);
        dest.writeInt(this.partNumber);
    }

    protected PartInfo(@NonNull Parcel in) {
        this.firstPage = in.readInt();
        this.lastPage = in.readInt();
        this.partNumber = in.readInt();
    }

    public static final Parcelable.Creator<PartInfo> CREATOR = new Parcelable.Creator<PartInfo>() {
        @Override
        public PartInfo createFromParcel(@NonNull Parcel source) {
            return new PartInfo(source);
        }

        @Override
        public PartInfo[] newArray(int size) {
            return new PartInfo[size];
        }
    };
}
