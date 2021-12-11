package com.pbt.cogni.fragment.finishTrip

import android.R.attr.data
import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.Routes
import com.pbt.cogni.repository.BaseRoutes
import com.pbt.cogni.util.AppConstant
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FinishTripViewModel : ViewModel(), Callback<HttpResponse> {

    var routesList = MutableLiveData<List<Routes>>()
    var notDataLayout:RelativeLayout?=null



    companion object {
        private var TAG: String = "FinishTripViewModel"
    }

    init {
        routesList = MutableLiveData<List<Routes>>()
    }


    fun onRouteListRequest(context: Context) {
        ApiClient.client.create(ApiInterface::class.java)
            .assignRequestList(MyPreferencesHelper.getUser(context)!!.id).enqueue(this)
    }


    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        if (response.body()?.code == false) {


            AppUtils.logDebug(TAG, " Response : " + response.body()!!.data.toString())
            val baseList: BaseRoutes = Gson().fromJson(
                response.body()?.data.toString(),
                BaseRoutes::class.java
            )


            val curremtTripList = ArrayList<Routes>()
            baseList.listRoutes.forEach { routes ->
//                if (routes.status.equals(AppConstant.CONST_STOP_TRIP)) {
//                    curremtTripList.add(routes)
//                }
                if (routes.status.equals("3")) {
                    curremtTripList.add(routes)
                }
            }

            if(curremtTripList.isNullOrEmpty()){
                notDataLayout?.visibility=View.VISIBLE

            }
            else{
                notDataLayout?.visibility=View.GONE
                routesList.value = curremtTripList
            }

        }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        AppUtils.logError(TAG, " onFailure : " + t?.message)

    }
}


