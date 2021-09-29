package com.pbt.cogni.WebService

import com.pbt.cogni.activity.map.ResponseDataClass
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
    ): retrofit2.Call<ResponseDataClass>


    @FormUrlEncoded
    @POST("api/login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<ResponseDataClass>
}