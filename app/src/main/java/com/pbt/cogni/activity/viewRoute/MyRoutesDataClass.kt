package com.pbt.cogni.activity.viewRoute

import com.google.android.gms.maps.model.LatLng

data class MyRoutesDataClass(

    var origin: String,
    var destination: String,
    var startLatLong : LatLng,
    var endLatLong : LatLng
)


