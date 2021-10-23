package com.pbt.cogni.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pbt.cogni.R
import com.pbt.cogni.WebService.ApiClient
import com.pbt.cogni.WebService.ApiInterface
import com.pbt.cogni.activity.home.MainActivity
import com.pbt.cogni.activity.login.LoginActivity
import com.pbt.cogni.model.HttpResponse
import com.pbt.cogni.model.UserDetailsData
import com.pbt.cogni.util.AppConstant.Companion.PREF_IS_LOGIN
import com.pbt.cogni.util.AppConstant.Companion.PREF_TOKEN
import com.pbt.cogni.util.AppUtils
import com.pbt.cogni.util.MyPreferencesHelper
import retrofit2.Call
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        AppUtils.logDebug(
            "SplashACtivity",
            "Check login tokem ==>> " + MyPreferencesHelper.getStringTokenValue(
                this@SplashActivity,
                PREF_TOKEN,
                ""
            )
        )

        AppUtils.logDebug(
            "SplashACtivity",
            "Check login tokem ==>> " + MyPreferencesHelper.getStringValue(
                this@SplashActivity,
                PREF_IS_LOGIN,
                ""
            )
        )


        Handler().postDelayed({



            token = MyPreferencesHelper.getStringTokenValue(applicationContext, PREF_TOKEN, "")


            val intent: Intent
            if (MyPreferencesHelper.getStringValue(this@SplashActivity, PREF_IS_LOGIN, "").equals("") || MyPreferencesHelper.getStringValue(this@SplashActivity, PREF_IS_LOGIN, "").equals("false")) {
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                if (token != "")
                    callAPi()
                else
                    AppUtils.logDebug("##Splash","token empty : "+token)
            }
        }, 5000)
    }

    fun callAPi() {

        val userdataDetails: UserDetailsData? = MyPreferencesHelper.getUser(this)

        Log.e("##id", userdataDetails?.id + "\n" + token)

        val apiclient = ApiClient.getClient()
        val apiInterface = apiclient?.create(ApiInterface::class.java)
        val call = apiInterface?.updateToken(userdataDetails!!.id, token!!)

        call?.enqueue(object : retrofit2.Callback<HttpResponse> {
            override fun onResponse(
                call: Call<HttpResponse>,
                response: Response<HttpResponse>
            ) {
                Log.e("##apiSuccess", response.body()?.code.toString())
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<HttpResponse>, t: Throwable) {
                AppUtils.logError("##Splash"," server Error: "+t.message)
            }

        })
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