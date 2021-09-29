package com.pbt.cogni.activity.login

import com.google.gson.annotations.SerializedName

class UserDetailsData (
    @SerializedName("Status") val status:String,
    @SerializedName("Id") val id:String,
    @SerializedName("UserName") val UserName:String,
    @SerializedName("FirstName") val FirstName:String,
    @SerializedName("LastName") val LastName:String,
    @SerializedName("Mobile") val Mobile:String,
    @SerializedName("Landline") val Landline:String,
    @SerializedName("companyId") val companyId:String,
    @SerializedName("RoleId") val RoleId:String,
    @SerializedName("Rolename") val Rolename:String,
    @SerializedName("msg") val msg:String,
    @SerializedName("Type") val Type:String,
        )
