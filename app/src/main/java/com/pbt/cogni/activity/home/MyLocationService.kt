package com.pbt.cogni.activity.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult
import java.lang.StringBuilder
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast


class MyLocationService:BroadcastReceiver() {

    companion object {
        val ACTION_UPDATES = "com.pbt.cogni.activity.home.UPDATE_LOCATION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!=null) {
            val stopsrevice = intent.extras?.getString("stopservice")
            if (!stopsrevice.isNullOrEmpty()) {
                Log.d("MyLocationService","in stopervice")
                context!!.stopService(Intent(context, service::class.java))
            }
else
            {
            val handler = Handler()

            val timedTask: Runnable = object : Runnable {
                override fun run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context!!.startService(Intent(context, service::class.java))
                    else
                        context!!.startService(Intent(context, service::class.java))
                    handler.postDelayed(this, 180000)
                }
            }
            handler.post(timedTask)

        }
        }
        else{
            val handler = Handler()

            val timedTask: Runnable = object : Runnable {
                override fun run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context!!.startService(Intent(context, service::class.java))
                    else
                        context!!.startService(Intent(context, service::class.java))
                    handler.postDelayed(this, 180000)
                }
            }
            handler.post(timedTask)
        }
    }

}
