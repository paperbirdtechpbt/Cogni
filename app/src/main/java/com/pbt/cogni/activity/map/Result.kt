package com.pbt.cogni.activity.map

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("Id") val id: Int,
    @SerializedName("Latitude") val lat: Double,
    @SerializedName("Longitude") val lng: Double,
    @SerializedName("RouteID")val routeid:Int
)