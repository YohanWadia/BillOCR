package com.apps.yo.mlkitvision;

import android.os.Parcel;
import android.os.Parcelable;

public class Bill implements Parcelable {

    String item,price;

    public Bill(String item, String price) {
        this.item = item;
        this.price = price;
    }


    protected Bill(Parcel in) {
        item = in.readString();
        price = in.readString();
    }

    public static final Creator<Bill> CREATOR = new Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item);
        dest.writeString(price);
    }
}
