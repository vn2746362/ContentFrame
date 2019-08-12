package com.horizon.contentframe.Handlers;

import android.os.Parcel;
import android.os.Parcelable;

/*By Horizon*/
public interface ObjectCallHandler <T> extends Parcelable{
    void onCall(T obj);

    @Override default int describeContents() { return 0; }
    @Override default void writeToParcel(Parcel parcel, int i) { }
}
