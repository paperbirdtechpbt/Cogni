package com.pbt.cogni.activity.login

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class UserLoginClass (
    @SerializedName("error")  val code: Boolean,
    @SerializedName("message")  val message: String,
    @SerializedName("result") val result: ArrayList<UserDetailsData>
)
