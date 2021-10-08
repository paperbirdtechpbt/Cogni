package com.pbt.cogni.activity.map

import com.google.gson.annotations.SerializedName

data class Resultt(
    @SerializedName("Id") val id: Int,
    @SerializedName("Latitude") val lat: Double,
    @SerializedName("Longitude") val lng: Double,
    @SerializedName("RouteID")val routeid:Int,
    @SerializedName("Name")val name:String,
    @SerializedName("StartLocation")val startlocation:String,
    @SerializedName("EndLocation")val endlocation:String,
    @SerializedName("companyName")val companyName:String,
    @SerializedName("Email")val Email:String,
    @SerializedName("FirstName")val Firstname:String,
    @SerializedName("LastName")val LastName:String,
    @SerializedName("Mobile")val Mobile:String,


)