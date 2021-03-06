package com.pbt.cogni.WebService

import com.pbt.cogni.activity.map.ResponseDataClass
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {


    @FormUrlEncoded
    @POST("api/Analyst")
    suspend fun getAnylyst(@Field("companyId") companyId: String, @Field("RoleId") RoleId: String, @Field("UserName") UserName: String) : Response<ResponseDataClass>


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