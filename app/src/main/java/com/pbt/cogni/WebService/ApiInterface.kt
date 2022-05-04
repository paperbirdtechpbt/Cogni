package com.pbt.cogni.WebService

import com.pbt.cogni.model.HttpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

//    @FormUrlEncoded
//    @POST("index.php/GoogleMap/GetNearest")
//    fun sentOriginDest(
//        @Field("startCityName") startcity: String,
//        @Field("endCityName") endcity: String
//    ): retrofit2.Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/updateToken")
    fun updateToken(
        @Field("id") userid: String,
        @Field("firebase_token") token: String
    ): Call<HttpResponse>


    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/getWayPoint")
    fun getWayPoint(@Field("routId") routId: String): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/updateLatLng")
      fun postLatLng(
        @Field("lat") lat: Double,
        @Field("lng") lng: Double,
        @Field("id") id: String): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/Analyst")
    fun getAnalystList(
        @Field("companyId") companyid: String,
        @Field("RoleId") RoleId: String,
        @Field("UserName") UserName: String,
    ): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/addNewMetting")
    fun makeCall(
        @Field("senderId") senderId: String,
        @Field("receiverId") receiverId: String,
        @Field("room") room: String,
        @Field("type") type: String,
        @Field("typeofcall") typeofcall: String,
    ): Call<HttpResponse>

//    @FormUrlEncoded
//    @POST("api/request")
//    fun getRoutes(
//        @Field("companyId") companyId: String,
//        @Field("roleId") roleId: String,
//        @Field("userName") userName: String
//    ): Call<HttpResponse>

    @Multipart
    @POST("api/addExpense")
      fun addExpense(
        @Part("routeid") id: RequestBody,
        @Part("price") price: RequestBody,
        @Part("description") descrition: RequestBody,
        @Part("expenseType") expenseType: RequestBody,
        @Part("expenseTypeId") expenseTypeId: RequestBody,
        @Part("createdBy") createdBy: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/getExpense")
     fun getExpense(
        @Field("routeid") roleId: String,
    ): Call<HttpResponse>

     @FormUrlEncoded
    @POST("api/updateSos")
     fun getSOSstatus(
        @Field("id") userid: String,
        @Field("sos") sos: Int,
    ): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/assignRequestList")
     fun assignRequestList(
        @Field("id") id: String,
    ): Call<HttpResponse>

//    @Multipart
//    @POST("api/uploadImage")
//     fun uploadImage(@Part file: MultipartBody.Part): retrofit2.Call<JsonObject>

     @Multipart
    @POST("api/uploadImage")
     fun uploadImage(@Part file: MultipartBody.Part): Call<HttpResponse>

    @FormUrlEncoded
    @POST("api/updateTrip")
     fun updateTripStatus(@Field("id") id: String, @Field("status") status: String): Call<HttpResponse>
}