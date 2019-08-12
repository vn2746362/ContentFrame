package com.horizon.contentframe.Handlers;

import android.os.Parcel;
import android.os.Parcelable;

/*By Horizon*/
public interface VoidCallHandler extends Parcelable {

    void onCall();

    @Override default int describeContents() { return 0; }
    @Override default void writeToParcel(Parcel parcel, int i) { }
}
