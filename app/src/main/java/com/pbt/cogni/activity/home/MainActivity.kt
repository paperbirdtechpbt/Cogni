package com.pbt.cogni.activity.home

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.TabLayout.TabLayoutFragment
import com.pbt.cogni.fragment.Chat.UserChatListFragment
import com.pbt.cogni.fragment.Profile.ProfileFragment
import com.pbt.cogni.fragment.ViewRoute.ViewRouteFragement
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoFragement
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.util.AppConstant.Companion.PREFF_OVERYLAY_PERMISSION
import com.pbt.cogni.util.MyPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), Callback<HttpResponse> {

    var bottomNavigation: BottomNavigationView? = null
    lateinit var chatsFragement: UserChatListFragment
    lateinit var audioVideoFragement: AudioVideoFragement
    lateinit var viewRouteFragement: ViewRouteFragement
    lateinit var tabLayoutFragment: TabLayoutFragment
    lateinit var profileFragement: ProfileFragment
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var user: UserDetailsData? = null

    lateinit var locationrequest: LocationRequest


    companion object {
        var instance: MainActivity? = null
            get() = instance
        var lat: Double = 1.0
        var long: Double = 1.0
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = MyPreferencesHelper.getUser(this)

        setContentView(R.layout.activity_main)

        fetchlocation()

        val overlaypermission =
            MyPreferencesHelper.getStringValue(this, PREFF_OVERYLAY_PERMISSION, null)
        if (overlaypermission == null) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            MyPreferencesHelper.setStringValue(this, PREFF_OVERYLAY_PERMISSION, "true")

        }
        getSupportActionBar()?.setTitle("Analyst Routes List ")

        if (savedInstanceState == null) {
            if (chechpermission()) {

            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.framelayout, ViewRouteFragement()).commit()
            bottomNavigation = findViewById(R.id.bottom_navigation)
            bottomNavigation?.getMenu()?.get(0)?.setChecked(true)
        }
        bottomNavigation?.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.profile -> {
                    getSupportActionBar()?.setTitle("Profile")

                    profileFragement = ProfileFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, profileFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.chat -> {

                    getSupportActionBar()?.setTitle("Chats")
                    supportActionBar?.show()


                    chatsFragement = UserChatListFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, chatsFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.audiovideo -> {
                    getSupportActionBar()?.setTitle("Audio Video ")

                    supportActionBar?.show()


                    audioVideoFragement = AudioVideoFragement()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, audioVideoFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.viewroute -> {
                    getSupportActionBar()?.setTitle("Analyst Routes List ")
                    supportActionBar?.show()

                    viewRouteFragement = ViewRouteFragement()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, viewRouteFragement)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.test -> {

                    getSupportActionBar()?.setTitle("Analyst Routes List ")
                    supportActionBar?.show()

                    tabLayoutFragment = TabLayoutFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.framelayout, tabLayoutFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
            }
            true
        }
    }

    private fun fetchlocation() {
        val handler = Handler()

        val timedTask: Runnable = object : Runnable {
            override fun run() {
                fetchLocation()
                updateLatLongApi()

                handler.postDelayed(this, 15000)
            }
        }
        handler.post(timedTask)
    }

    private fun fetchLocation() {
        locationrequest = LocationRequest()
        locationrequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationrequest.interval = 15000
        locationrequest.fastestInterval = 15000
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
        Log.d("task", task.toString())

        task.addOnSuccessListener {

            if (it != null) {

                lat = it.latitude
                long = it.longitude
                Toast.makeText(this, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
                Log.d("##asdd", "${it.latitude} ${it.longitude}")

                Toast.makeText(
                    applicationContext,
                    "${it.latitude} ${it.longitude}",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(this, "Please Enable Location", Toast.LENGTH_SHORT).show()


            }
        }
    }

    private fun getPendingIntent(): PendingIntent? {

        val intent = Intent(this, MyLocationService::class.java)
        intent.setAction(MyLocationService.ACTION_UPDATES)
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }


    private fun updateLatLongApi() {
        ApiClient.client.create(ApiInterface::class.java)
            .postLatLng(lat.toString(), long.toString(), user!!.id).enqueue(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//        super.onDestroy()
//        val serviceIntent = Intent(this, service::class.java)
//        ContextCompat.startForegroundService(this, serviceIntent)

        val broadcastIntent = Intent(this, MyLocationService::class.java)
        sendBroadcast(broadcastIntent)


    }


    private fun chechpermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.VIBRATE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                ), 100
            )

        }
        return true
    }

    override fun onResponse(call: Call<HttpResponse>, response: Response<HttpResponse>) {

        Toast.makeText(this, "ResponseSucessFull", Toast.LENGTH_LONG).show()
    }

    override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
        Toast.makeText(this, "${t.message}", Toast.LENGTH_LONG).show()
    }


}