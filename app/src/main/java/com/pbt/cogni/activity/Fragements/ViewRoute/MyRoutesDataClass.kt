package com.pbt.cogni.activity.Fragements.ViewRoute

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject

data class MyRoutesDataClass(

    var origin: String,
    var destination: String,
    var startLatLong : LatLng,
    var endLatLong : LatLng
)


