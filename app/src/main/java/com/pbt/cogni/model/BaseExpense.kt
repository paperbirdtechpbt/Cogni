package com.pbt.cogni.model

import com.google.gson.annotations.SerializedName

data class BaseExpense(@SerializedName("result") val listExpense: List<Expense>)
