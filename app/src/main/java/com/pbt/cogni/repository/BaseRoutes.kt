package com.pbt.cogni.repository

import com.google.gson.annotations.SerializedName
import com.pbt.cogni.model.Routes

data class BaseRoutes (
    @SerializedName("result")
    var listRoutes : List<Routes>)