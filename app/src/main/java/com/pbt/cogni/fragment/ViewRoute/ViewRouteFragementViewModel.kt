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
import com.pbt.cogni.repository.BaseRoutes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import com.pbt.cogni.viewModel.LoginViewModel
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewRouteFragementViewModel:ViewModel(), Callback<HttpResponse> {

    var routesList  = MutableLiveData<List<Routes>>()

    companion object {
        private var TAG : String = "ViewRouteFragment"
    }

    init {
        routesList = MutableLiveData<List<Routes>>()
    }



    fun onRouteListRequest(context: Context) {
        ApiClient.client.create(ApiInterface::class.java).getRoutes(MyPreferencesHelper.getUser(context)!!.companyId,
            MyPreferencesHelper.getUser(context)!!.RoleId,
            MyPreferencesHelper.getUser(context)!!.UserName).enqueue(this)
    }


    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {

        if(response?.body()?.code == false){
           var baseList :  BaseRoutes =  Gson().fromJson(response?.body()?.data.toString(),BaseRoutes::class.java)
            routesList.value = baseList.listRoutes
            AppUtils.logDebug(TAG,  " Response : " + Gson().toJson(baseList.listRoutes))
        }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        AppUtils.logError(TAG,  " onFailure : " + t?.message)
    }
}


