package com.pbt.cogni.fragment.audioVideoCall

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.map.Data
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.activity.map.Resultt
import retrofit2.Call
import retrofit2.Response

class AudioVideoViewModel : ViewModel() {
    lateinit var liveDataList: MutableLiveData<HttpResponse>


    companion object {

        var result: Resultt? = null
        var myResult: Data? = null
    }
    init {
        liveDataList = MutableLiveData()

    }

    fun getLiveDataObserver(): MutableLiveData<HttpResponse> {
        return liveDataList
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

                val responseDataClass = response.body()
                myResult = Gson().fromJson(Gson().toJson(responseDataClass?.data), Data::class.java)
                val reulst: ArrayList<Resultt>? = Gson().fromJson(
                    Gson().toJson(myResult?.mydata),
                    ArrayList<Resultt>()::class.java
                )


            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {

            }

        })


    }
    fun makeCall(boolean: Boolean){

        if (boolean){
          Toast.makeText(Application(),"true",Toast.LENGTH_SHORT).show()
        }
    }
}


