package com.pbt.cogni.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pbt.cogni.R
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.activity.login.LoginActivity

import com.pbt.cogni.activity.splash.TokenViewModel
import com.pbt.cogni.util.AppConstant.PREF_FIREBASE_TOKEN
import com.pbt.cogni.util.AppConstant.PREF_IS_LOGIN
import com.pbt.cogni.util.AppConstant.PREF_USER
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import es.dmoral.toasty.Toasty

class SplashActivity : AppCompatActivity() {
    var viewModel: TokenViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


//                || MyPreferencesHelper.getStringValue(this, AppConstant.PREF_IS_LOGIN)).equals( "")){

        Handler().postDelayed({

            AppUtils.logDebug(
                "SplashACtivity",
                "Check login Preff ==>> " + MyPreferencesHelper.getStringValue(
                    this@SplashActivity,
                    PREF_USER,
                    ""
                )
            )

            val intent: Intent;
            if (MyPreferencesHelper.getStringValue(this@SplashActivity, PREF_IS_LOGIN, "")
                    .equals("") || MyPreferencesHelper.getStringValue(
                    this@SplashActivity,
                    PREF_IS_LOGIN,
                    ""
                ).equals("false")
            ){
                intent = Intent(this, LoginActivity::class.java)
           val string= MyPreferencesHelper.getStringValue(this,PREF_USER,"")
          val login=  MyPreferencesHelper.getStringValue(this,PREF_IS_LOGIN,"")
          val token=  MyPreferencesHelper.getStringValue(this, PREF_FIREBASE_TOKEN,"")
            Log.d(TAG,string+"\n"+login+"\n"+token)}
            else

                intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
            finish()

        }, 500)
    }
}


//hello test
//Navigation google map
//        var myResult = Gson().fromJson(json, Data::class.java)
//        var uri = "waypoints="
//
//        var k : Int  = 1
//        var count : Int  = 0
//        var size : Int = myResult.mydata!!.size / 22;
//
//        myResult.mydata?.forEach {
//            if (k == 1) {
//                count++;
//                val latitude = it.lat
//                val longitude = it.lng
//                uri =  uri+latitude+","+longitude+"|"
//            }
//            if (size == k) {
//                k = 0;
//            }
//            k++
//        }
//
//        val navigation = Intent(
//            Intent.ACTION_VIEW,
//            Uri.parse(
//                "google.navigation:q=" + "23.577571,72.352010"+"&"+uri.dropLast(1))
//        )
//        navigation.setPackage("com.google.android.apps.maps")
//        startActivity(navigation)