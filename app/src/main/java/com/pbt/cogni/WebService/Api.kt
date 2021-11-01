package com.pbt.cogni.WebService

import com.pbt.cogni.model.HttpResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

    @FormUrlEncoded
    @POST("api/Analyst")
    suspend fun getAnylyst(@Field("companyId") companyId: String, @Field("RoleId") RoleId: String, @Field("UserName") UserName: String) : Response<HttpResponse>

    @FormUrlEncoded
    @POST("api/request")
    suspend fun getRouteList(@Field("companyId") companyId: String, @Field("roleId") RoleId: String, @Field("userName") UserName: String) : Response<HttpResponse>


    companion object{
        operator fun invoke() : Api{
          return  Retrofit.Builder()
                .baseUrl("http://cogni.paperbirdtech.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        }
    }
}