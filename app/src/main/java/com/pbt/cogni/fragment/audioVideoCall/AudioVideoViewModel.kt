package com.pbt.cogni.fragment.audioVideoCall

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.AccountPicker
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.MapsActivity
import com.pbt.cogni.activity.map.Data
import com.pbt.cogni.activity.map.ResponseDataClass
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.repository.AnalystRepo
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class AudioVideoViewModel : ViewModel() {
    lateinit var liveDataList: MutableLiveData<ResponseDataClass>


    companion object {

        var result: Resultt? = null
        var myResult: Data? = null
    }
    init {
        liveDataList = MutableLiveData()

    }

    fun getLiveDataObserver(): MutableLiveData<ResponseDataClass> {
        return liveDataList
    }


    fun oncall() {
        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.getAnalystList("58", "5", "analystanalyst@gmail.com")
        call?.enqueue(object : retrofit2.Callback<ResponseDataClass> {
            override fun onResponse(
                call: Call<ResponseDataClass>,
                response: Response<ResponseDataClass>
            ) {
                liveDataList.postValue(response.body())

                val responseDataClass = response.body()
                myResult = Gson().fromJson(Gson().toJson(responseDataClass?.data), Data::class.java)
                val reulst: ArrayList<Resultt>? = Gson().fromJson(
                    Gson().toJson(myResult?.mydata),
                    ArrayList<Resultt>()::class.java
                )


            }

            override fun onFailure(call: Call<ResponseDataClass>, t: Throwable) {

            }

        })


    }
    fun makeCall(boolean: Boolean){

        if (boolean){
          Toast.makeText(Application(),"true",Toast.LENGTH_SHORT).show()
        }
    }
}


