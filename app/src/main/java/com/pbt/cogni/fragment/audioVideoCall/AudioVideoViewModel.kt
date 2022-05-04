package com.pbt.cogni.fragment.audioVideoCall

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.call.CallActivity
import com.pbt.cogni.activity.map.Data
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.activity.map.Resultt
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import retrofit2.Call
import retrofit2.Response
import java.security.SecureRandom

class AudioVideoViewModel : ViewModel() {
     var liveDataList: MutableLiveData<HttpResponse>

     companion object {
        var result: ArrayList<Resultt>? = null
        var myResult: Data? = null
         val TAG="AudioVideoViewModel"
         var myRoomId=""
    }

    init {
        liveDataList = MutableLiveData()
    }

    fun getLiveDataObserver(): MutableLiveData<HttpResponse> {
        return liveDataList
    }


    fun oncall(context: Context) {
        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.getAnalystList(MyPreferencesHelper.getUser(context)!!.companyId, MyPreferencesHelper.getUser(context)!!.RoleId, MyPreferencesHelper.getUser(context)!!.UserName)
        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(
                call: Call<HttpResponse>,
                response: Response<HttpResponse>
            ) {
                liveDataList.postValue(response.body())

                val httpResponse = response.body()
                if (httpResponse?.data?.get("result")==null){
                    AppUtils.logDebug(TAG,"Data Not Found or Result is Null")
                    Toast.makeText(context,"No data Available",Toast.LENGTH_SHORT).show()
                }
                else{

                    myResult = Gson().fromJson(Gson().toJson(httpResponse?.data), Data::class.java)
                    result = Gson().fromJson(
                        Gson().toJson(myResult?.mydata),
                        ArrayList<Resultt>()::class.java
                    )
                    Log.d("####", "data-----${myResult?.mydata}" + "\n" + "result---${result}")
                }

//             result = Gson().fromJson(Gson().toJson(myResult?.mydata), ArrayList<Resultt>()::class.java)

            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
            }
        })

    }
    fun JitsiCall(context: Context){
        context.startActivity(Intent(context,CallActivity::class.java))
    }

    fun sendCall(context:Context,sender:String,reciver:String,room:String,type:String,typeofcall:String) {

        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
       myRoomId=getRandomString(8)
        AppUtils.logDebug(TAG,"MyRoomId"+ room)
        val call = apiInterface?.makeCall(sender,reciver, room,type,typeofcall)
        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(
                call: Call<HttpResponse>,
                response: Response<HttpResponse>
            ) {
                 AppUtils.logDebug(TAG, "Call Successfull")
                val intent=Intent(context,CallActivity::class.java)
                intent.putExtra("makecallRoomId",room)
                intent.putExtra("sendid",sender)
                intent.putExtra("reciverid",reciver)
                intent.putExtra("typeofcall",typeofcall)

                if (typeofcall=="video"){
                    intent.putExtra("isVideoCall",true)

                }
                if (typeofcall=="audio"){
                    intent.putExtra("isVideoCall",false)

                }
                context.startActivity(intent)
            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                Log.d("####makeCAllresponse",t.message.toString())
            }


        })  }

    fun getRandomString(length: Int) : String {
//        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val allowedChars = ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}


