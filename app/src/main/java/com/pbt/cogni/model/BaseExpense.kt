package com.pbt.cogni.model

import com.google.gson.annotations.SerializedName

data class BaseExpense(
    @SerializedName("result") val listExpense: List<Expense>,
    @SerializedName("image") val image: String,
    @SerializedName("orignal") val orignal: String,
    @SerializedName("fileName") val fileName: String,
    @SerializedName("extension") val extension: String,
    )
