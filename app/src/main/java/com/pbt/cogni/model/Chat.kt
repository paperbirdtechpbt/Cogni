package com.pbt.cogni.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.pbt.cogni.R
import java.util.*

data class Chat(

    var sender: Int? = 0,
    var timestamp: Long? = Date().time,
    var type: String? = "",
    var text: String? = "",
    var fileName: String? = "",
    var read: Int? = 0,
    var key: String? = "",
    var isSelect:Boolean=false,



) : Parcelable {

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readBoolean(),

    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(sender)
        parcel.writeValue(timestamp)
        parcel.writeString(type)
        parcel.writeString(text)
        parcel.writeString(fileName)
        parcel.writeValue(read)
        parcel.writeString(key)
        parcel.writeBoolean(isSelect)


    }

    companion object CREATOR : Parcelable.Creator<Chat> {

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }


}