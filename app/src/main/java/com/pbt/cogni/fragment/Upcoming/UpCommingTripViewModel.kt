package com.pbt.cogni.fragment.Upcoming

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.Routes
import com.pbt.cogni.repository.BaseRoutes
import com.pbt.cogni.util.AppConstant.Companion.CONST_ASSIGN_TRIP
import com.pbt.cogni.util.AppConstant.Companion.CONST_UPCOMMING_TRIP
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.android.synthetic.main.fragment_current.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpCommingTripViewModel : ViewModel(), Callback<HttpResponse> {

    var routesList  = MutableLiveData<List<Routes>>()


    companion object {
        private var TAG : String = "UpCommingTripViewModel"
    }

    init {
        routesList = MutableLiveData<List<Routes>>()
    }

     fun onRouteListRequest(context: Context) {
        ApiClient.client.create(ApiInterface::class.java).assignRequestList(MyPreferencesHelper.getUser(context)!!.id).enqueue(this)
    }

    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        if(response?.body()?.code == false){

            AppUtils.logDebug(TAG,  " Response : " +response?.body())
            var baseList : BaseRoutes =  Gson().fromJson(response?.body()?.data.toString(),
                BaseRoutes::class.java)

            AppUtils.logDebug(TAG,"Routes Upcomming Trip Status "+response?.body()?.data.toString())
            routesList.value = emptyList()
            var upCommingTripList = ArrayList<Routes>()
            baseList.listRoutes.forEach { routes ->

                if (routes.status.equals(CONST_ASSIGN_TRIP)) {
                    upCommingTripList.add(routes)
                }
            }
            routesList.value = upCommingTripList

        }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {

        AppUtils.logError(TAG,  " onFailure : " + t?.message)
    }
}


