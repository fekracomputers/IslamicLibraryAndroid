package com.fekracomputers.islamiclibrary.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by moda_ on 8/2/2017.
 */
public class BookPartsInfo implements Parcelable {

    public int largestPage;
    public PartInfo firstPart;
    public int lastPart;

    public BookPartsInfo(PartInfo firstPart, int lastPart, int largestPage) {
        this.firstPart = firstPart;
        this.lastPart = lastPart;
        this.largestPage = largestPage;
    }

    public boolean isMultiPart() {
//        if (DoesBookHaveZeroPageSinglePart()) {
//            return firstPart.partNumber + 1 != lastPart;
//        }
//        else {
            //this condition to avoid problems with books having first 0th part with one page
            return firstPart.partNumber != lastPart;
//        }
    }

    /**
     * tests wether the book has its first part 0 and has only one page with page number zero
     * @return true if the book has [0/0] problem
     */
    private boolean DoesBookHaveZeroPageSinglePart() {
        return firstPart.partNumber == 0 && firstPart.firstPage == 0 && firstPart.firstPage == firstPart.lastPage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.largestPage);
        dest.writeParcelable(this.firstPart, flags);
        dest.writeInt(this.lastPart);
    }

    protected BookPartsInfo(Parcel in) {
        this.largestPage = in.readInt();
        this.firstPart = in.readParcelable(PartInfo.class.getClassLoader());
        this.lastPart = in.readInt();
    }

    public static final Parcelable.Creator<BookPartsInfo> CREATOR = new Parcelable.Creator<BookPartsInfo>() {
        @Override
        public BookPartsInfo createFromParcel(Parcel source) {
            return new BookPartsInfo(source);
        }

        @Override
        public BookPartsInfo[] newArray(int size) {
            return new BookPartsInfo[size];
        }
    };
}

