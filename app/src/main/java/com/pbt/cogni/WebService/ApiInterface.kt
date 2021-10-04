package com.pbt.cogni.WebService

import com.pbt.cogni.model.HttpResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("index.php/GoogleMap/GetNearest")

    fun sentOriginDest(
        @Field("startCityName") startcity: String,
        @Field("endCityName") endcity: String
    ): retrofit2.Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/updateToken")
     fun updateToken(@Field("id") userid: String, @Field("firebase_token") token: String) : Call<HttpResponse>



    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<HttpResponse>

//    @FormUrlEncoded
//    @POST("api/sendtoken")
//    fun generateToken(@Field("userid") userid: String, @Field("token") token: String): Call<TokenGenerateClass>

    @FormUrlEncoded
    @POST("api/Analyst")
    fun getAnalystList(
        @Field("companyId")companyid:String,
        @Field("RoleId")RoleId:String,
        @Field("UserName")UserName:String,
    ):retrofit2.Call<HttpResponse>
}