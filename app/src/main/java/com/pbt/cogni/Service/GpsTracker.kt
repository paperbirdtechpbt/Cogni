package com.pbt.cogni.fragment.Current

import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GPSTracker : Service(), LocationListener, Callback<HttpResponse?> {



    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location : Location? = null

    var latitude :Double?= 0.0
    var longitude:Double ?= 0.0

    // The minimum time between updates in milliseconds
    //    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    @JvmName("getLocation1")
    @SuppressLint("MissingPermission")
    fun getLocation() {
        try {
            AppUtils.logError(TAG,"In TRy method")
            locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            AppUtils.logError(TAG,"------$e")
        }

    }
//   fun getLocation(): Location? {
//        try {
//            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
//            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
//
//            // getting network status
//            isNetworkEnabled = locationManager!!
//                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                // no network provider is enabled
//            } else {
//                canGetLocation = true
//                // First get location from Network Provider
//                if (isNetworkEnabled) {
//                    locationManager!!.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER,
//                        0,
//                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
//                    )
//                    Log.d("Network", "Network")
//                    if (locationManager != null) {
//                        location = locationManager!!
//                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                        if (location != null) {
//                            latitude = location!!.latitude
//                            longitude = location!!.longitude
//                        }
//                    }
//                }
//
//                // if GPS Enabled get lat/long using GPS Services
//                if (isGPSEnabled) {
//                    if (location == null) {
//                        locationManager!!.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER,
//                            0,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this
//                        )
//                        Log.d("GPS Enabled", "GPS Enabled")
//                        if (locationManager != null) {
//                            location = locationManager!!
//                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                            if (location != null) {
//                                latitude = location!!.latitude
//                                longitude = location!!.longitude
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show()
//        }
//        return location
//    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude!!
    }

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude!!
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    fun showSettingsAlert() {
//        val alertDialog = AlertDialog.Builder(mContext)
//        alertDialog.setTitle("GPS is settings")
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")
//        alertDialog.setPositiveButton(
//            "Settings"
//        ) { dialog, which ->
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            mContext.startActivity(intent)
//        }
//        alertDialog.setNegativeButton(
//            "Cancel"
//        ) { dialog, which -> dialog.cancel() }
//
//        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {
        Toast.makeText(applicationContext, """On Location Chnaged${location.latitude} ${location.longitude}
//     """.trimIndent(), Toast.LENGTH_LONG
        ).show()
        updateLatlong(location)
        Log.d("##GPSTracker", location.toString())
    }

    override fun onDestroy() {

        AppUtils.logDebug(TAG,"On destroy in service class")

//        val broadcastIntent = Intent(this, BroadClassRecicver::class.java)
//        broadcastIntent.putExtra("extra","extra")
//        sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

    private fun  updateLatlong(location: Location) {
        val user=MyPreferencesHelper.getUser(applicationContext)
        ApiClient.client.create(ApiInterface::class.java)
            .postLatLng(location.latitude,location.longitude, user!!.id).enqueue(this)

    }
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, "my_channel")
            .setContentTitle("Cogni")
            .setContentText("Location Update")
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(2, notification)
        getLocation()
//


        Log.i(TAG, "In onStartCommand");
        return  START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel("my_channel", "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    override fun onResponse(call: Call<HttpResponse?>, response: Response<HttpResponse?>) {
        AppUtils.logDebug("##GpsTracker","${response.body()?.message}")
//        Toast.makeText(mContext,"On response successfull",Toast.LENGTH_SHORT).show()
    }
    override fun onFailure(call: Call<HttpResponse?>, t: Throwable) {
        AppUtils.logDebug("##GpsTracker","Lat long update Fail")
//        Toast.makeText(mContext,"Error : ${t.message}",Toast.LENGTH_SHORT).show()
    }


    companion object {

            fun startService(context: Context, message: String) {
                val startIntent = Intent(context, GPSTracker::class.java)
                startIntent.putExtra("inputExtra", message)
                ContextCompat.startForegroundService(context, startIntent)
            }
            fun stopService(context: Context) {
                val stopIntent = Intent(context, GPSTracker::class.java)
                context.stopService(stopIntent)
            }

        // The minimum distance to change Updates in meters
        val TAG="mContext"
//        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 15 meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 15 meters
        private val     MIN_TIME_BW_UPDATES :Long= 1000 * 60 * 1
    }
//
//    init {
//        getLocation()
//    }
}