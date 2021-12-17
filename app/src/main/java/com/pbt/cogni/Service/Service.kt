package com.pbt.cogni.activity.home

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.pbt.cogni.fragment.Current.BroadClassRecicver
import com.pbt.cogni.util.AppUtils
import kotlinx.coroutines.channels.BroadcastChannel


class service : Service() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationrequest: LocationRequest
var TAG="Service"
    var lat: Double = 1.0
    var long: Double = 1.0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("####onbroadcastreviever","recived Broadcast")

            getLatlong()
        val handler = Handler()

        val timedTask: Runnable = object : Runnable {
            override fun run() {
                fetchLocation()
                handler.postDelayed(this, 180000) }
        }
        handler.post(timedTask)
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           getLatlong()
            val handler = Handler()

            val timedTask: Runnable = object : Runnable {
                override fun run() {

                    fetchLocation()
                    handler.postDelayed(this, 180000) }
            }
            handler.post(timedTask)
        }
        else{
            Log.d("####oncreatesevice","in on Create Service class")
            val handler = Handler()

            val timedTask: Runnable = object : Runnable {
                override fun run() {
                    fetchLocation()

                    handler.postDelayed(this, 180000) }
            }
            handler.post(timedTask)
            getLatlong()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

    }


   private fun getLatlong() {
        val handler = Handler()

        val timedTask: Runnable = object : Runnable {
            override fun run() {
                fetchLocation()

                handler.postDelayed(this, 60000) }
        }
        handler.post(timedTask)
    }

     private fun fetchLocation() {
        locationrequest = LocationRequest()
        locationrequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationrequest.interval = 100000
        locationrequest.fastestInterval = 100000
        locationrequest.smallestDisplacement = 3F

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationrequest, getPendingIntent())
        val task = fusedLocationProviderClient.lastLocation
//        Log.d("task", task.toString())

        task.addOnSuccessListener {

            if (it != null) {

                lat = it.latitude
                long = it.longitude
               AppUtils.logDebug(TAG," In Service Class  lat->>>$lat  long-->>>>$long")

//                Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()

            }
            else{
//                Toast.makeText(this, "Lat long is null", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPendingIntent(): PendingIntent? {

        val intent = Intent(this, BroadClassRecicver()::class.java)
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }



}
