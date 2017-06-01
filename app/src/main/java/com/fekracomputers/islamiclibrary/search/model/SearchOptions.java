package com.fekracomputers.islamiclibrary.search.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 22/2/2017.
 */
public class SearchOptions implements Parcelable {

    private boolean areAllAlefEquivelent,isTaMarboutaEquivlantToHax,neglectTashkeel;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.areAllAlefEquivelent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTaMarboutaEquivlantToHax ? (byte) 1 : (byte) 0);
        dest.writeByte(this.neglectTashkeel ? (byte) 1 : (byte) 0);
    }

    public SearchOptions() {
    }

    protected SearchOptions(Parcel in) {
        this.areAllAlefEquivelent = in.readByte() != 0;
        this.isTaMarboutaEquivlantToHax = in.readByte() != 0;
        this.neglectTashkeel = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SearchOptions> CREATOR = new Parcelable.Creator<SearchOptions>() {
        @Override
        public SearchOptions createFromParcel(Parcel source) {
            return new SearchOptions(source);
        }

        @Override
        public SearchOptions[] newArray(int size) {
            return new SearchOptions[size];
        }
    };
}
