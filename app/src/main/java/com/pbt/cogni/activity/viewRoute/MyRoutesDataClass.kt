package com.pbt.cogni.activity.viewRoute

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
class MyRoutesDataClass(

    var origin: String,
    var destination: String,
    var startLatLong : LatLng,
    var endLatLong : LatLng
) : Parcelable


