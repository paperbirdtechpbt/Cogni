package com.pbt.cogni.model

data class AnalystModel(
    val AnalysisDuration: String,
    val CreatedBy: String,
    val CreatedOn: String,
    val EndCity: String,
    val EndLocation: String,
    val Id: Int,
    val MSTCompanyId: Int,
    val MSTRequestTypeId: Int,
    val MSTRouteId: Any,
    val MSTStatusID: Int,
    val Name: String,
    val RequestAutoID: String,
    val StartCity: String,
    val StartDate: String,
    val StartLocation: String,
    val StartTime: String,
    val companyName: String,
    val statusName: String
)