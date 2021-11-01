package com.pbt.cogni.model

import com.google.gson.annotations.SerializedName

data class RoutLatLng(
    @SerializedName("Latitude") val lat: Double,
    @SerializedName("Longitude") val long: Double)
