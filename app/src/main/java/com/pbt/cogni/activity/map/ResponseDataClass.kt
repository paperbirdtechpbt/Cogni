package com.pbt.cogni.activity.map

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.*

data class ResponseDataClass(
    @SerializedName("error")  val code: Boolean,
    @SerializedName("msg")  val message: String,
    @SerializedName("data") val data: Data
)
