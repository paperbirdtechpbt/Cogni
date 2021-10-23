package com.pbt.cogni.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class Expense(
    @SerializedName("Id")  val id: Int,
    @SerializedName("ExpenseType")  val expenseType: String,
    @SerializedName("Price") val amount: String,
    @SerializedName("Image") val image: String,
    @SerializedName("Description") val description: String,
    @SerializedName("ExpenseTypeId") val ExpenseTypeId: String,
    @SerializedName("Routeid") val Routeid: String,
    @SerializedName("CreatedOn") val CreatedOn: String,
    @SerializedName("CreatedBy") val CreatedBy: String,
)
