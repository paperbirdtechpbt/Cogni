package com.pbt.cogni.WebService

import com.pbt.cogni.util.AppUtils
import retrofit2.Response
import java.io.IOException

abstract  class ApiRequest {
    suspend fun<T:Any> apiRequest(call : suspend ()-> Response<T>) : T {
         val response  = call.invoke()
        if(response.isSuccessful) {
            AppUtils.logDebug("ApiRequest", " Resposne  : "+response.body())
            return response.body()!!
        }else{
             throw  ApiException(response.toString())
        }
    }
}

class ApiException(message : String) : IOException(message)