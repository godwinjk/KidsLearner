package com.godwin.drawview.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Godwin on 28-11-2017 18:04 for DrawView.
 * @author : Godwin Joseph Kurinjikattu
 */
data class HomeItem(val name: String, val id: Long, val iconId: Int, val iconUri: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(id)
        parcel.writeInt(iconId)
        parcel.writeString(iconUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HomeItem> {
        override fun createFromParcel(parcel: Parcel): HomeItem {
            return HomeItem(parcel)
        }

        override fun newArray(size: Int): Array<HomeItem?> {
            return arrayOfNulls(size)
        }
    }

}