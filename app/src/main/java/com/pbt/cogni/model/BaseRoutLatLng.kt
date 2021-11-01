package com.pbt.cogni.model

import com.google.gson.annotations.SerializedName

data class BaseRoutLatLng(
    @SerializedName("result") val listLatLng: List<RoutLatLng>
)
