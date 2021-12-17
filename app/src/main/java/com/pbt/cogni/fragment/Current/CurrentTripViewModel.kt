package com.pbt.cogni.fragment.Current

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.Routes
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.repository.BaseRoutes
import com.pbt.cogni.util.AppConstant.Companion.CONST_START_TRIP
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CurrentTripViewModel : ViewModel(), Callback<HttpResponse> {

    var routesList = MutableLiveData<List<Routes>>()
    var user: UserDetailsData? = null
    var lat: Double = 1.0
    var long: Double = 1.0
    var relativelayout:RelativeLayout?=null
    val context:Context?=null


    companion object {
        val handler = Handler()
        private var TAG: String = "CurrentTripViewModel"
    }

    init {
        routesList = MutableLiveData<List<Routes>>()
    }
    fun onRouteListRequest(context: Context) {

        ApiClient.client.create(ApiInterface::class.java)
                .assignRequestList(MyPreferencesHelper.getUser(context)!!.id).enqueue(this@CurrentTripViewModel)
    }


    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {
        if (response.body()?.code == false) {
            AppUtils.logDebug(TAG, " Response : " + response.body())

            if (response.body()!!.data!=null){
                relativelayout?.visibility=View.GONE

            val baseList: BaseRoutes = Gson().fromJson(
                response.body()?.data.toString(),
                BaseRoutes::class.java
            )
            val curremtTripList = ArrayList<Routes>()
            baseList.listRoutes.forEach { routes ->
                if (routes.status.equals(CONST_START_TRIP)) {

                    curremtTripList.add(routes)
                }
            }
            if(!curremtTripList.isNullOrEmpty()){
//                GPSTracker.startService(context!!, "Foreground Service is running...")

                routesList.value = curremtTripList
            }
            else{




                AppUtils.logDebug(TAG,"No routes Found")
                relativelayout?.visibility= View.VISIBLE
            }
            }




        }
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        AppUtils.logError(TAG, " onFailure : " + t.message)
    }

     fun fetchlocation(context: Context) {
        user = MyPreferencesHelper.getUser(context)
         fetchMyLocation(context)

    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun fetchMyLocation(context: Context) {
        val gps = GPSTracker()

        if (gps.canGetLocation()) {
            val latitude: Double = gps.getLatitude()
            val longitude: Double = gps.getLongitude()
        }


    }

}


