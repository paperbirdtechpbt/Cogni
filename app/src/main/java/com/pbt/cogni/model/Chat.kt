package com.pbt.cogni.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Chat(
    var sender: Int? = 0,
    var timestamp: Long? = Date().time,
    var type: String? = "",
    var text: String? = "",
    var read: Int? = 0,
    var key: String? = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(sender)
        parcel.writeValue(timestamp)
        parcel.writeString(type)
        parcel.writeString(text)
        parcel.writeValue(read)
        parcel.writeString(key)
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }


}