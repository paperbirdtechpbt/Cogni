package com.pbt.cogni.repository

import com.pbt.cogni.WebService.Api
import com.pbt.cogni.WebService.ApiRequest
import retrofit2.http.Field

class AnalystRepository(private  var api : Api): ApiRequest(){

    suspend fun getAnylyst( companyId: String,roleId: String, userName: String) = apiRequest { api.getAnylyst(companyId,roleId,userName)}

}
//data class ResponseDataClass(
//    @SerializedName("error")  val code: Boolean,
//    @SerializedName("msg")  val message: String,
//    @SerializedName("result") val data: JsonObject
//)