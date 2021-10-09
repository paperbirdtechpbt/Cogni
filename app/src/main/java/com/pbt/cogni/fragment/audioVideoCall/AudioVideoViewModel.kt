package com.pbt.cogni.fragment.audioVideoCall

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.map.Data
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.model.BaseAnalystModel
import com.pbt.cogni.util.MyPreferencesHelper
import retrofit2.Call
import retrofit2.Response

class AudioVideoViewModel : ViewModel() {
    lateinit var liveDataList: MutableLiveData<HttpResponse>
    companion object {

        var result: ArrayList<Resultt>? = null
        var myResult: Data? = null
    }

    init {
        liveDataList = MutableLiveData()

    }

    fun getLiveDataObserver(): MutableLiveData<HttpResponse> {
        return liveDataList
    }
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun oncall() {
        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.getAnalystList("58", "5", "analystanalyst@gmail.com")
        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(
                call: Call<HttpResponse>,
                response: Response<HttpResponse>
            ) {
                liveDataList.postValue(response.body())
                var httpResponse = response.body()

                myResult = Gson().fromJson(Gson().toJson(httpResponse?.data), Data::class.java)
                result = Gson().fromJson(
                    Gson().toJson(myResult?.mydata),
                    ArrayList<Resultt>()::class.java
                )
                Log.d("####", "data-----${myResult?.mydata}" + "\n" + "result---${result}")
//             result = Gson().fromJson(Gson().toJson(myResult?.mydata), ArrayList<Resultt>()::class.java)

            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {

            }

        })


    }

    fun sendCall(call : Boolean,id : String,sendername:String,context : Context) {
        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.makeCall(getRandomString(15), MyPreferencesHelper.getUser(context)!!.Mobile, call.toString(),id,
        sendername)
        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(
                call: Call<HttpResponse>,
                response: Response<HttpResponse>
            ) {
                Log.d("####makeCAllresponse",response.body().toString())
            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                Log.d("####makeCAllresponse",t.message.toString())
            }

        })  }
}


