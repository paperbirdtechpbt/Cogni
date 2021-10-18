package com.pbt.cogni.fragment.ViewRoute

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.WebService.Coroutines
import com.pbt.cogni.activity.call.CallActivity
import com.pbt.cogni.activity.map.Data
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.model.*
import com.pbt.cogni.repository.AnalystRepository
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Response

class ViewRouteFragementViewModel:ViewModel(){
    lateinit var liveDataList: MutableLiveData<HttpResponse>
    companion object {

        var result: ArrayList<Resultt>? = null
        var myDAta: com.pbt.cogni.model.Data? = null
    }

    init {
        liveDataList = MutableLiveData()

    }

    fun getLiveDataObserver(): MutableLiveData<HttpResponse> {
        return liveDataList
    }


    fun onRouteListRequest(context: Context) {
        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.getRoutes("59","2","clientrequester@gmail.com")
        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(
                call: Call<HttpResponse>,
                response: Response<HttpResponse>
            ) {
                liveDataList.postValue(response.body())

                var httpResponse = response.body()

              myDAta = Gson().fromJson(Gson().toJson(httpResponse?.data), com.pbt.cogni.model.Data::class.java)
                result = Gson().fromJson(
                    Gson().toJson(myDAta?.result),
                    ArrayList<Resultt>()::class.java
                )
                Log.d("####", "result---${result}")
//             result = Gson().fromJson(Gson().toJson(myResult?.mydata), ArrayList<Resultt>()::class.java)

            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
Log.d("##routelist","api faile"+t.message)
            }

        })

    }


}


