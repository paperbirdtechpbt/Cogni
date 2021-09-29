package com.pbt.cogni.activity.login

import com.google.gson.annotations.SerializedName

data class Users (

    @SerializedName("Status") var Status : Int,
    @SerializedName("Id") var Id : Int,
    @SerializedName("UserName") var UserName : String,
    @SerializedName("FirstName") var FirstName : String,
    @SerializedName("LastName") var LastName : String,
    @SerializedName("Mobile") var Mobile : String,
    @SerializedName("Landline") var Landline : String,
    @SerializedName("companyId") var companyId : Int,
    @SerializedName("companyname") var companyname : String,
    @SerializedName("RoleId") var RoleId : String,
    @SerializedName("Rolename") var Rolename : String,
    @SerializedName("Type") var Type : Int
)
