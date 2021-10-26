package com.pbt.cogni.activity.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pbt.cogni.R
import com.pbt.cogni.fragment.Chat.UserChatListFragment
import com.pbt.cogni.fragment.Profile.ProfileFragment
import com.pbt.cogni.fragment.ViewRoute.ViewRouteFragement
import com.pbt.cogni.fragment.audioVideoCall.AudioVideoFragement
import com.pbt.cogni.util.AppConstant.Companion.PREFF_OVERYLAY_PERMISSION
import com.pbt.cogni.util.MyPreferencesHelper


class MainActivity : AppCompatActivity() {

     var bottomNavigation: BottomNavigationView?=null
    lateinit var chatsFragement: UserChatListFragment
    lateinit var audioVideoFragement: AudioVideoFragement
    lateinit var viewRouteFragement: ViewRouteFragement
    lateinit var profileFragement: ProfileFragment
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var locationrequest: LocationRequest

    var lat: Double = 1.0
    var long: Double = 1.0

    companion object {
        var instance: MainActivity? = null
            get() = instance
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, MyLocationService::class.java)
        this.sendBroadcast(broadcastIntent)


       val overlaypermission= MyPreferencesHelper.getStringValue(this,PREFF_OVERYLAY_PERMISSION,null)
        if (overlaypermission==null){
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            MyPreferencesHelper.setStringValue(this, PREFF_OVERYLAY_PERMISSION,"true")

        }
        getSupportActionBar()?.setTitle("Analyst Routes List ")

//        service().getLatlong()
//        startService(Intent(this,service::class.java))



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
            }
            true }
    }

//    override fun onDestroy() {
//
//        Log.d("####ondestroy","on Destroy In MainActivity")
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartservice"
//        broadcastIntent.setClass(this, MyLocationService::class.java)
//        this.sendBroadcast(broadcastIntent)
//        super.onDestroy()
//    }


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


}