package com.pbt.cogni.fragment.Current

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.home.MyLocationService
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.Routes
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.repository.BaseRoutes
import com.pbt.cogni.util.AppConstant.Companion.CONST_START_TRIP
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.measureTimeMillis

class CurrentTripViewModel : ViewModel(), Callback<HttpResponse> {

    var routesList = MutableLiveData<List<Routes>>()
    var user: UserDetailsData? = null
    var lat: Double = 1.0
    var long: Double = 1.0

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

                routesList.value = curremtTripList
            }}
            else{
             AppUtils.logDebug(TAG,"No routes Found")
            }



        }
    }

     fun fetchlocation(context: Context) {
        user = MyPreferencesHelper.getUser(context)


        val timedTask: Runnable = object : Runnable {
            override fun run() {

                    fetchLocation(context)
                    updateLatLongApi(context)


                handler.postDelayed(this, 20000)
            }
        }
        handler.post(timedTask)
    }

    private fun updateLatLongApi(context: Context) {

        ApiClient.client.create(ApiInterface::class.java)
                        .postLatLng(lat.toString(), long.toString(), user!!.id).enqueue(this@CurrentTripViewModel)

    }

    private fun fetchLocation(context: Context) {
         var locationrequest: LocationRequest
         var fusedLocationProviderClient: FusedLocationProviderClient
        locationrequest = LocationRequest()
        locationrequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationrequest.interval = 10000
        locationrequest.fastestInterval = 10000
        locationrequest.smallestDisplacement = 2F

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationrequest, getPendingIntent(context))
        val task = fusedLocationProviderClient.lastLocation
        Log.d("task", task.toString())

        task.addOnSuccessListener {


            if (it != null) {
                lat = it.latitude
                long = it.longitude

                AppUtils.logDebug(TAG,"lat-----${lat},  long-----${long}")

            } else {

                AppUtils.logDebug(TAG,"Please  Enable Location")

            }
        }
    }

      private fun getPendingIntent(context: Context): PendingIntent? {

        val intent = Intent(context, MyLocationService::class.java)
        intent.setAction(MyLocationService.ACTION_UPDATES)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        AppUtils.logError(TAG, " onFailure : " + t.message)
    }
}


