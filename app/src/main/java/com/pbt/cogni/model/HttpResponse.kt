package com.pbt.cogni.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.*

data class HttpResponse(
    @SerializedName("error")  val code: Boolean,
    @SerializedName("msg")  val message: String,
    @SerializedName("data") val data: JsonObject
)
