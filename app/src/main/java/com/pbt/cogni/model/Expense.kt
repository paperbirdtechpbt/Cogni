package com.pbt.cogni.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("id")  val id: Int,
    @SerializedName("expenseType")  val expenseType: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("image") val image: String,
    @SerializedName("description") val description: String,

)
