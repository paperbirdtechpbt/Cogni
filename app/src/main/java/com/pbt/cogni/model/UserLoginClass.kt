package com.pbt.cogni.model

import com.google.gson.annotations.SerializedName
import com.pbt.cogni.model.UserDetailsData

class UserLoginClass (
    @SerializedName("error")  val code: Boolean,
    @SerializedName("message")  val message: String,
    @SerializedName("result") val result: ArrayList<UserDetailsData>
)
