package com.pbt.cogni.fragment.Current

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
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

//
//        val timedTask: Runnable = object : Runnable {
//            override fun run() {
//              fetchMyLocation(context)
//                    fetchLocation(context)
//                    updateLatLongApi(context)
//              handler.postDelayed(this, 20000)
//            }
//        }
//        handler.post(timedTask)
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun fetchMyLocation(context: Context) {
        val gps = GPSTracker(context)

        if (gps.canGetLocation()) {
            val latitude: Double = gps.getLatitude()
            val longitude: Double = gps.getLongitude()
        }
//        else {
//            gps.showSettingsAlert()
//        }

    }

//     fun updateLatLongApi(location:Location) {
//AppUtils.logError(TAG,"location $location")
//
//        ApiClient.client.create(ApiInterface::class.java)
//                        .postLatLng(location.latitude,location.longitude, user!!.id).enqueue(this@CurrentTripViewModel)
//
//    }

//    private fun fetchLocation(context: Context) {
//         var locationrequest: LocationRequest
//         var fusedLocationProviderClient: FusedLocationProviderClient
//        locationrequest = LocationRequest()
//        locationrequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
//        locationrequest.interval = 0
////        locationrequest.fastestInterval = 10000
//        locationrequest.smallestDisplacement = 2F
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                context,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//            return
//        }
//        fusedLocationProviderClient.requestLocationUpdates(locationrequest, getPendingIntent(context))
//        val task = fusedLocationProviderClient.lastLocation
//        Log.d("task", task.toString())
//
//        task.addOnSuccessListener {
//
//            if (it != null) {
//                lat = it.latitude
//                long = it.longitude
////                Toast.makeText(context,"lat-----${lat},  long-----${long}",Toast.LENGTH_SHORT).show()
//
//                AppUtils.logDebug(TAG,"lat-----${lat},  long-----${long}")
//
//            } else {
//                AppUtils.logDebug(TAG,"Please  Enable Location")
//            }
//        }
//    }
//
//      private fun getPendingIntent(context: Context): PendingIntent? {
//
//        val intent = Intent(context, MyLocationService::class.java)
//        intent.setAction(MyLocationService.ACTION_UPDATES)
//        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//    }

}


