package com.pbt.cogni.model

import com.google.gson.annotations.SerializedName

data class Routes(
    @SerializedName("AnalysisDuration")val AnalysisDuration: String,
    @SerializedName("CreatedBy") val CreatedBy: String,
    @SerializedName("CreatedOn") val CreatedOn: String,
    @SerializedName("EndCity") val EndCity: String,
    @SerializedName("EndLocation") val EndLocation: String,
    @SerializedName("Id")  val Id: Int,
    @SerializedName("assignId") val assignId: String,
    @SerializedName("MSTCompanyId") val MSTCompanyId: Int,
    @SerializedName("MSTRequestTypeId") val MSTRequestTypeId: Int,
    @SerializedName("MSTRouteId") val MSTRouteId: String,
    @SerializedName("MSTStatusID") val MSTStatusID: Int,
    @SerializedName("Name") val Name: String,
    @SerializedName("RequestAutoID") val RequestAutoID: String,
    @SerializedName("StartCity") val StartCity: String,
    @SerializedName("StartDate") val StartDate: String,
    @SerializedName("StartLocation") val StartLocation: String,
    @SerializedName("StartTime") val StartTime: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("StartLat") val startLat: String,
    @SerializedName("StartLong") val startLong: String,
    @SerializedName("EndLat") val endLat: String,
    @SerializedName("EndLong") val endLong: String,
    @SerializedName("clientCompanyName") val clientCompanyName: String,
    @SerializedName("statusName") val statusName: String,
    @SerializedName("Status") val status: String
)